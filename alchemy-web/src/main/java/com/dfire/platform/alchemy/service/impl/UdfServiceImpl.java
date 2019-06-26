package com.dfire.platform.alchemy.service.impl;

import com.dfire.platform.alchemy.service.UdfService;
import com.dfire.platform.alchemy.domain.Udf;
import com.dfire.platform.alchemy.repository.UdfRepository;
import com.dfire.platform.alchemy.service.dto.UdfDTO;
import com.dfire.platform.alchemy.service.mapper.UdfMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Udf}.
 */
@Service
@Transactional
public class UdfServiceImpl implements UdfService {

    private final Logger log = LoggerFactory.getLogger(UdfServiceImpl.class);

    private final UdfRepository udfRepository;

    private final UdfMapper udfMapper;

    public UdfServiceImpl(UdfRepository udfRepository, UdfMapper udfMapper) {
        this.udfRepository = udfRepository;
        this.udfMapper = udfMapper;
    }

    /**
     * Save a udf.
     *
     * @param udfDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public UdfDTO save(UdfDTO udfDTO) {
        log.debug("Request to save Udf : {}", udfDTO);
        Udf udf = udfMapper.toEntity(udfDTO);
        udf = udfRepository.save(udf);
        return udfMapper.toDto(udf);
    }

    /**
     * Get all the udfs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UdfDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Udfs");
        return udfRepository.findAll(pageable)
            .map(udfMapper::toDto);
    }


    /**
     * Get one udf by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<UdfDTO> findOne(Long id) {
        log.debug("Request to get Udf : {}", id);
        return udfRepository.findById(id)
            .map(udfMapper::toDto);
    }

    /**
     * Delete the udf by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Udf : {}", id);
        udfRepository.deleteById(id);
    }
}
