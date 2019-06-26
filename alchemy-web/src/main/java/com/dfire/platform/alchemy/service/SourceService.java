package com.dfire.platform.alchemy.service;

import com.dfire.platform.alchemy.service.dto.SourceDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.dfire.platform.alchemy.domain.Source}.
 */
public interface SourceService {

    /**
     * Save a source.
     *
     * @param sourceDTO the entity to save.
     * @return the persisted entity.
     */
    SourceDTO save(SourceDTO sourceDTO);

    /**
     * Get all the sources.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SourceDTO> findAll(Pageable pageable);


    /**
     * Get the "id" source.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SourceDTO> findOne(Long id);

    /**
     * Delete the "id" source.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
