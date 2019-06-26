package com.dfire.platform.alchemy.service;

import com.dfire.platform.alchemy.service.dto.UdfDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.dfire.platform.alchemy.domain.Udf}.
 */
public interface UdfService {

    /**
     * Save a udf.
     *
     * @param udfDTO the entity to save.
     * @return the persisted entity.
     */
    UdfDTO save(UdfDTO udfDTO);

    /**
     * Get all the udfs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UdfDTO> findAll(Pageable pageable);


    /**
     * Get the "id" udf.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UdfDTO> findOne(Long id);

    /**
     * Delete the "id" udf.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
