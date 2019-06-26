package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.AlchemyApp;
import com.dfire.platform.alchemy.domain.Job;
import com.dfire.platform.alchemy.domain.Business;
import com.dfire.platform.alchemy.domain.Cluster;
import com.dfire.platform.alchemy.domain.JobSql;
import com.dfire.platform.alchemy.repository.JobRepository;
import com.dfire.platform.alchemy.service.JobService;
import com.dfire.platform.alchemy.service.dto.JobDTO;
import com.dfire.platform.alchemy.service.mapper.JobMapper;
import com.dfire.platform.alchemy.web.rest.errors.ExceptionTranslator;
import com.dfire.platform.alchemy.service.dto.JobCriteria;
import com.dfire.platform.alchemy.service.JobQueryService;

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
import org.springframework.util.Base64Utils;
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

import com.dfire.platform.alchemy.domain.enumeration.JobType;
import com.dfire.platform.alchemy.domain.enumeration.JobStatus;
/**
 * Integration tests for the {@Link JobResource} REST controller.
 */
@SpringBootTest(classes = AlchemyApp.class)
public class JobResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final JobType DEFAULT_TYPE = JobType.JAR;
    private static final JobType UPDATED_TYPE = JobType.SQL;

    private static final String DEFAULT_CONFIG = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG = "BBBBBBBBBB";

    private static final String DEFAULT_REMARK = "AAAAAAAAAA";
    private static final String UPDATED_REMARK = "BBBBBBBBBB";

    private static final String DEFAULT_CLUSTER_JOB_ID = "AAAAAAAAAA";
    private static final String UPDATED_CLUSTER_JOB_ID = "BBBBBBBBBB";

    private static final JobStatus DEFAULT_STATUS = JobStatus.CREATE;
    private static final JobStatus UPDATED_STATUS = JobStatus.SUBMIT;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private JobService jobService;

    @Autowired
    private JobQueryService jobQueryService;

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

    private MockMvc restJobMockMvc;

    private Job job;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final JobResource jobResource = new JobResource(jobService, jobQueryService);
        this.restJobMockMvc = MockMvcBuilders.standaloneSetup(jobResource)
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
    public static Job createEntity(EntityManager em) {
        Job job = new Job()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .config(DEFAULT_CONFIG)
            .remark(DEFAULT_REMARK)
            .clusterJobId(DEFAULT_CLUSTER_JOB_ID)
            .status(DEFAULT_STATUS)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return job;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Job createUpdatedEntity(EntityManager em) {
        Job job = new Job()
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .config(UPDATED_CONFIG)
            .remark(UPDATED_REMARK)
            .clusterJobId(UPDATED_CLUSTER_JOB_ID)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return job;
    }

    @BeforeEach
    public void initTest() {
        job = createEntity(em);
    }

    @Test
    @Transactional
    public void createJob() throws Exception {
        int databaseSizeBeforeCreate = jobRepository.findAll().size();

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);
        restJobMockMvc.perform(post("/api/jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isCreated());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeCreate + 1);
        Job testJob = jobList.get(jobList.size() - 1);
        assertThat(testJob.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testJob.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testJob.getConfig()).isEqualTo(DEFAULT_CONFIG);
        assertThat(testJob.getRemark()).isEqualTo(DEFAULT_REMARK);
        assertThat(testJob.getClusterJobId()).isEqualTo(DEFAULT_CLUSTER_JOB_ID);
        assertThat(testJob.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testJob.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testJob.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testJob.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testJob.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createJobWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jobRepository.findAll().size();

        // Create the Job with an existing ID
        job.setId(1L);
        JobDTO jobDTO = jobMapper.toDto(job);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobMockMvc.perform(post("/api/jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobRepository.findAll().size();
        // set the field null
        job.setName(null);

        // Create the Job, which fails.
        JobDTO jobDTO = jobMapper.toDto(job);

        restJobMockMvc.perform(post("/api/jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isBadRequest());

        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobRepository.findAll().size();
        // set the field null
        job.setType(null);

        // Create the Job, which fails.
        JobDTO jobDTO = jobMapper.toDto(job);

        restJobMockMvc.perform(post("/api/jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isBadRequest());

        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRemarkIsRequired() throws Exception {
        int databaseSizeBeforeTest = jobRepository.findAll().size();
        // set the field null
        job.setRemark(null);

        // Create the Job, which fails.
        JobDTO jobDTO = jobMapper.toDto(job);

        restJobMockMvc.perform(post("/api/jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isBadRequest());

        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllJobs() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList
        restJobMockMvc.perform(get("/api/jobs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(job.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG.toString())))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK.toString())))
            .andExpect(jsonPath("$.[*].clusterJobId").value(hasItem(DEFAULT_CLUSTER_JOB_ID.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getJob() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get the job
        restJobMockMvc.perform(get("/api/jobs/{id}", job.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(job.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.config").value(DEFAULT_CONFIG.toString()))
            .andExpect(jsonPath("$.remark").value(DEFAULT_REMARK.toString()))
            .andExpect(jsonPath("$.clusterJobId").value(DEFAULT_CLUSTER_JOB_ID.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getAllJobsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where name equals to DEFAULT_NAME
        defaultJobShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the jobList where name equals to UPDATED_NAME
        defaultJobShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllJobsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where name in DEFAULT_NAME or UPDATED_NAME
        defaultJobShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the jobList where name equals to UPDATED_NAME
        defaultJobShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllJobsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where name is not null
        defaultJobShouldBeFound("name.specified=true");

        // Get all the jobList where name is null
        defaultJobShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where type equals to DEFAULT_TYPE
        defaultJobShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the jobList where type equals to UPDATED_TYPE
        defaultJobShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllJobsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultJobShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the jobList where type equals to UPDATED_TYPE
        defaultJobShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllJobsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where type is not null
        defaultJobShouldBeFound("type.specified=true");

        // Get all the jobList where type is null
        defaultJobShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByRemarkIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where remark equals to DEFAULT_REMARK
        defaultJobShouldBeFound("remark.equals=" + DEFAULT_REMARK);

        // Get all the jobList where remark equals to UPDATED_REMARK
        defaultJobShouldNotBeFound("remark.equals=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    public void getAllJobsByRemarkIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where remark in DEFAULT_REMARK or UPDATED_REMARK
        defaultJobShouldBeFound("remark.in=" + DEFAULT_REMARK + "," + UPDATED_REMARK);

        // Get all the jobList where remark equals to UPDATED_REMARK
        defaultJobShouldNotBeFound("remark.in=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    public void getAllJobsByRemarkIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where remark is not null
        defaultJobShouldBeFound("remark.specified=true");

        // Get all the jobList where remark is null
        defaultJobShouldNotBeFound("remark.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByClusterJobIdIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where clusterJobId equals to DEFAULT_CLUSTER_JOB_ID
        defaultJobShouldBeFound("clusterJobId.equals=" + DEFAULT_CLUSTER_JOB_ID);

        // Get all the jobList where clusterJobId equals to UPDATED_CLUSTER_JOB_ID
        defaultJobShouldNotBeFound("clusterJobId.equals=" + UPDATED_CLUSTER_JOB_ID);
    }

    @Test
    @Transactional
    public void getAllJobsByClusterJobIdIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where clusterJobId in DEFAULT_CLUSTER_JOB_ID or UPDATED_CLUSTER_JOB_ID
        defaultJobShouldBeFound("clusterJobId.in=" + DEFAULT_CLUSTER_JOB_ID + "," + UPDATED_CLUSTER_JOB_ID);

        // Get all the jobList where clusterJobId equals to UPDATED_CLUSTER_JOB_ID
        defaultJobShouldNotBeFound("clusterJobId.in=" + UPDATED_CLUSTER_JOB_ID);
    }

    @Test
    @Transactional
    public void getAllJobsByClusterJobIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where clusterJobId is not null
        defaultJobShouldBeFound("clusterJobId.specified=true");

        // Get all the jobList where clusterJobId is null
        defaultJobShouldNotBeFound("clusterJobId.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where status equals to DEFAULT_STATUS
        defaultJobShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the jobList where status equals to UPDATED_STATUS
        defaultJobShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllJobsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultJobShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the jobList where status equals to UPDATED_STATUS
        defaultJobShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void getAllJobsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where status is not null
        defaultJobShouldBeFound("status.specified=true");

        // Get all the jobList where status is null
        defaultJobShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where createdBy equals to DEFAULT_CREATED_BY
        defaultJobShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the jobList where createdBy equals to UPDATED_CREATED_BY
        defaultJobShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllJobsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultJobShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the jobList where createdBy equals to UPDATED_CREATED_BY
        defaultJobShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllJobsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where createdBy is not null
        defaultJobShouldBeFound("createdBy.specified=true");

        // Get all the jobList where createdBy is null
        defaultJobShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where createdDate equals to DEFAULT_CREATED_DATE
        defaultJobShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the jobList where createdDate equals to UPDATED_CREATED_DATE
        defaultJobShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultJobShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the jobList where createdDate equals to UPDATED_CREATED_DATE
        defaultJobShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where createdDate is not null
        defaultJobShouldBeFound("createdDate.specified=true");

        // Get all the jobList where createdDate is null
        defaultJobShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultJobShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the jobList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultJobShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllJobsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultJobShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the jobList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultJobShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllJobsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where lastModifiedBy is not null
        defaultJobShouldBeFound("lastModifiedBy.specified=true");

        // Get all the jobList where lastModifiedBy is null
        defaultJobShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultJobShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the jobList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultJobShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultJobShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the jobList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultJobShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllJobsByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        // Get all the jobList where lastModifiedDate is not null
        defaultJobShouldBeFound("lastModifiedDate.specified=true");

        // Get all the jobList where lastModifiedDate is null
        defaultJobShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobsByBusinessIsEqualToSomething() throws Exception {
        // Initialize the database
        Business business = BusinessResourceIT.createEntity(em);
        em.persist(business);
        em.flush();
        job.setBusiness(business);
        jobRepository.saveAndFlush(job);
        Long businessId = business.getId();

        // Get all the jobList where business equals to businessId
        defaultJobShouldBeFound("businessId.equals=" + businessId);

        // Get all the jobList where business equals to businessId + 1
        defaultJobShouldNotBeFound("businessId.equals=" + (businessId + 1));
    }


    @Test
    @Transactional
    public void getAllJobsByClusterIsEqualToSomething() throws Exception {
        // Initialize the database
        Cluster cluster = ClusterResourceIT.createEntity(em);
        em.persist(cluster);
        em.flush();
        job.setCluster(cluster);
        jobRepository.saveAndFlush(job);
        Long clusterId = cluster.getId();

        // Get all the jobList where cluster equals to clusterId
        defaultJobShouldBeFound("clusterId.equals=" + clusterId);

        // Get all the jobList where cluster equals to clusterId + 1
        defaultJobShouldNotBeFound("clusterId.equals=" + (clusterId + 1));
    }


    @Test
    @Transactional
    public void getAllJobsBySqlIsEqualToSomething() throws Exception {
        // Initialize the database
        JobSql sql = JobSqlResourceIT.createEntity(em);
        em.persist(sql);
        em.flush();
        job.addSql(sql);
        jobRepository.saveAndFlush(job);
        Long sqlId = sql.getId();

        // Get all the jobList where sql equals to sqlId
        defaultJobShouldBeFound("sqlId.equals=" + sqlId);

        // Get all the jobList where sql equals to sqlId + 1
        defaultJobShouldNotBeFound("sqlId.equals=" + (sqlId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJobShouldBeFound(String filter) throws Exception {
        restJobMockMvc.perform(get("/api/jobs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(job.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG.toString())))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK)))
            .andExpect(jsonPath("$.[*].clusterJobId").value(hasItem(DEFAULT_CLUSTER_JOB_ID)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restJobMockMvc.perform(get("/api/jobs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultJobShouldNotBeFound(String filter) throws Exception {
        restJobMockMvc.perform(get("/api/jobs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restJobMockMvc.perform(get("/api/jobs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingJob() throws Exception {
        // Get the job
        restJobMockMvc.perform(get("/api/jobs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJob() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        int databaseSizeBeforeUpdate = jobRepository.findAll().size();

        // Update the job
        Job updatedJob = jobRepository.findById(job.getId()).get();
        // Disconnect from session so that the updates on updatedJob are not directly saved in db
        em.detach(updatedJob);
        updatedJob
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .config(UPDATED_CONFIG)
            .remark(UPDATED_REMARK)
            .clusterJobId(UPDATED_CLUSTER_JOB_ID)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        JobDTO jobDTO = jobMapper.toDto(updatedJob);

        restJobMockMvc.perform(put("/api/jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isOk());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
        Job testJob = jobList.get(jobList.size() - 1);
        assertThat(testJob.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testJob.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testJob.getConfig()).isEqualTo(UPDATED_CONFIG);
        assertThat(testJob.getRemark()).isEqualTo(UPDATED_REMARK);
        assertThat(testJob.getClusterJobId()).isEqualTo(UPDATED_CLUSTER_JOB_ID);
        assertThat(testJob.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testJob.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testJob.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testJob.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testJob.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingJob() throws Exception {
        int databaseSizeBeforeUpdate = jobRepository.findAll().size();

        // Create the Job
        JobDTO jobDTO = jobMapper.toDto(job);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobMockMvc.perform(put("/api/jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Job in the database
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteJob() throws Exception {
        // Initialize the database
        jobRepository.saveAndFlush(job);

        int databaseSizeBeforeDelete = jobRepository.findAll().size();

        // Delete the job
        restJobMockMvc.perform(delete("/api/jobs/{id}", job.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Job> jobList = jobRepository.findAll();
        assertThat(jobList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Job.class);
        Job job1 = new Job();
        job1.setId(1L);
        Job job2 = new Job();
        job2.setId(job1.getId());
        assertThat(job1).isEqualTo(job2);
        job2.setId(2L);
        assertThat(job1).isNotEqualTo(job2);
        job1.setId(null);
        assertThat(job1).isNotEqualTo(job2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(JobDTO.class);
        JobDTO jobDTO1 = new JobDTO();
        jobDTO1.setId(1L);
        JobDTO jobDTO2 = new JobDTO();
        assertThat(jobDTO1).isNotEqualTo(jobDTO2);
        jobDTO2.setId(jobDTO1.getId());
        assertThat(jobDTO1).isEqualTo(jobDTO2);
        jobDTO2.setId(2L);
        assertThat(jobDTO1).isNotEqualTo(jobDTO2);
        jobDTO1.setId(null);
        assertThat(jobDTO1).isNotEqualTo(jobDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(jobMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(jobMapper.fromId(null)).isNull();
    }
}
