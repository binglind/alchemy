package com.dfire.platform.alchemy.service.impl;

import com.dfire.platform.alchemy.service.FieldService;
import com.dfire.platform.alchemy.domain.Field;
import com.dfire.platform.alchemy.repository.FieldRepository;
import com.dfire.platform.alchemy.service.dto.FieldDTO;
import com.dfire.platform.alchemy.service.mapper.FieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Field}.
 */
@Service
@Transactional
public class FieldServiceImpl implements FieldService {

    private final Logger log = LoggerFactory.getLogger(FieldServiceImpl.class);

    private final FieldRepository fieldRepository;

    private final FieldMapper fieldMapper;

    public FieldServiceImpl(FieldRepository fieldRepository, FieldMapper fieldMapper) {
        this.fieldRepository = fieldRepository;
        this.fieldMapper = fieldMapper;
    }

    /**
     * Save a field.
     *
     * @param fieldDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public FieldDTO save(FieldDTO fieldDTO) {
        log.debug("Request to save Field : {}", fieldDTO);
        Field field = fieldMapper.toEntity(fieldDTO);
        field = fieldRepository.save(field);
        return fieldMapper.toDto(field);
    }

    /**
     * Get all the fields.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FieldDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Fields");
        return fieldRepository.findAll(pageable)
            .map(fieldMapper::toDto);
    }


    /**
     * Get one field by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<FieldDTO> findOne(Long id) {
        log.debug("Request to get Field : {}", id);
        return fieldRepository.findById(id)
            .map(fieldMapper::toDto);
    }

    /**
     * Delete the field by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Field : {}", id);
        fieldRepository.deleteById(id);
    }
}
