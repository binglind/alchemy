package com.dfire.platform.alchemy.service;

import com.dfire.platform.alchemy.service.dto.ClusterDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.dfire.platform.alchemy.domain.Cluster}.
 */
public interface ClusterService {

    /**
     * Save a cluster.
     *
     * @param clusterDTO the entity to save.
     * @return the persisted entity.
     */
    ClusterDTO save(ClusterDTO clusterDTO) throws Exception;

    /**
     * Get all the clusters.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ClusterDTO> findAll(Pageable pageable);


    /**
     * Get the "id" cluster.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ClusterDTO> findOne(Long id);

    /**
     * Delete the "id" cluster.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
