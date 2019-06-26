package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.domain.enumeration.JobStatus;
import com.dfire.platform.alchemy.client.response.Response;
import com.dfire.platform.alchemy.security.SecurityUtils;
import com.dfire.platform.alchemy.service.JobService;
import com.dfire.platform.alchemy.web.rest.errors.BadRequestAlertException;
import com.dfire.platform.alchemy.service.dto.JobDTO;
import com.dfire.platform.alchemy.service.dto.JobCriteria;
import com.dfire.platform.alchemy.service.JobQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.dfire.platform.alchemy.domain.Job}.
 */
@RestController
@RequestMapping("/api")
public class JobResource {

    private final Logger log = LoggerFactory.getLogger(JobResource.class);

    private static final String ENTITY_NAME = "job";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JobService jobService;

    private final JobQueryService jobQueryService;

    public JobResource(JobService jobService, JobQueryService jobQueryService) {
        this.jobService = jobService;
        this.jobQueryService = jobQueryService;
    }

    /**
     * {@code POST  /jobs} : Create a new job.
     *
     * @param jobDTO the jobDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new jobDTO, or with status {@code 400 (Bad Request)} if the job has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/jobs")
    public ResponseEntity<JobDTO> createJob(@Valid @RequestBody JobDTO jobDTO) throws URISyntaxException {
        log.debug("REST request to save Job : {}", jobDTO);
        if (jobDTO.getId() != null) {
            throw new BadRequestAlertException("A new job cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if(!loginUser.isPresent()){
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        jobDTO.setStatus(JobStatus.CREATE);
        jobDTO.setCreatedBy(loginUser.get());
        jobDTO.setCreatedDate(Instant.now());
        jobDTO.setLastModifiedBy(loginUser.get());
        jobDTO.setLastModifiedDate(Instant.now());
        JobDTO result = jobService.save(jobDTO);
        return ResponseEntity.created(new URI("/api/jobs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /jobs} : Updates an existing job.
     *
     * @param jobDTO the jobDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jobDTO,
     * or with status {@code 400 (Bad Request)} if the jobDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the jobDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/jobs")
    public ResponseEntity<JobDTO> updateJob(@Valid @RequestBody JobDTO jobDTO) throws URISyntaxException {
        log.debug("REST request to update Job : {}", jobDTO);
        if (jobDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if(!loginUser.isPresent()){
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        jobDTO.setStatus(JobStatus.UPDATE);
        jobDTO.setLastModifiedBy(loginUser.get());
        jobDTO.setLastModifiedDate(Instant.now());
        JobDTO result = jobService.save(jobDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, jobDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /jobs} : get all the jobs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of jobs in body.
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<JobDTO>> getAllJobs(JobCriteria criteria, Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get Jobs by criteria: {}", criteria);
        Page<JobDTO> page = jobQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /jobs/count} : count all the jobs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/jobs/count")
    public ResponseEntity<Long> countJobs(JobCriteria criteria) {
        log.debug("REST request to count Jobs by criteria: {}", criteria);
        return ResponseEntity.ok().body(jobQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /jobs/:id} : get the "id" job.
     *
     * @param id the id of the jobDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the jobDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/jobs/{id}")
    public ResponseEntity<JobDTO> getJob(@PathVariable Long id) {
        log.debug("REST request to get Job : {}", id);
        Optional<JobDTO> jobDTO = jobService.findOne(id);
        return ResponseUtil.wrapOrNotFound(jobDTO);
    }

    /**
     * {@code DELETE  /jobs/:id} : delete the "id" job.
     *
     * @param id the id of the jobDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) throws Exception {
        log.debug("REST request to delete Job : {}", id);
        jobService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    @GetMapping("/jobs/submit/{id}")
    public ResponseEntity<Response> submitJob(@PathVariable Long id) throws Exception {
        log.debug("REST request to submit Job : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Response result = jobService.submit(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    @GetMapping("/jobs/cancel/{id}")
    public ResponseEntity<Response> cancelJob(@PathVariable Long id) throws Exception {
        log.debug("REST request to cancel Job : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Response result = jobService.cancel(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    @GetMapping("/jobs/cancel-savepoint")
    public ResponseEntity<Response> cancelWithSavepoint( Long id, String savepointDirectory) throws Exception {
        log.debug("REST request to cancelWithSavepoint Job : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Response result = jobService.cancelWithSavepoint(id,savepointDirectory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    @GetMapping("/jobs/rescale")
    public ResponseEntity<Response> rescale( Long id, Integer newParallelism) throws Exception {
        log.debug("REST request to rescale Job : {}, newParallelism:{}", id, newParallelism);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (newParallelism == null) {
            throw new BadRequestAlertException("Invalid newParallelism", ENTITY_NAME, "newParallelism null");
        }
        Response result = jobService.rescale(id,newParallelism);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    @GetMapping("/jobs/trigger-savepoint")
    public ResponseEntity<Response> triggerSavepoint(Long id, String savepointDirectory) throws Exception {
        log.debug("REST request to triggerSavepoint : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Response result = jobService.triggerSavepoint(id,savepointDirectory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }
}
