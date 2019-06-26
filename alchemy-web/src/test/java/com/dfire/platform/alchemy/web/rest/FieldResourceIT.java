package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.AlchemyApp;
import com.dfire.platform.alchemy.domain.Field;
import com.dfire.platform.alchemy.domain.Source;
import com.dfire.platform.alchemy.repository.FieldRepository;
import com.dfire.platform.alchemy.service.FieldService;
import com.dfire.platform.alchemy.service.dto.FieldDTO;
import com.dfire.platform.alchemy.service.mapper.FieldMapper;
import com.dfire.platform.alchemy.web.rest.errors.ExceptionTranslator;
import com.dfire.platform.alchemy.service.dto.FieldCriteria;
import com.dfire.platform.alchemy.service.FieldQueryService;

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

/**
 * Integration tests for the {@Link FieldResource} REST controller.
 */
@SpringBootTest(classes = AlchemyApp.class)
public class FieldResourceIT {

    private static final String DEFAULT_COLUMN_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COLUMN_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COLUMN_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_COLUMN_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CONFIG = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private FieldMapper fieldMapper;

    @Autowired
    private FieldService fieldService;

    @Autowired
    private FieldQueryService fieldQueryService;

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

    private MockMvc restFieldMockMvc;

    private Field field;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final FieldResource fieldResource = new FieldResource(fieldService, fieldQueryService);
        this.restFieldMockMvc = MockMvcBuilders.standaloneSetup(fieldResource)
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
    public static Field createEntity(EntityManager em) {
        Field field = new Field()
            .columnName(DEFAULT_COLUMN_NAME)
            .columnType(DEFAULT_COLUMN_TYPE)
            .config(DEFAULT_CONFIG)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return field;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Field createUpdatedEntity(EntityManager em) {
        Field field = new Field()
            .columnName(UPDATED_COLUMN_NAME)
            .columnType(UPDATED_COLUMN_TYPE)
            .config(UPDATED_CONFIG)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return field;
    }

    @BeforeEach
    public void initTest() {
        field = createEntity(em);
    }

    @Test
    @Transactional
    public void createField() throws Exception {
        int databaseSizeBeforeCreate = fieldRepository.findAll().size();

        // Create the Field
        FieldDTO fieldDTO = fieldMapper.toDto(field);
        restFieldMockMvc.perform(post("/api/fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fieldDTO)))
            .andExpect(status().isCreated());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeCreate + 1);
        Field testField = fieldList.get(fieldList.size() - 1);
        assertThat(testField.getColumnName()).isEqualTo(DEFAULT_COLUMN_NAME);
        assertThat(testField.getColumnType()).isEqualTo(DEFAULT_COLUMN_TYPE);
        assertThat(testField.getConfig()).isEqualTo(DEFAULT_CONFIG);
        assertThat(testField.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testField.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testField.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testField.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void createFieldWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = fieldRepository.findAll().size();

        // Create the Field with an existing ID
        field.setId(1L);
        FieldDTO fieldDTO = fieldMapper.toDto(field);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFieldMockMvc.perform(post("/api/fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fieldDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkColumnNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = fieldRepository.findAll().size();
        // set the field null
        field.setColumnName(null);

        // Create the Field, which fails.
        FieldDTO fieldDTO = fieldMapper.toDto(field);

        restFieldMockMvc.perform(post("/api/fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fieldDTO)))
            .andExpect(status().isBadRequest());

        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkColumnTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = fieldRepository.findAll().size();
        // set the field null
        field.setColumnType(null);

        // Create the Field, which fails.
        FieldDTO fieldDTO = fieldMapper.toDto(field);

        restFieldMockMvc.perform(post("/api/fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fieldDTO)))
            .andExpect(status().isBadRequest());

        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFields() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList
        restFieldMockMvc.perform(get("/api/fields?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(field.getId().intValue())))
            .andExpect(jsonPath("$.[*].columnName").value(hasItem(DEFAULT_COLUMN_NAME.toString())))
            .andExpect(jsonPath("$.[*].columnType").value(hasItem(DEFAULT_COLUMN_TYPE.toString())))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY.toString())))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getField() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get the field
        restFieldMockMvc.perform(get("/api/fields/{id}", field.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(field.getId().intValue()))
            .andExpect(jsonPath("$.columnName").value(DEFAULT_COLUMN_NAME.toString()))
            .andExpect(jsonPath("$.columnType").value(DEFAULT_COLUMN_TYPE.toString()))
            .andExpect(jsonPath("$.config").value(DEFAULT_CONFIG.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getAllFieldsByColumnNameIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where columnName equals to DEFAULT_COLUMN_NAME
        defaultFieldShouldBeFound("columnName.equals=" + DEFAULT_COLUMN_NAME);

        // Get all the fieldList where columnName equals to UPDATED_COLUMN_NAME
        defaultFieldShouldNotBeFound("columnName.equals=" + UPDATED_COLUMN_NAME);
    }

    @Test
    @Transactional
    public void getAllFieldsByColumnNameIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where columnName in DEFAULT_COLUMN_NAME or UPDATED_COLUMN_NAME
        defaultFieldShouldBeFound("columnName.in=" + DEFAULT_COLUMN_NAME + "," + UPDATED_COLUMN_NAME);

        // Get all the fieldList where columnName equals to UPDATED_COLUMN_NAME
        defaultFieldShouldNotBeFound("columnName.in=" + UPDATED_COLUMN_NAME);
    }

    @Test
    @Transactional
    public void getAllFieldsByColumnNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where columnName is not null
        defaultFieldShouldBeFound("columnName.specified=true");

        // Get all the fieldList where columnName is null
        defaultFieldShouldNotBeFound("columnName.specified=false");
    }

    @Test
    @Transactional
    public void getAllFieldsByColumnTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where columnType equals to DEFAULT_COLUMN_TYPE
        defaultFieldShouldBeFound("columnType.equals=" + DEFAULT_COLUMN_TYPE);

        // Get all the fieldList where columnType equals to UPDATED_COLUMN_TYPE
        defaultFieldShouldNotBeFound("columnType.equals=" + UPDATED_COLUMN_TYPE);
    }

    @Test
    @Transactional
    public void getAllFieldsByColumnTypeIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where columnType in DEFAULT_COLUMN_TYPE or UPDATED_COLUMN_TYPE
        defaultFieldShouldBeFound("columnType.in=" + DEFAULT_COLUMN_TYPE + "," + UPDATED_COLUMN_TYPE);

        // Get all the fieldList where columnType equals to UPDATED_COLUMN_TYPE
        defaultFieldShouldNotBeFound("columnType.in=" + UPDATED_COLUMN_TYPE);
    }

    @Test
    @Transactional
    public void getAllFieldsByColumnTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where columnType is not null
        defaultFieldShouldBeFound("columnType.specified=true");

        // Get all the fieldList where columnType is null
        defaultFieldShouldNotBeFound("columnType.specified=false");
    }

    @Test
    @Transactional
    public void getAllFieldsByConfigIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where config equals to DEFAULT_CONFIG
        defaultFieldShouldBeFound("config.equals=" + DEFAULT_CONFIG);

        // Get all the fieldList where config equals to UPDATED_CONFIG
        defaultFieldShouldNotBeFound("config.equals=" + UPDATED_CONFIG);
    }

    @Test
    @Transactional
    public void getAllFieldsByConfigIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where config in DEFAULT_CONFIG or UPDATED_CONFIG
        defaultFieldShouldBeFound("config.in=" + DEFAULT_CONFIG + "," + UPDATED_CONFIG);

        // Get all the fieldList where config equals to UPDATED_CONFIG
        defaultFieldShouldNotBeFound("config.in=" + UPDATED_CONFIG);
    }

    @Test
    @Transactional
    public void getAllFieldsByConfigIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where config is not null
        defaultFieldShouldBeFound("config.specified=true");

        // Get all the fieldList where config is null
        defaultFieldShouldNotBeFound("config.specified=false");
    }

    @Test
    @Transactional
    public void getAllFieldsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where createdBy equals to DEFAULT_CREATED_BY
        defaultFieldShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the fieldList where createdBy equals to UPDATED_CREATED_BY
        defaultFieldShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllFieldsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultFieldShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the fieldList where createdBy equals to UPDATED_CREATED_BY
        defaultFieldShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    public void getAllFieldsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where createdBy is not null
        defaultFieldShouldBeFound("createdBy.specified=true");

        // Get all the fieldList where createdBy is null
        defaultFieldShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllFieldsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where createdDate equals to DEFAULT_CREATED_DATE
        defaultFieldShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the fieldList where createdDate equals to UPDATED_CREATED_DATE
        defaultFieldShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllFieldsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultFieldShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the fieldList where createdDate equals to UPDATED_CREATED_DATE
        defaultFieldShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    public void getAllFieldsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where createdDate is not null
        defaultFieldShouldBeFound("createdDate.specified=true");

        // Get all the fieldList where createdDate is null
        defaultFieldShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllFieldsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultFieldShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the fieldList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultFieldShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllFieldsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultFieldShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the fieldList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultFieldShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    public void getAllFieldsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where lastModifiedBy is not null
        defaultFieldShouldBeFound("lastModifiedBy.specified=true");

        // Get all the fieldList where lastModifiedBy is null
        defaultFieldShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    public void getAllFieldsByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultFieldShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the fieldList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultFieldShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllFieldsByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultFieldShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the fieldList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultFieldShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void getAllFieldsByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        // Get all the fieldList where lastModifiedDate is not null
        defaultFieldShouldBeFound("lastModifiedDate.specified=true");

        // Get all the fieldList where lastModifiedDate is null
        defaultFieldShouldNotBeFound("lastModifiedDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllFieldsBySourceIsEqualToSomething() throws Exception {
        // Initialize the database
        Source source = SourceResourceIT.createEntity(em);
        em.persist(source);
        em.flush();
        field.setSource(source);
        fieldRepository.saveAndFlush(field);
        Long sourceId = source.getId();

        // Get all the fieldList where source equals to sourceId
        defaultFieldShouldBeFound("sourceId.equals=" + sourceId);

        // Get all the fieldList where source equals to sourceId + 1
        defaultFieldShouldNotBeFound("sourceId.equals=" + (sourceId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFieldShouldBeFound(String filter) throws Exception {
        restFieldMockMvc.perform(get("/api/fields?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(field.getId().intValue())))
            .andExpect(jsonPath("$.[*].columnName").value(hasItem(DEFAULT_COLUMN_NAME)))
            .andExpect(jsonPath("$.[*].columnType").value(hasItem(DEFAULT_COLUMN_TYPE)))
            .andExpect(jsonPath("$.[*].config").value(hasItem(DEFAULT_CONFIG)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restFieldMockMvc.perform(get("/api/fields/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFieldShouldNotBeFound(String filter) throws Exception {
        restFieldMockMvc.perform(get("/api/fields?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFieldMockMvc.perform(get("/api/fields/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingField() throws Exception {
        // Get the field
        restFieldMockMvc.perform(get("/api/fields/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateField() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        int databaseSizeBeforeUpdate = fieldRepository.findAll().size();

        // Update the field
        Field updatedField = fieldRepository.findById(field.getId()).get();
        // Disconnect from session so that the updates on updatedField are not directly saved in db
        em.detach(updatedField);
        updatedField
            .columnName(UPDATED_COLUMN_NAME)
            .columnType(UPDATED_COLUMN_TYPE)
            .config(UPDATED_CONFIG)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        FieldDTO fieldDTO = fieldMapper.toDto(updatedField);

        restFieldMockMvc.perform(put("/api/fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fieldDTO)))
            .andExpect(status().isOk());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeUpdate);
        Field testField = fieldList.get(fieldList.size() - 1);
        assertThat(testField.getColumnName()).isEqualTo(UPDATED_COLUMN_NAME);
        assertThat(testField.getColumnType()).isEqualTo(UPDATED_COLUMN_TYPE);
        assertThat(testField.getConfig()).isEqualTo(UPDATED_CONFIG);
        assertThat(testField.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testField.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testField.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testField.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingField() throws Exception {
        int databaseSizeBeforeUpdate = fieldRepository.findAll().size();

        // Create the Field
        FieldDTO fieldDTO = fieldMapper.toDto(field);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFieldMockMvc.perform(put("/api/fields")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(fieldDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Field in the database
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteField() throws Exception {
        // Initialize the database
        fieldRepository.saveAndFlush(field);

        int databaseSizeBeforeDelete = fieldRepository.findAll().size();

        // Delete the field
        restFieldMockMvc.perform(delete("/api/fields/{id}", field.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database is empty
        List<Field> fieldList = fieldRepository.findAll();
        assertThat(fieldList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Field.class);
        Field field1 = new Field();
        field1.setId(1L);
        Field field2 = new Field();
        field2.setId(field1.getId());
        assertThat(field1).isEqualTo(field2);
        field2.setId(2L);
        assertThat(field1).isNotEqualTo(field2);
        field1.setId(null);
        assertThat(field1).isNotEqualTo(field2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FieldDTO.class);
        FieldDTO fieldDTO1 = new FieldDTO();
        fieldDTO1.setId(1L);
        FieldDTO fieldDTO2 = new FieldDTO();
        assertThat(fieldDTO1).isNotEqualTo(fieldDTO2);
        fieldDTO2.setId(fieldDTO1.getId());
        assertThat(fieldDTO1).isEqualTo(fieldDTO2);
        fieldDTO2.setId(2L);
        assertThat(fieldDTO1).isNotEqualTo(fieldDTO2);
        fieldDTO1.setId(null);
        assertThat(fieldDTO1).isNotEqualTo(fieldDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(fieldMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(fieldMapper.fromId(null)).isNull();
    }
}
