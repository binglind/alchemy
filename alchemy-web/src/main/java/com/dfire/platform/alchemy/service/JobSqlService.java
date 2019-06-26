package com.dfire.platform.alchemy.service;

import com.dfire.platform.alchemy.service.dto.JobSqlDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.dfire.platform.alchemy.domain.JobSql}.
 */
public interface JobSqlService {

    /**
     * Save a jobSql.
     *
     * @param jobSqlDTO the entity to save.
     * @return the persisted entity.
     */
    JobSqlDTO save(JobSqlDTO jobSqlDTO);

    /**
     * Get all the jobSqls.
     *
     * @return the list of entities.
     */
    List<JobSqlDTO> findAll();


    /**
     * Get the "id" jobSql.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<JobSqlDTO> findOne(Long id);

    /**
     * Delete the "id" jobSql.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
