package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.AlchemyApp;
import com.dfire.platform.alchemy.client.ClientManager;
import com.dfire.platform.alchemy.domain.Cluster;
import com.dfire.platform.alchemy.domain.Business;
import com.dfire.platform.alchemy.domain.Job;
import com.dfire.platform.alchemy.repository.ClusterRepository;
import com.dfire.platform.alchemy.service.ClusterService;
import com.dfire.platform.alchemy.service.dto.ClusterDTO;
import com.dfire.platform.alchemy.service.mapper.ClusterMapper;
import com.dfire.platform.alchemy.web.rest.errors.ExceptionTranslator;
import com.dfire.platform.alchemy.service.ClusterQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.dfire.platform.alchemy.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dfire.platform.alchemy.domain.enumeration.ClusterType;
/**
 * Integration tests for the {@Link ClusterResource} REST controller.
 */
@SpringBootTest(classes = AlchemyApp.class)
public class ClusterResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final ClusterType DEFAULT_TYPE = ClusterType.STANDALONE;
    private static final ClusterType UPDATED_TYPE = ClusterType.YARN;

    private static final String DEFAULT_CONFIG = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG = "BBBBBBBBBB";

    private static final String DEFAULT_REMARK = "AAAAAAAAAA";
    private static final String UPDATED_REMARK = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private ClusterMapper clusterMapper;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ClusterQueryService clusterQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    @Autowired
    private ClientManager clientManager;

    private MockMvc restClusterMockMvc;

    private Cluster cluster;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ClusterResource clusterResource = new ClusterResource(clusterService, clusterQueryService, clientManager);
        this.restClusterMockMvc = MockMvcBuilders.standaloneSetup(clusterResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cluster createEntity(EntityManager em) {
        Cluster cluster = new Cluster()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .config(DEFAULT_CONFIG)
            .remark(DEFAULT_REMARK)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return cluster;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cluster createUpdatedEntity(EntityManager em) {
        Cluster cluster = new Cluster()
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .config(UPDATED_CONFIG)
            .remark(UPDATED_REMARK)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return cluster;
    }

    @BeforeEach
    public void initTest() {
        cluster = createEntity(em);
    }

    @Test
    @Transactional
    public void createCluster() throws Exception {
        int databaseSizeBeforeCreate = clusterRepository.findAll().size();

        // Create the Cluster
        ClusterDTO clusterDTO = clusterMapper.toDto(cluster);
        restClusterMockMvc.perform(post("/api/clusters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clusterDTO)))
            .andExpect(status().isCreated());

        // Validate the Cluster in the database
        List<Cluster> clusterList = clusterRepository.findAll();
        assertThat(clusterList).hasSize(databaseSizeBeforeCreate + 1);
        Cluster testCluster = clusterList.get(clusterList.size() - 1);
        assertThat(testCluster.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCluster.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testCluster.getConfig()).isEqualTo(DEFAULT_CONFIG);
        assertThat(testCluster.getRemark()).isEqualTo(DEFAULT_REMARK);
        assertThat(testCluster.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testCluster.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testCluster.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testCluster.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createClusterWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = clusterRepository.findAll().size();

        // Create the Cluster with an existing ID
        cluster.setId(1L);
        ClusterDTO clusterDTO = clusterMapper.toDto(cluster);

        // An entity with an existing ID cannot be created, so this API call must fail
        restClusterMockMvc.perform(post("/api/clusters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clusterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Cluster in the database
        List<Cluster> clusterList = clusterRepository.findAll();
        assertThat(clusterList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = clusterRepository.findAll().size();
        // set the field null
        cluster.setName(null);

        // Create the Cluster, which fails.
        ClusterDTO clusterDTO = clusterMapper.toDto(cluster);

        restClusterMockMvc.perform(post("/api/clusters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clusterDTO)))
            .andExpect(status().isBadRequest());

        List<Cluster> clusterList = clusterRepository.findAll();
        assertThat(clusterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = clusterRepository.findAll().size();
        // set the field null
        cluster.setType(null);

        // Create the Cluster, which fails.
        ClusterDTO clusterDTO = clusterMapper.toDto(cluster);

        restClusterMockMvc.perform(post("/api/clusters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clusterDTO)))
            .andExpect(status().isBadRequest());

        List<Cluster> clusterList = clusterRepository.findAll();
        assertThat(clusterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRemarkIsRequired() throws Exception {
        int databaseSizeBeforeTest = clusterRepository.findAll().size();
        // set the field null
        cluster.setRemark(null);

        // Create the Cluster, which fails.
        ClusterDTO clusterDTO = clusterMapper.toDto(cluster);

        restClusterMockMvc.perform(post("/api/clusters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clusterDTO)))
            .andExpect(status().isBadRequest());

        List<Cluster> clusterList = clusterRepository.findAll();
        assertThat(clusterList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllClusters() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList
        restClusterMockMvc.perform(get("/api/clusters?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cluster.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG.toString())))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getCluster() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get the cluster
        restClusterMockMvc.perform(get("/api/clusters/{id}", cluster.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(cluster.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.config").value(DEFAULT_CONFIG.toString()))
            .andExpect(jsonPath("$.remark").value(DEFAULT_REMARK.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getAllClustersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where name equals to DEFAULT_NAME
        defaultClusterShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the clusterList where name equals to UPDATED_NAME
        defaultClusterShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllClustersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where name in DEFAULT_NAME or UPDATED_NAME
        defaultClusterShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the clusterList where name equals to UPDATED_NAME
        defaultClusterShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllClustersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where name is not null
        defaultClusterShouldBeFound("name.specified=true");

        // Get all the clusterList where name is null
        defaultClusterShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllClustersByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where type equals to DEFAULT_TYPE
        defaultClusterShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the clusterList where type equals to UPDATED_TYPE
        defaultClusterShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllClustersByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultClusterShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the clusterList where type equals to UPDATED_TYPE
        defaultClusterShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllClustersByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where type is not null
        defaultClusterShouldBeFound("type.specified=true");

        // Get all the clusterList where type is null
        defaultClusterShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllClustersByRemarkIsEqualToSomething() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where remark equals to DEFAULT_REMARK
        defaultClusterShouldBeFound("remark.equals=" + DEFAULT_REMARK);

        // Get all the clusterList where remark equals to UPDATED_REMARK
        defaultClusterShouldNotBeFound("remark.equals=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    public void getAllClustersByRemarkIsInShouldWork() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where remark in DEFAULT_REMARK or UPDATED_REMARK
        defaultClusterShouldBeFound("remark.in=" + DEFAULT_REMARK + "," + UPDATED_REMARK);

        // Get all the clusterList where remark equals to UPDATED_REMARK
        defaultClusterShouldNotBeFound("remark.in=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    public void getAllClustersByRemarkIsNullOrNotNull() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where remark is not null
        defaultClusterShouldBeFound("remark.specified=true");

        // Get all the clusterList where remark is null
        defaultClusterShouldNotBeFound("remark.specified=false");
    }

    @Test
    @Transactional
    public void getAllClustersByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where createdBy equals to DEFAULT_CREATED_BY
        defaultClusterShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the clusterList where createdBy equals to UPDATED_CREATED_BY
        defaultClusterShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllClustersByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultClusterShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the clusterList where createdBy equals to UPDATED_CREATED_BY
        defaultClusterShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllClustersByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where createdBy is not null
        defaultClusterShouldBeFound("createdBy.specified=true");

        // Get all the clusterList where createdBy is null
        defaultClusterShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllClustersByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where createdDate equals to DEFAULT_CREATED_DATE
        defaultClusterShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the clusterList where createdDate equals to UPDATED_CREATED_DATE
        defaultClusterShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllClustersByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultClusterShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the clusterList where createdDate equals to UPDATED_CREATED_DATE
        defaultClusterShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllClustersByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where createdDate is not null
        defaultClusterShouldBeFound("createdDate.specified=true");

        // Get all the clusterList where createdDate is null
        defaultClusterShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllClustersByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultClusterShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the clusterList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultClusterShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllClustersByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultClusterShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the clusterList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultClusterShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllClustersByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where lastModifiedBy is not null
        defaultClusterShouldBeFound("lastModifiedBy.specified=true");

        // Get all the clusterList where lastModifiedBy is null
        defaultClusterShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllClustersByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultClusterShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the clusterList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultClusterShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllClustersByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultClusterShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the clusterList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultClusterShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllClustersByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        // Get all the clusterList where lastModifiedDate is not null
        defaultClusterShouldBeFound("lastModifiedDate.specified=true");

        // Get all the clusterList where lastModifiedDate is null
        defaultClusterShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllClustersByBusinessIsEqualToSomething() throws Exception {
        // Initialize the database
        Business business = BusinessResourceIT.createEntity(em);
        em.persist(business);
        em.flush();
        cluster.setBusiness(business);
        clusterRepository.saveAndFlush(cluster);
        Long businessId = business.getId();

        // Get all the clusterList where business equals to businessId
        defaultClusterShouldBeFound("businessId.equals=" + businessId);

        // Get all the clusterList where business equals to businessId + 1
        defaultClusterShouldNotBeFound("businessId.equals=" + (businessId + 1));
    }


    @Test
    @Transactional
    public void getAllClustersByJobIsEqualToSomething() throws Exception {
        // Initialize the database
        Job job = JobResourceIT.createEntity(em);
        em.persist(job);
        em.flush();
        cluster.addJob(job);
        clusterRepository.saveAndFlush(cluster);
        Long jobId = job.getId();

        // Get all the clusterList where job equals to jobId
        defaultClusterShouldBeFound("jobId.equals=" + jobId);

        // Get all the clusterList where job equals to jobId + 1
        defaultClusterShouldNotBeFound("jobId.equals=" + (jobId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClusterShouldBeFound(String filter) throws Exception {
        restClusterMockMvc.perform(get("/api/clusters?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cluster.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG.toString())))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restClusterMockMvc.perform(get("/api/clusters/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClusterShouldNotBeFound(String filter) throws Exception {
        restClusterMockMvc.perform(get("/api/clusters?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClusterMockMvc.perform(get("/api/clusters/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingCluster() throws Exception {
        // Get the cluster
        restClusterMockMvc.perform(get("/api/clusters/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCluster() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        int databaseSizeBeforeUpdate = clusterRepository.findAll().size();

        // Update the cluster
        Cluster updatedCluster = clusterRepository.findById(cluster.getId()).get();
        // Disconnect from session so that the updates on updatedCluster are not directly saved in db
        em.detach(updatedCluster);
        updatedCluster
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .config(UPDATED_CONFIG)
            .remark(UPDATED_REMARK)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        ClusterDTO clusterDTO = clusterMapper.toDto(updatedCluster);

        restClusterMockMvc.perform(put("/api/clusters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clusterDTO)))
            .andExpect(status().isOk());

        // Validate the Cluster in the database
        List<Cluster> clusterList = clusterRepository.findAll();
        assertThat(clusterList).hasSize(databaseSizeBeforeUpdate);
        Cluster testCluster = clusterList.get(clusterList.size() - 1);
        assertThat(testCluster.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCluster.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCluster.getConfig()).isEqualTo(UPDATED_CONFIG);
        assertThat(testCluster.getRemark()).isEqualTo(UPDATED_REMARK);
        assertThat(testCluster.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testCluster.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testCluster.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testCluster.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingCluster() throws Exception {
        int databaseSizeBeforeUpdate = clusterRepository.findAll().size();

        // Create the Cluster
        ClusterDTO clusterDTO = clusterMapper.toDto(cluster);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClusterMockMvc.perform(put("/api/clusters")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clusterDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Cluster in the database
        List<Cluster> clusterList = clusterRepository.findAll();
        assertThat(clusterList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCluster() throws Exception {
        // Initialize the database
        clusterRepository.saveAndFlush(cluster);

        int databaseSizeBeforeDelete = clusterRepository.findAll().size();

        // Delete the cluster
        restClusterMockMvc.perform(delete("/api/clusters/{id}", cluster.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Cluster> clusterList = clusterRepository.findAll();
        assertThat(clusterList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Cluster.class);
        Cluster cluster1 = new Cluster();
        cluster1.setId(1L);
        Cluster cluster2 = new Cluster();
        cluster2.setId(cluster1.getId());
        assertThat(cluster1).isEqualTo(cluster2);
        cluster2.setId(2L);
        assertThat(cluster1).isNotEqualTo(cluster2);
        cluster1.setId(null);
        assertThat(cluster1).isNotEqualTo(cluster2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClusterDTO.class);
        ClusterDTO clusterDTO1 = new ClusterDTO();
        clusterDTO1.setId(1L);
        ClusterDTO clusterDTO2 = new ClusterDTO();
        assertThat(clusterDTO1).isNotEqualTo(clusterDTO2);
        clusterDTO2.setId(clusterDTO1.getId());
        assertThat(clusterDTO1).isEqualTo(clusterDTO2);
        clusterDTO2.setId(2L);
        assertThat(clusterDTO1).isNotEqualTo(clusterDTO2);
        clusterDTO1.setId(null);
        assertThat(clusterDTO1).isNotEqualTo(clusterDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(clusterMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(clusterMapper.fromId(null)).isNull();
    }
}
