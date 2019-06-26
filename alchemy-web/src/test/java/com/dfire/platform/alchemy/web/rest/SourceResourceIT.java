package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.AlchemyApp;
import com.dfire.platform.alchemy.domain.Source;
import com.dfire.platform.alchemy.domain.Business;
import com.dfire.platform.alchemy.repository.SourceRepository;
import com.dfire.platform.alchemy.service.SourceService;
import com.dfire.platform.alchemy.service.dto.SourceDTO;
import com.dfire.platform.alchemy.service.mapper.SourceMapper;
import com.dfire.platform.alchemy.web.rest.errors.ExceptionTranslator;
import com.dfire.platform.alchemy.service.dto.SourceCriteria;
import com.dfire.platform.alchemy.service.SourceQueryService;

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

import com.dfire.platform.alchemy.domain.enumeration.TableType;
import com.dfire.platform.alchemy.domain.enumeration.SourceType;
/**
 * Integration tests for the {@Link SourceResource} REST controller.
 */
@SpringBootTest(classes = AlchemyApp.class)
public class SourceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final TableType DEFAULT_TABLE_TYPE = TableType.TABLE;
    private static final TableType UPDATED_TABLE_TYPE = TableType.VIEW;

    private static final SourceType DEFAULT_SOURCE_TYPE = SourceType.KAFKA010;
    private static final SourceType UPDATED_SOURCE_TYPE = SourceType.MYSQL;

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
    private SourceRepository sourceRepository;

    @Autowired
    private SourceMapper sourceMapper;

    @Autowired
    private SourceService sourceService;

    @Autowired
    private SourceQueryService sourceQueryService;

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

    private MockMvc restSourceMockMvc;

    private Source source;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SourceResource sourceResource = new SourceResource(sourceService, sourceQueryService);
        this.restSourceMockMvc = MockMvcBuilders.standaloneSetup(sourceResource)
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
    public static Source createEntity(EntityManager em) {
        Source source = new Source()
            .name(DEFAULT_NAME)
            .tableType(DEFAULT_TABLE_TYPE)
            .sourceType(DEFAULT_SOURCE_TYPE)
            .config(DEFAULT_CONFIG)
            .remark(DEFAULT_REMARK)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return source;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Source createUpdatedEntity(EntityManager em) {
        Source source = new Source()
            .name(UPDATED_NAME)
            .tableType(UPDATED_TABLE_TYPE)
            .sourceType(UPDATED_SOURCE_TYPE)
            .config(UPDATED_CONFIG)
            .remark(UPDATED_REMARK)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return source;
    }

    @BeforeEach
    public void initTest() {
        source = createEntity(em);
    }

    @Test
    @Transactional
    public void createSource() throws Exception {
        int databaseSizeBeforeCreate = sourceRepository.findAll().size();

        // Create the Source
        SourceDTO sourceDTO = sourceMapper.toDto(source);
        restSourceMockMvc.perform(post("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isCreated());

        // Validate the Source in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeCreate + 1);
        Source testSource = sourceList.get(sourceList.size() - 1);
        assertThat(testSource.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSource.getTableType()).isEqualTo(DEFAULT_TABLE_TYPE);
        assertThat(testSource.getSourceType()).isEqualTo(DEFAULT_SOURCE_TYPE);
        assertThat(testSource.getConfig()).isEqualTo(DEFAULT_CONFIG);
        assertThat(testSource.getRemark()).isEqualTo(DEFAULT_REMARK);
        assertThat(testSource.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testSource.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testSource.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testSource.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createSourceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = sourceRepository.findAll().size();

        // Create the Source with an existing ID
        source.setId(1L);
        SourceDTO sourceDTO = sourceMapper.toDto(source);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSourceMockMvc.perform(post("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Source in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sourceRepository.findAll().size();
        // set the field null
        source.setName(null);

        // Create the Source, which fails.
        SourceDTO sourceDTO = sourceMapper.toDto(source);

        restSourceMockMvc.perform(post("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isBadRequest());

        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTableTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sourceRepository.findAll().size();
        // set the field null
        source.setTableType(null);

        // Create the Source, which fails.
        SourceDTO sourceDTO = sourceMapper.toDto(source);

        restSourceMockMvc.perform(post("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isBadRequest());

        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSourceTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sourceRepository.findAll().size();
        // set the field null
        source.setSourceType(null);

        // Create the Source, which fails.
        SourceDTO sourceDTO = sourceMapper.toDto(source);

        restSourceMockMvc.perform(post("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isBadRequest());

        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRemarkIsRequired() throws Exception {
        int databaseSizeBeforeTest = sourceRepository.findAll().size();
        // set the field null
        source.setRemark(null);

        // Create the Source, which fails.
        SourceDTO sourceDTO = sourceMapper.toDto(source);

        restSourceMockMvc.perform(post("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isBadRequest());

        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSources() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList
        restSourceMockMvc.perform(get("/api/sources?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(source.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].tableType").value(hasItem(DEFAULT_TABLE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].sourceType").value(hasItem(DEFAULT_SOURCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG.toString())))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getSource() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get the source
        restSourceMockMvc.perform(get("/api/sources/{id}", source.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(source.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.tableType").value(DEFAULT_TABLE_TYPE.toString()))
            .andExpect(jsonPath("$.sourceType").value(DEFAULT_SOURCE_TYPE.toString()))
            .andExpect(jsonPath("$.config").value(DEFAULT_CONFIG.toString()))
            .andExpect(jsonPath("$.remark").value(DEFAULT_REMARK.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getAllSourcesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where name equals to DEFAULT_NAME
        defaultSourceShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the sourceList where name equals to UPDATED_NAME
        defaultSourceShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSourcesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSourceShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the sourceList where name equals to UPDATED_NAME
        defaultSourceShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSourcesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where name is not null
        defaultSourceShouldBeFound("name.specified=true");

        // Get all the sourceList where name is null
        defaultSourceShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByTableTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where tableType equals to DEFAULT_TABLE_TYPE
        defaultSourceShouldBeFound("tableType.equals=" + DEFAULT_TABLE_TYPE);

        // Get all the sourceList where tableType equals to UPDATED_TABLE_TYPE
        defaultSourceShouldNotBeFound("tableType.equals=" + UPDATED_TABLE_TYPE);
    }

    @Test
    @Transactional
    public void getAllSourcesByTableTypeIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where tableType in DEFAULT_TABLE_TYPE or UPDATED_TABLE_TYPE
        defaultSourceShouldBeFound("tableType.in=" + DEFAULT_TABLE_TYPE + "," + UPDATED_TABLE_TYPE);

        // Get all the sourceList where tableType equals to UPDATED_TABLE_TYPE
        defaultSourceShouldNotBeFound("tableType.in=" + UPDATED_TABLE_TYPE);
    }

    @Test
    @Transactional
    public void getAllSourcesByTableTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where tableType is not null
        defaultSourceShouldBeFound("tableType.specified=true");

        // Get all the sourceList where tableType is null
        defaultSourceShouldNotBeFound("tableType.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesBySourceTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where sourceType equals to DEFAULT_SOURCE_TYPE
        defaultSourceShouldBeFound("sourceType.equals=" + DEFAULT_SOURCE_TYPE);

        // Get all the sourceList where sourceType equals to UPDATED_SOURCE_TYPE
        defaultSourceShouldNotBeFound("sourceType.equals=" + UPDATED_SOURCE_TYPE);
    }

    @Test
    @Transactional
    public void getAllSourcesBySourceTypeIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where sourceType in DEFAULT_SOURCE_TYPE or UPDATED_SOURCE_TYPE
        defaultSourceShouldBeFound("sourceType.in=" + DEFAULT_SOURCE_TYPE + "," + UPDATED_SOURCE_TYPE);

        // Get all the sourceList where sourceType equals to UPDATED_SOURCE_TYPE
        defaultSourceShouldNotBeFound("sourceType.in=" + UPDATED_SOURCE_TYPE);
    }

    @Test
    @Transactional
    public void getAllSourcesBySourceTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where sourceType is not null
        defaultSourceShouldBeFound("sourceType.specified=true");

        // Get all the sourceList where sourceType is null
        defaultSourceShouldNotBeFound("sourceType.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByRemarkIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where remark equals to DEFAULT_REMARK
        defaultSourceShouldBeFound("remark.equals=" + DEFAULT_REMARK);

        // Get all the sourceList where remark equals to UPDATED_REMARK
        defaultSourceShouldNotBeFound("remark.equals=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    public void getAllSourcesByRemarkIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where remark in DEFAULT_REMARK or UPDATED_REMARK
        defaultSourceShouldBeFound("remark.in=" + DEFAULT_REMARK + "," + UPDATED_REMARK);

        // Get all the sourceList where remark equals to UPDATED_REMARK
        defaultSourceShouldNotBeFound("remark.in=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    public void getAllSourcesByRemarkIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where remark is not null
        defaultSourceShouldBeFound("remark.specified=true");

        // Get all the sourceList where remark is null
        defaultSourceShouldNotBeFound("remark.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where createdBy equals to DEFAULT_CREATED_BY
        defaultSourceShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the sourceList where createdBy equals to UPDATED_CREATED_BY
        defaultSourceShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllSourcesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultSourceShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the sourceList where createdBy equals to UPDATED_CREATED_BY
        defaultSourceShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllSourcesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where createdBy is not null
        defaultSourceShouldBeFound("createdBy.specified=true");

        // Get all the sourceList where createdBy is null
        defaultSourceShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where createdDate equals to DEFAULT_CREATED_DATE
        defaultSourceShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the sourceList where createdDate equals to UPDATED_CREATED_DATE
        defaultSourceShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllSourcesByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultSourceShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the sourceList where createdDate equals to UPDATED_CREATED_DATE
        defaultSourceShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllSourcesByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where createdDate is not null
        defaultSourceShouldBeFound("createdDate.specified=true");

        // Get all the sourceList where createdDate is null
        defaultSourceShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultSourceShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the sourceList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultSourceShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllSourcesByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultSourceShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the sourceList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultSourceShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllSourcesByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where lastModifiedBy is not null
        defaultSourceShouldBeFound("lastModifiedBy.specified=true");

        // Get all the sourceList where lastModifiedBy is null
        defaultSourceShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultSourceShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the sourceList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultSourceShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllSourcesByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultSourceShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the sourceList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultSourceShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllSourcesByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where lastModifiedDate is not null
        defaultSourceShouldBeFound("lastModifiedDate.specified=true");

        // Get all the sourceList where lastModifiedDate is null
        defaultSourceShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByBusinessIsEqualToSomething() throws Exception {
        // Initialize the database
        Business business = BusinessResourceIT.createEntity(em);
        em.persist(business);
        em.flush();
        source.setBusiness(business);
        sourceRepository.saveAndFlush(source);
        Long businessId = business.getId();

        // Get all the sourceList where business equals to businessId
        defaultSourceShouldBeFound("businessId.equals=" + businessId);

        // Get all the sourceList where business equals to businessId + 1
        defaultSourceShouldNotBeFound("businessId.equals=" + (businessId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSourceShouldBeFound(String filter) throws Exception {
        restSourceMockMvc.perform(get("/api/sources?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(source.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].tableType").value(hasItem(DEFAULT_TABLE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].sourceType").value(hasItem(DEFAULT_SOURCE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG.toString())))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restSourceMockMvc.perform(get("/api/sources/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSourceShouldNotBeFound(String filter) throws Exception {
        restSourceMockMvc.perform(get("/api/sources?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSourceMockMvc.perform(get("/api/sources/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingSource() throws Exception {
        // Get the source
        restSourceMockMvc.perform(get("/api/sources/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSource() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        int databaseSizeBeforeUpdate = sourceRepository.findAll().size();

        // Update the source
        Source updatedSource = sourceRepository.findById(source.getId()).get();
        // Disconnect from session so that the updates on updatedSource are not directly saved in db
        em.detach(updatedSource);
        updatedSource
            .name(UPDATED_NAME)
            .tableType(UPDATED_TABLE_TYPE)
            .sourceType(UPDATED_SOURCE_TYPE)
            .config(UPDATED_CONFIG)
            .remark(UPDATED_REMARK)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        SourceDTO sourceDTO = sourceMapper.toDto(updatedSource);

        restSourceMockMvc.perform(put("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isOk());

        // Validate the Source in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeUpdate);
        Source testSource = sourceList.get(sourceList.size() - 1);
        assertThat(testSource.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSource.getTableType()).isEqualTo(UPDATED_TABLE_TYPE);
        assertThat(testSource.getSourceType()).isEqualTo(UPDATED_SOURCE_TYPE);
        assertThat(testSource.getConfig()).isEqualTo(UPDATED_CONFIG);
        assertThat(testSource.getRemark()).isEqualTo(UPDATED_REMARK);
        assertThat(testSource.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testSource.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testSource.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testSource.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingSource() throws Exception {
        int databaseSizeBeforeUpdate = sourceRepository.findAll().size();

        // Create the Source
        SourceDTO sourceDTO = sourceMapper.toDto(source);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSourceMockMvc.perform(put("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Source in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSource() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        int databaseSizeBeforeDelete = sourceRepository.findAll().size();

        // Delete the source
        restSourceMockMvc.perform(delete("/api/sources/{id}", source.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Source.class);
        Source source1 = new Source();
        source1.setId(1L);
        Source source2 = new Source();
        source2.setId(source1.getId());
        assertThat(source1).isEqualTo(source2);
        source2.setId(2L);
        assertThat(source1).isNotEqualTo(source2);
        source1.setId(null);
        assertThat(source1).isNotEqualTo(source2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SourceDTO.class);
        SourceDTO sourceDTO1 = new SourceDTO();
        sourceDTO1.setId(1L);
        SourceDTO sourceDTO2 = new SourceDTO();
        assertThat(sourceDTO1).isNotEqualTo(sourceDTO2);
        sourceDTO2.setId(sourceDTO1.getId());
        assertThat(sourceDTO1).isEqualTo(sourceDTO2);
        sourceDTO2.setId(2L);
        assertThat(sourceDTO1).isNotEqualTo(sourceDTO2);
        sourceDTO1.setId(null);
        assertThat(sourceDTO1).isNotEqualTo(sourceDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(sourceMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(sourceMapper.fromId(null)).isNull();
    }
}
