package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.AlchemyApp;
import com.dfire.platform.alchemy.domain.JobSql;
import com.dfire.platform.alchemy.domain.Job;
import com.dfire.platform.alchemy.repository.JobSqlRepository;
import com.dfire.platform.alchemy.service.JobSqlService;
import com.dfire.platform.alchemy.service.dto.JobSqlDTO;
import com.dfire.platform.alchemy.service.mapper.JobSqlMapper;
import com.dfire.platform.alchemy.web.rest.errors.ExceptionTranslator;
import com.dfire.platform.alchemy.service.dto.JobSqlCriteria;
import com.dfire.platform.alchemy.service.JobSqlQueryService;

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

/**
 * Integration tests for the {@Link JobSqlResource} REST controller.
 */
@SpringBootTest(classes = AlchemyApp.class)
public class JobSqlResourceIT {

    private static final String DEFAULT_SQL = "AAAAAAAAAA";
    private static final String UPDATED_SQL = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private JobSqlRepository jobSqlRepository;

    @Autowired
    private JobSqlMapper jobSqlMapper;

    @Autowired
    private JobSqlService jobSqlService;

    @Autowired
    private JobSqlQueryService jobSqlQueryService;

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

    private MockMvc restJobSqlMockMvc;

    private JobSql jobSql;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final JobSqlResource jobSqlResource = new JobSqlResource(jobSqlService, jobSqlQueryService);
        this.restJobSqlMockMvc = MockMvcBuilders.standaloneSetup(jobSqlResource)
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
    public static JobSql createEntity(EntityManager em) {
        JobSql jobSql = new JobSql()
            .sql(DEFAULT_SQL)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return jobSql;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JobSql createUpdatedEntity(EntityManager em) {
        JobSql jobSql = new JobSql()
            .sql(UPDATED_SQL)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return jobSql;
    }

    @BeforeEach
    public void initTest() {
        jobSql = createEntity(em);
    }

    @Test
    @Transactional
    public void createJobSql() throws Exception {
        int databaseSizeBeforeCreate = jobSqlRepository.findAll().size();

        // Create the JobSql
        JobSqlDTO jobSqlDTO = jobSqlMapper.toDto(jobSql);
        restJobSqlMockMvc.perform(post("/api/job-sqls")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobSqlDTO)))
            .andExpect(status().isCreated());

