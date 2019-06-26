package com.dfire.platform.alchemy.service;

import com.dfire.platform.alchemy.service.dto.SinkDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.dfire.platform.alchemy.domain.Sink}.
 */
public interface SinkService {

    /**
     * Save a sink.
     *
     * @param sinkDTO the entity to save.
     * @return the persisted entity.
     */
    SinkDTO save(SinkDTO sinkDTO);

    /**
     * Get all the sinks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SinkDTO> findAll(Pageable pageable);


    /**
     * Get the "id" sink.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SinkDTO> findOne(Long id);

    /**
     * Delete the "id" sink.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
