package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.AlchemyApp;
import com.dfire.platform.alchemy.domain.Sink;
import com.dfire.platform.alchemy.domain.Business;
import com.dfire.platform.alchemy.repository.SinkRepository;
import com.dfire.platform.alchemy.service.SinkService;
import com.dfire.platform.alchemy.service.dto.SinkDTO;
import com.dfire.platform.alchemy.service.mapper.SinkMapper;
import com.dfire.platform.alchemy.web.rest.errors.ExceptionTranslator;
import com.dfire.platform.alchemy.service.dto.SinkCriteria;
import com.dfire.platform.alchemy.service.SinkQueryService;

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

import com.dfire.platform.alchemy.domain.enumeration.SinkType;
/**
 * Integration tests for the {@Link SinkResource} REST controller.
 */
@SpringBootTest(classes = AlchemyApp.class)
public class SinkResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final SinkType DEFAULT_TYPE = SinkType.REDIS;
    private static final SinkType UPDATED_TYPE = SinkType.KAFKA010;

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
    private SinkRepository sinkRepository;

    @Autowired
    private SinkMapper sinkMapper;

    @Autowired
    private SinkService sinkService;

    @Autowired
    private SinkQueryService sinkQueryService;

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

    private MockMvc restSinkMockMvc;

    private Sink sink;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SinkResource sinkResource = new SinkResource(sinkService, sinkQueryService);
        this.restSinkMockMvc = MockMvcBuilders.standaloneSetup(sinkResource)
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
    public static Sink createEntity(EntityManager em) {
        Sink sink = new Sink()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .config(DEFAULT_CONFIG)
            .remark(DEFAULT_REMARK)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return sink;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sink createUpdatedEntity(EntityManager em) {
        Sink sink = new Sink()
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .config(UPDATED_CONFIG)
            .remark(UPDATED_REMARK)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return sink;
    }

    @BeforeEach
    public void initTest() {
        sink = createEntity(em);
    }

    @Test
    @Transactional
    public void createSink() throws Exception {
        int databaseSizeBeforeCreate = sinkRepository.findAll().size();

        // Create the Sink
        SinkDTO sinkDTO = sinkMapper.toDto(sink);
        restSinkMockMvc.perform(post("/api/sinks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sinkDTO)))
            .andExpect(status().isCreated());

        // Validate the Sink in the database
        List<Sink> sinkList = sinkRepository.findAll();
        assertThat(sinkList).hasSize(databaseSizeBeforeCreate + 1);
        Sink testSink = sinkList.get(sinkList.size() - 1);
        assertThat(testSink.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSink.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSink.getConfig()).isEqualTo(DEFAULT_CONFIG);
        assertThat(testSink.getRemark()).isEqualTo(DEFAULT_REMARK);
        assertThat(testSink.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testSink.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testSink.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testSink.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createSinkWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = sinkRepository.findAll().size();

        // Create the Sink with an existing ID
        sink.setId(1L);
        SinkDTO sinkDTO = sinkMapper.toDto(sink);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSinkMockMvc.perform(post("/api/sinks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sinkDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sink in the database
        List<Sink> sinkList = sinkRepository.findAll();
        assertThat(sinkList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sinkRepository.findAll().size();
        // set the field null
        sink.setType(null);

        // Create the Sink, which fails.
        SinkDTO sinkDTO = sinkMapper.toDto(sink);

        restSinkMockMvc.perform(post("/api/sinks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sinkDTO)))
            .andExpect(status().isBadRequest());

        List<Sink> sinkList = sinkRepository.findAll();
        assertThat(sinkList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRemarkIsRequired() throws Exception {
        int databaseSizeBeforeTest = sinkRepository.findAll().size();
        // set the field null
        sink.setRemark(null);

        // Create the Sink, which fails.
        SinkDTO sinkDTO = sinkMapper.toDto(sink);

        restSinkMockMvc.perform(post("/api/sinks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sinkDTO)))
            .andExpect(status().isBadRequest());

        List<Sink> sinkList = sinkRepository.findAll();
        assertThat(sinkList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSinks() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList
        restSinkMockMvc.perform(get("/api/sinks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sink.getId().intValue())))
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
    public void getSink() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get the sink
        restSinkMockMvc.perform(get("/api/sinks/{id}", sink.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(sink.getId().intValue()))
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
    public void getAllSinksByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where name equals to DEFAULT_NAME
        defaultSinkShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the sinkList where name equals to UPDATED_NAME
        defaultSinkShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSinksByNameIsInShouldWork() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where name in DEFAULT_NAME or UPDATED_NAME
        defaultSinkShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the sinkList where name equals to UPDATED_NAME
        defaultSinkShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllSinksByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where name is not null
        defaultSinkShouldBeFound("name.specified=true");

        // Get all the sinkList where name is null
        defaultSinkShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllSinksByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where type equals to DEFAULT_TYPE
        defaultSinkShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the sinkList where type equals to UPDATED_TYPE
        defaultSinkShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllSinksByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultSinkShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the sinkList where type equals to UPDATED_TYPE
        defaultSinkShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllSinksByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where type is not null
        defaultSinkShouldBeFound("type.specified=true");

        // Get all the sinkList where type is null
        defaultSinkShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllSinksByRemarkIsEqualToSomething() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where remark equals to DEFAULT_REMARK
        defaultSinkShouldBeFound("remark.equals=" + DEFAULT_REMARK);

        // Get all the sinkList where remark equals to UPDATED_REMARK
        defaultSinkShouldNotBeFound("remark.equals=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    public void getAllSinksByRemarkIsInShouldWork() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where remark in DEFAULT_REMARK or UPDATED_REMARK
        defaultSinkShouldBeFound("remark.in=" + DEFAULT_REMARK + "," + UPDATED_REMARK);

        // Get all the sinkList where remark equals to UPDATED_REMARK
        defaultSinkShouldNotBeFound("remark.in=" + UPDATED_REMARK);
    }

    @Test
    @Transactional
    public void getAllSinksByRemarkIsNullOrNotNull() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where remark is not null
        defaultSinkShouldBeFound("remark.specified=true");

        // Get all the sinkList where remark is null
        defaultSinkShouldNotBeFound("remark.specified=false");
    }

    @Test
    @Transactional
    public void getAllSinksByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where createdBy equals to DEFAULT_CREATED_BY
        defaultSinkShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the sinkList where createdBy equals to UPDATED_CREATED_BY
        defaultSinkShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllSinksByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultSinkShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the sinkList where createdBy equals to UPDATED_CREATED_BY
        defaultSinkShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllSinksByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where createdBy is not null
        defaultSinkShouldBeFound("createdBy.specified=true");

        // Get all the sinkList where createdBy is null
        defaultSinkShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllSinksByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where createdDate equals to DEFAULT_CREATED_DATE
        defaultSinkShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the sinkList where createdDate equals to UPDATED_CREATED_DATE
        defaultSinkShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllSinksByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultSinkShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the sinkList where createdDate equals to UPDATED_CREATED_DATE
        defaultSinkShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllSinksByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where createdDate is not null
        defaultSinkShouldBeFound("createdDate.specified=true");

        // Get all the sinkList where createdDate is null
        defaultSinkShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllSinksByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultSinkShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the sinkList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultSinkShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllSinksByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultSinkShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the sinkList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultSinkShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllSinksByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where lastModifiedBy is not null
        defaultSinkShouldBeFound("lastModifiedBy.specified=true");

        // Get all the sinkList where lastModifiedBy is null
        defaultSinkShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllSinksByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultSinkShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the sinkList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultSinkShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllSinksByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultSinkShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the sinkList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultSinkShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllSinksByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        // Get all the sinkList where lastModifiedDate is not null
        defaultSinkShouldBeFound("lastModifiedDate.specified=true");

        // Get all the sinkList where lastModifiedDate is null
        defaultSinkShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllSinksByBusinessIsEqualToSomething() throws Exception {
        // Initialize the database
        Business business = BusinessResourceIT.createEntity(em);
        em.persist(business);
        em.flush();
        sink.setBusiness(business);
        sinkRepository.saveAndFlush(sink);
        Long businessId = business.getId();

        // Get all the sinkList where business equals to businessId
        defaultSinkShouldBeFound("businessId.equals=" + businessId);

        // Get all the sinkList where business equals to businessId + 1
        defaultSinkShouldNotBeFound("businessId.equals=" + (businessId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSinkShouldBeFound(String filter) throws Exception {
        restSinkMockMvc.perform(get("/api/sinks?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sink.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG.toString())))
            .andExpect(jsonPath("$.[*].remark").value(hasItem(DEFAULT_REMARK)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restSinkMockMvc.perform(get("/api/sinks/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSinkShouldNotBeFound(String filter) throws Exception {
        restSinkMockMvc.perform(get("/api/sinks?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSinkMockMvc.perform(get("/api/sinks/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingSink() throws Exception {
        // Get the sink
        restSinkMockMvc.perform(get("/api/sinks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSink() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        int databaseSizeBeforeUpdate = sinkRepository.findAll().size();

        // Update the sink
        Sink updatedSink = sinkRepository.findById(sink.getId()).get();
        // Disconnect from session so that the updates on updatedSink are not directly saved in db
        em.detach(updatedSink);
        updatedSink
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .config(UPDATED_CONFIG)
            .remark(UPDATED_REMARK)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        SinkDTO sinkDTO = sinkMapper.toDto(updatedSink);

        restSinkMockMvc.perform(put("/api/sinks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sinkDTO)))
            .andExpect(status().isOk());

        // Validate the Sink in the database
        List<Sink> sinkList = sinkRepository.findAll();
        assertThat(sinkList).hasSize(databaseSizeBeforeUpdate);
        Sink testSink = sinkList.get(sinkList.size() - 1);
        assertThat(testSink.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSink.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSink.getConfig()).isEqualTo(UPDATED_CONFIG);
        assertThat(testSink.getRemark()).isEqualTo(UPDATED_REMARK);
        assertThat(testSink.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testSink.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testSink.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testSink.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingSink() throws Exception {
        int databaseSizeBeforeUpdate = sinkRepository.findAll().size();

        // Create the Sink
        SinkDTO sinkDTO = sinkMapper.toDto(sink);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSinkMockMvc.perform(put("/api/sinks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sinkDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sink in the database
        List<Sink> sinkList = sinkRepository.findAll();
        assertThat(sinkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSink() throws Exception {
        // Initialize the database
        sinkRepository.saveAndFlush(sink);

        int databaseSizeBeforeDelete = sinkRepository.findAll().size();

        // Delete the sink
        restSinkMockMvc.perform(delete("/api/sinks/{id}", sink.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Sink> sinkList = sinkRepository.findAll();
        assertThat(sinkList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sink.class);
        Sink sink1 = new Sink();
        sink1.setId(1L);
        Sink sink2 = new Sink();
        sink2.setId(sink1.getId());
        assertThat(sink1).isEqualTo(sink2);
        sink2.setId(2L);
        assertThat(sink1).isNotEqualTo(sink2);
        sink1.setId(null);
        assertThat(sink1).isNotEqualTo(sink2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SinkDTO.class);
        SinkDTO sinkDTO1 = new SinkDTO();
        sinkDTO1.setId(1L);
        SinkDTO sinkDTO2 = new SinkDTO();
        assertThat(sinkDTO1).isNotEqualTo(sinkDTO2);
        sinkDTO2.setId(sinkDTO1.getId());
        assertThat(sinkDTO1).isEqualTo(sinkDTO2);
        sinkDTO2.setId(2L);
        assertThat(sinkDTO1).isNotEqualTo(sinkDTO2);
        sinkDTO1.setId(null);
        assertThat(sinkDTO1).isNotEqualTo(sinkDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(sinkMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(sinkMapper.fromId(null)).isNull();
    }
}