        // Validate the JobSql in the database
        List<JobSql> jobSqlList = jobSqlRepository.findAll();
        assertThat(jobSqlList).hasSize(databaseSizeBeforeCreate + 1);
        JobSql testJobSql = jobSqlList.get(jobSqlList.size() - 1);
        assertThat(testJobSql.getSql()).isEqualTo(DEFAULT_SQL);
        assertThat(testJobSql.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testJobSql.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testJobSql.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testJobSql.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createJobSqlWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jobSqlRepository.findAll().size();

        // Create the JobSql with an existing ID
        jobSql.setId(1L);
        JobSqlDTO jobSqlDTO = jobSqlMapper.toDto(jobSql);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJobSqlMockMvc.perform(post("/api/job-sqls")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobSqlDTO)))
            .andExpect(status().isBadRequest());

        // Validate the JobSql in the database
        List<JobSql> jobSqlList = jobSqlRepository.findAll();
        assertThat(jobSqlList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllJobSqls() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList
        restJobSqlMockMvc.perform(get("/api/job-sqls?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobSql.getId().intValue())))
            .andExpect(jsonPath("$.[*].sql").value(hasItem(DEFAULT_SQL.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getJobSql() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get the jobSql
        restJobSqlMockMvc.perform(get("/api/job-sqls/{id}", jobSql.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jobSql.getId().intValue()))
            .andExpect(jsonPath("$.sql").value(DEFAULT_SQL.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getAllJobSqlsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where createdBy equals to DEFAULT_CREATED_BY
        defaultJobSqlShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the jobSqlList where createdBy equals to UPDATED_CREATED_BY
        defaultJobSqlShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllJobSqlsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultJobSqlShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the jobSqlList where createdBy equals to UPDATED_CREATED_BY
        defaultJobSqlShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllJobSqlsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where createdBy is not null
        defaultJobSqlShouldBeFound("createdBy.specified=true");

        // Get all the jobSqlList where createdBy is null
        defaultJobSqlShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobSqlsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where createdDate equals to DEFAULT_CREATED_DATE
        defaultJobSqlShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the jobSqlList where createdDate equals to UPDATED_CREATED_DATE
        defaultJobSqlShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllJobSqlsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultJobSqlShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the jobSqlList where createdDate equals to UPDATED_CREATED_DATE
        defaultJobSqlShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllJobSqlsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where createdDate is not null
        defaultJobSqlShouldBeFound("createdDate.specified=true");

        // Get all the jobSqlList where createdDate is null
        defaultJobSqlShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobSqlsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultJobSqlShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the jobSqlList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultJobSqlShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllJobSqlsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultJobSqlShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the jobSqlList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultJobSqlShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllJobSqlsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where lastModifiedBy is not null
        defaultJobSqlShouldBeFound("lastModifiedBy.specified=true");

        // Get all the jobSqlList where lastModifiedBy is null
        defaultJobSqlShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobSqlsByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultJobSqlShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the jobSqlList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultJobSqlShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllJobSqlsByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultJobSqlShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the jobSqlList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultJobSqlShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllJobSqlsByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        // Get all the jobSqlList where lastModifiedDate is not null
        defaultJobSqlShouldBeFound("lastModifiedDate.specified=true");

        // Get all the jobSqlList where lastModifiedDate is null
        defaultJobSqlShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllJobSqlsByJobIsEqualToSomething() throws Exception {
        // Initialize the database
        Job job = JobResourceIT.createEntity(em);
        em.persist(job);
        em.flush();
        jobSql.setJob(job);
        jobSqlRepository.saveAndFlush(jobSql);
        Long jobId = job.getId();

        // Get all the jobSqlList where job equals to jobId
        defaultJobSqlShouldBeFound("jobId.equals=" + jobId);

        // Get all the jobSqlList where job equals to jobId + 1
        defaultJobSqlShouldNotBeFound("jobId.equals=" + (jobId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJobSqlShouldBeFound(String filter) throws Exception {
        restJobSqlMockMvc.perform(get("/api/job-sqls?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jobSql.getId().intValue())))
            .andExpect(jsonPath("$.[*].sql").value(hasItem(DEFAULT_SQL.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restJobSqlMockMvc.perform(get("/api/job-sqls/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultJobSqlShouldNotBeFound(String filter) throws Exception {
        restJobSqlMockMvc.perform(get("/api/job-sqls?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restJobSqlMockMvc.perform(get("/api/job-sqls/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingJobSql() throws Exception {
        // Get the jobSql
        restJobSqlMockMvc.perform(get("/api/job-sqls/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJobSql() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        int databaseSizeBeforeUpdate = jobSqlRepository.findAll().size();

        // Update the jobSql
        JobSql updatedJobSql = jobSqlRepository.findById(jobSql.getId()).get();
        // Disconnect from session so that the updates on updatedJobSql are not directly saved in db
        em.detach(updatedJobSql);
        updatedJobSql
            .sql(UPDATED_SQL)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        JobSqlDTO jobSqlDTO = jobSqlMapper.toDto(updatedJobSql);

        restJobSqlMockMvc.perform(put("/api/job-sqls")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobSqlDTO)))
            .andExpect(status().isOk());

        // Validate the JobSql in the database
        List<JobSql> jobSqlList = jobSqlRepository.findAll();
        assertThat(jobSqlList).hasSize(databaseSizeBeforeUpdate);
        JobSql testJobSql = jobSqlList.get(jobSqlList.size() - 1);
        assertThat(testJobSql.getSql()).isEqualTo(UPDATED_SQL);
        assertThat(testJobSql.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testJobSql.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testJobSql.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testJobSql.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingJobSql() throws Exception {
        int databaseSizeBeforeUpdate = jobSqlRepository.findAll().size();

        // Create the JobSql
        JobSqlDTO jobSqlDTO = jobSqlMapper.toDto(jobSql);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJobSqlMockMvc.perform(put("/api/job-sqls")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jobSqlDTO)))
            .andExpect(status().isBadRequest());

        // Validate the JobSql in the database
        List<JobSql> jobSqlList = jobSqlRepository.findAll();
        assertThat(jobSqlList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteJobSql() throws Exception {
        // Initialize the database
        jobSqlRepository.saveAndFlush(jobSql);

        int databaseSizeBeforeDelete = jobSqlRepository.findAll().size();

        // Delete the jobSql
        restJobSqlMockMvc.perform(delete("/api/job-sqls/{id}", jobSql.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<JobSql> jobSqlList = jobSqlRepository.findAll();
        assertThat(jobSqlList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JobSql.class);
        JobSql jobSql1 = new JobSql();
        jobSql1.setId(1L);
        JobSql jobSql2 = new JobSql();
        jobSql2.setId(jobSql1.getId());
        assertThat(jobSql1).isEqualTo(jobSql2);
        jobSql2.setId(2L);
        assertThat(jobSql1).isNotEqualTo(jobSql2);
        jobSql1.setId(null);
        assertThat(jobSql1).isNotEqualTo(jobSql2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(JobSqlDTO.class);
        JobSqlDTO jobSqlDTO1 = new JobSqlDTO();
        jobSqlDTO1.setId(1L);
        JobSqlDTO jobSqlDTO2 = new JobSqlDTO();
        assertThat(jobSqlDTO1).isNotEqualTo(jobSqlDTO2);
        jobSqlDTO2.setId(jobSqlDTO1.getId());
        assertThat(jobSqlDTO1).isEqualTo(jobSqlDTO2);
        jobSqlDTO2.setId(2L);
        assertThat(jobSqlDTO1).isNotEqualTo(jobSqlDTO2);
        jobSqlDTO1.setId(null);
        assertThat(jobSqlDTO1).isNotEqualTo(jobSqlDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(jobSqlMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(jobSqlMapper.fromId(null)).isNull();
    }
}
