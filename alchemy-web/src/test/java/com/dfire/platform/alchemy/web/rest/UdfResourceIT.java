package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.AlchemyApp;
import com.dfire.platform.alchemy.domain.Udf;
import com.dfire.platform.alchemy.domain.Business;
import com.dfire.platform.alchemy.repository.UdfRepository;
import com.dfire.platform.alchemy.service.UdfService;
import com.dfire.platform.alchemy.service.dto.UdfDTO;
import com.dfire.platform.alchemy.service.mapper.UdfMapper;
import com.dfire.platform.alchemy.web.rest.errors.ExceptionTranslator;
import com.dfire.platform.alchemy.service.UdfQueryService;

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

import com.dfire.platform.alchemy.domain.enumeration.UdfType;
/**
 * Integration tests for the {@Link UdfResource} REST controller.
 */
@SpringBootTest(classes = AlchemyApp.class)
public class UdfResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final UdfType DEFAULT_TYPE = UdfType.DEPENDENCY;
    private static final UdfType UPDATED_TYPE = UdfType.CODE;

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_DEPENDENCY = "AAAAAAAAAA";
    private static final String UPDATED_DEPENDENCY = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_REMARK = "AAAAAAAAAA";
    private static final String UPDATED_REMARK = "BBBBBBBBBB";

    @Autowired
    private UdfRepository udfRepository;

    @Autowired
    private UdfMapper udfMapper;

    @Autowired
    private UdfService udfService;

    @Autowired
    private UdfQueryService udfQueryService;

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

    private MockMvc restUdfMockMvc;

    private Udf udf;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UdfResource udfResource = new UdfResource(udfService, udfQueryService);
        this.restUdfMockMvc = MockMvcBuilders.standaloneSetup(udfResource)
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
    public static Udf createEntity(EntityManager em) {
        Udf udf = new Udf()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .value(DEFAULT_VALUE)
            .dependency(DEFAULT_DEPENDENCY)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .remark(DEFAULT_REMARK);
        return udf;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Udf createUpdatedEntity(EntityManager em) {
        Udf udf = new Udf()
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .value(UPDATED_VALUE)
            .dependency(UPDATED_DEPENDENCY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .remark(UPDATED_REMARK);
        return udf;
    }

    @BeforeEach
    public void initTest() {
        udf = createEntity(em);
    }

    @Test
    @Transactional
    public void createUdf() throws Exception {
        int databaseSizeBeforeCreate = udfRepository.findAll().size();

        // Create the Udf
        UdfDTO udfDTO = udfMapper.toDto(udf);
        restUdfMockMvc.perform(post("/api/udfs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(udfDTO)))
            .andExpect(status().isCreated());

        // Validate the Udf in the database
        List<Udf> udfList = udfRepository.findAll();
        assertThat(udfList).hasSize(databaseSizeBeforeCreate + 1);
        Udf testUdf = udfList.get(udfList.size() - 1);
        assertThat(testUdf.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUdf.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testUdf.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testUdf.getDependency()).isEqualTo(DEFAULT_DEPENDENCY);
        assertThat(testUdf.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testUdf.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testUdf.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testUdf.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
        assertThat(testUdf.getRemark()).isEqualTo(DEFAULT_REMARK);
    }

    @Test
    @Transactional
    public void createUdfWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = udfRepository.findAll().size();

        // Create the Udf with an existing ID
        udf.setId(1L);
        UdfDTO udfDTO = udfMapper.toDto(udf);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUdfMockMvc.perform(post("/api/udfs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(udfDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Udf in the database
        List<Udf> udfList = udfRepository.findAll();
        assertThat(udfList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = udfRepository.findAll().size();
        // set the field null
        udf.setName(null);

        // Create the Udf, which fails.
        UdfDTO udfDTO = udfMapper.toDto(udf);

        restUdfMockMvc.perform(post("/api/udfs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(udfDTO)))
            .andExpect(status().isBadRequest());

        List<Udf> udfList = udfRepository.findAll();
        assertThat(udfList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = udfRepository.findAll().size();
        // set the field null
        udf.setType(null);

        // Create the Udf, which fails.
        UdfDTO udfDTO = udfMapper.toDto(udf);

        restUdfMockMvc.perform(post("/api/udfs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(udfDTO)))
            .andExpect(status().isBadRequest());

        List<Udf> udfList = udfRepository.findAll();
        assertThat(udfList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUdfs() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList
        restUdfMockMvc.perform(get("/api/udfs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(udf.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
            .andExpect(jsonPath("$.[*].dependency").value(hasItem(DEFAULT_DEPENDENCY.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK.toString())));
    }
    
    @Test
    @Transactional
    public void getUdf() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get the udf
        restUdfMockMvc.perform(get("/api/udfs/{id}", udf.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(udf.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE.toString()))
            .andExpect(jsonPath("$.dependency").value(DEFAULT_DEPENDENCY.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .andExpect(jsonPath("$.remark").value(DEFAULT_REMARK.toString()));
    }

    @Test
    @Transactional
    public void getAllUdfsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where name equals to DEFAULT_NAME
        defaultUdfShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the udfList where name equals to UPDATED_NAME
        defaultUdfShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllUdfsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where name in DEFAULT_NAME or UPDATED_NAME
        defaultUdfShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the udfList where name equals to UPDATED_NAME
        defaultUdfShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllUdfsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where name is not null
        defaultUdfShouldBeFound("name.specified=true");

        // Get all the udfList where name is null
        defaultUdfShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllUdfsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where type equals to DEFAULT_TYPE
        defaultUdfShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the udfList where type equals to UPDATED_TYPE
        defaultUdfShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllUdfsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultUdfShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the udfList where type equals to UPDATED_TYPE
        defaultUdfShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllUdfsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where type is not null
        defaultUdfShouldBeFound("type.specified=true");

        // Get all the udfList where type is null
        defaultUdfShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllUdfsByDependencyIsEqualToSomething() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where avg equals to DEFAULT_AVG
        defaultUdfShouldBeFound("dependency.equals=" + DEFAULT_DEPENDENCY);

        // Get all the udfList where avg equals to UPDATED_AVG
        defaultUdfShouldNotBeFound("dependency.equals=" + UPDATED_DEPENDENCY);
    }

    @Test
    @Transactional
    public void getAllUdfsByDependencyIsInShouldWork() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where avg in DEFAULT_AVG or UPDATED_AVG
        defaultUdfShouldBeFound("dependency.in=" + DEFAULT_DEPENDENCY + "," + UPDATED_DEPENDENCY);

        // Get all the udfList where avg equals to UPDATED_AVG
        defaultUdfShouldNotBeFound("dependency.in=" + UPDATED_DEPENDENCY);
    }

    @Test
    @Transactional
    public void getAllUdfsByDependencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where avg is not null
        defaultUdfShouldBeFound("dependency.specified=true");

        // Get all the udfList where avg is null
        defaultUdfShouldNotBeFound("dependency.specified=false");
    }

    @Test
    @Transactional
    public void getAllUdfsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where createdBy equals to DEFAULT_CREATED_BY
        defaultUdfShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the udfList where createdBy equals to UPDATED_CREATED_BY
        defaultUdfShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllUdfsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultUdfShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the udfList where createdBy equals to UPDATED_CREATED_BY
        defaultUdfShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllUdfsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where createdBy is not null
        defaultUdfShouldBeFound("createdBy.specified=true");

        // Get all the udfList where createdBy is null
        defaultUdfShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllUdfsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where createdDate equals to DEFAULT_CREATED_DATE
        defaultUdfShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the udfList where createdDate equals to UPDATED_CREATED_DATE
        defaultUdfShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllUdfsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultUdfShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the udfList where createdDate equals to UPDATED_CREATED_DATE
        defaultUdfShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllUdfsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where createdDate is not null
        defaultUdfShouldBeFound("createdDate.specified=true");

        // Get all the udfList where createdDate is null
        defaultUdfShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllUdfsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultUdfShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the udfList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultUdfShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllUdfsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultUdfShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the udfList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultUdfShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllUdfsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where lastModifiedBy is not null
        defaultUdfShouldBeFound("lastModifiedBy.specified=true");

        // Get all the udfList where lastModifiedBy is null
        defaultUdfShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllUdfsByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultUdfShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the udfList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultUdfShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllUdfsByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultUdfShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the udfList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultUdfShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllUdfsByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where lastModifiedDate is not null
        defaultUdfShouldBeFound("lastModifiedDate.specified=true");

        // Get all the udfList where lastModifiedDate is null
        defaultUdfShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllUdfsByRemarkIsEqualToSomething() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where remark equals to DEFAULT_REMARK
        defaultUdfShouldBeFound("remark.equals=" + DEFAULT_REMARK);

        // Get all the udfList where remark equals to UPDATED_REMARK
        defaultUdfShouldNotBeFound("remark.equals=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    public void getAllUdfsByRemarkIsInShouldWork() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where remark in DEFAULT_REMARK or UPDATED_REMARK
        defaultUdfShouldBeFound("remark.in=" + DEFAULT_REMARK + "," + UPDATED_REMARK);

        // Get all the udfList where remark equals to UPDATED_REMARK
        defaultUdfShouldNotBeFound("remark.in=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    public void getAllUdfsByRemarkIsNullOrNotNull() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        // Get all the udfList where remark is not null
        defaultUdfShouldBeFound("remark.specified=true");

        // Get all the udfList where remark is null
        defaultUdfShouldNotBeFound("remark.specified=false");
    }

    @Test
    @Transactional
    public void getAllUdfsByBusinessIsEqualToSomething() throws Exception {
        // Initialize the database
        Business business = BusinessResourceIT.createEntity(em);
        em.persist(business);
        em.flush();
        udf.setBusiness(business);
        udfRepository.saveAndFlush(udf);
        Long businessId = business.getId();

        // Get all the udfList where business equals to businessId
        defaultUdfShouldBeFound("businessId.equals=" + businessId);

        // Get all the udfList where business equals to businessId + 1
        defaultUdfShouldNotBeFound("businessId.equals=" + (businessId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUdfShouldBeFound(String filter) throws Exception {
        restUdfMockMvc.perform(get("/api/udfs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(udf.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE.toString())))
            .andExpect(jsonPath("$.[*].avg").value(hasItem(DEFAULT_DEPENDENCY)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK)));

        // Check, that the count call also returns 1
        restUdfMockMvc.perform(get("/api/udfs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUdfShouldNotBeFound(String filter) throws Exception {
        restUdfMockMvc.perform(get("/api/udfs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUdfMockMvc.perform(get("/api/udfs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingUdf() throws Exception {
        // Get the udf
        restUdfMockMvc.perform(get("/api/udfs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUdf() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        int databaseSizeBeforeUpdate = udfRepository.findAll().size();

        // Update the udf
        Udf updatedUdf = udfRepository.findById(udf.getId()).get();
        // Disconnect from session so that the updates on updatedUdf are not directly saved in db
        em.detach(updatedUdf);
        updatedUdf
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .value(UPDATED_VALUE)
            .dependency(UPDATED_DEPENDENCY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .remark(UPDATED_REMARK);
        UdfDTO udfDTO = udfMapper.toDto(updatedUdf);

        restUdfMockMvc.perform(put("/api/udfs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(udfDTO)))
            .andExpect(status().isOk());

        // Validate the Udf in the database
        List<Udf> udfList = udfRepository.findAll();
        assertThat(udfList).hasSize(databaseSizeBeforeUpdate);
        Udf testUdf = udfList.get(udfList.size() - 1);
        assertThat(testUdf.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUdf.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testUdf.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testUdf.getDependency()).isEqualTo(UPDATED_DEPENDENCY);
        assertThat(testUdf.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testUdf.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testUdf.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testUdf.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
        assertThat(testUdf.getRemark()).isEqualTo(UPDATED_REMARK);
    }

    @Test
    @Transactional
    public void updateNonExistingUdf() throws Exception {
        int databaseSizeBeforeUpdate = udfRepository.findAll().size();

        // Create the Udf
        UdfDTO udfDTO = udfMapper.toDto(udf);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUdfMockMvc.perform(put("/api/udfs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(udfDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Udf in the database
        List<Udf> udfList = udfRepository.findAll();
        assertThat(udfList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUdf() throws Exception {
        // Initialize the database
        udfRepository.saveAndFlush(udf);

        int databaseSizeBeforeDelete = udfRepository.findAll().size();

        // Delete the udf
        restUdfMockMvc.perform(delete("/api/udfs/{id}", udf.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Udf> udfList = udfRepository.findAll();
        assertThat(udfList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Udf.class);
        Udf udf1 = new Udf();
        udf1.setId(1L);
        Udf udf2 = new Udf();
        udf2.setId(udf1.getId());
        assertThat(udf1).isEqualTo(udf2);
        udf2.setId(2L);
        assertThat(udf1).isNotEqualTo(udf2);
        udf1.setId(null);
        assertThat(udf1).isNotEqualTo(udf2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UdfDTO.class);
        UdfDTO udfDTO1 = new UdfDTO();
        udfDTO1.setId(1L);
        UdfDTO udfDTO2 = new UdfDTO();
        assertThat(udfDTO1).isNotEqualTo(udfDTO2);
        udfDTO2.setId(udfDTO1.getId());
        assertThat(udfDTO1).isEqualTo(udfDTO2);
        udfDTO2.setId(2L);
        assertThat(udfDTO1).isNotEqualTo(udfDTO2);
        udfDTO1.setId(null);
        assertThat(udfDTO1).isNotEqualTo(udfDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(udfMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(udfMapper.fromId(null)).isNull();
    }
}
