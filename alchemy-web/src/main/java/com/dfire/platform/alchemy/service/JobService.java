package com.dfire.platform.alchemy.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dfire.platform.alchemy.client.response.Response;
import com.dfire.platform.alchemy.service.dto.JobDTO;

/**
 * Service Interface for managing {@link com.dfire.platform.alchemy.domain.Job}.
 */
public interface JobService {

    /**
     * Save a job.
     *
     * @param jobDTO the entity to save.
     * @return the persisted entity.
     */
    JobDTO save(JobDTO jobDTO);

    /**
     * Get all the jobs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<JobDTO> findAll(Pageable pageable);

    /**
     * Get the "id" job.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<JobDTO> findOne(Long id);

    /**
     * Delete the "id" job.
     *
     * @param id the id of the entity.
     */
    void delete(Long id) throws Exception;

    Response submit(Long id) throws Exception;

    Response cancel(Long id) throws Exception;

    Response cancelWithSavepoint(Long id, String savepointDirectory) throws Exception;

    Response rescale(Long id, int newParallelism) throws Exception;

    Response triggerSavepoint(Long id, String savepointDirectory) throws Exception;
}
