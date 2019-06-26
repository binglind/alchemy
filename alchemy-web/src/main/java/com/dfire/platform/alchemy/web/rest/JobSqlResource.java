package com.dfire.platform.alchemy.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dfire.platform.alchemy.security.SecurityUtils;
import com.dfire.platform.alchemy.service.JobSqlQueryService;
import com.dfire.platform.alchemy.service.JobSqlService;
import com.dfire.platform.alchemy.service.dto.JobSqlCriteria;
import com.dfire.platform.alchemy.service.dto.JobSqlDTO;
import com.dfire.platform.alchemy.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dfire.platform.alchemy.domain.JobSql}.
 */
@RestController
@RequestMapping("/api")
public class JobSqlResource {

    private final Logger log = LoggerFactory.getLogger(JobSqlResource.class);

    private static final String ENTITY_NAME = "jobSql";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JobSqlService jobSqlService;

    private final JobSqlQueryService jobSqlQueryService;

    public JobSqlResource(JobSqlService jobSqlService, JobSqlQueryService jobSqlQueryService) {
        this.jobSqlService = jobSqlService;
        this.jobSqlQueryService = jobSqlQueryService;
    }

    /**
     * {@code POST  /job-sqls} : Create a new jobSql.
     *
     * @param jobSqlDTO the jobSqlDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new jobSqlDTO, or with
     *         status {@code 400 (Bad Request)} if the jobSql has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/job-sqls")
    public ResponseEntity<JobSqlDTO> createJobSql(@Valid @RequestBody JobSqlDTO jobSqlDTO) throws URISyntaxException {
        log.debug("REST request to save JobSql : {}", jobSqlDTO);
        if (jobSqlDTO.getId() != null) {
            throw new BadRequestAlertException("A new jobSql cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if (!loginUser.isPresent()) {
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        jobSqlDTO.setCreatedBy(loginUser.get());
        jobSqlDTO.setCreatedDate(Instant.now());
        jobSqlDTO.setLastModifiedBy(loginUser.get());
        jobSqlDTO.setLastModifiedDate(Instant.now());
        JobSqlDTO result = jobSqlService.save(jobSqlDTO);
        return ResponseEntity.created(new URI("/api/job-sqls/" + result.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /job-sqls} : Updates an existing jobSql.
     *
     * @param jobSqlDTO the jobSqlDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jobSqlDTO, or with
     *         status {@code 400 (Bad Request)} if the jobSqlDTO is not valid, or with status
     *         {@code 500 (Internal Server Error)} if the jobSqlDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/job-sqls")
    public ResponseEntity<JobSqlDTO> updateJobSql(@Valid @RequestBody JobSqlDTO jobSqlDTO) throws URISyntaxException {
        log.debug("REST request to update JobSql : {}", jobSqlDTO);
        if (jobSqlDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if (!loginUser.isPresent()) {
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        jobSqlDTO.setLastModifiedBy(loginUser.get());
        jobSqlDTO.setLastModifiedDate(Instant.now());
        JobSqlDTO result = jobSqlService.save(jobSqlDTO);
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, jobSqlDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /job-sqls} : get all the jobSqls.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of jobSqls in body.
     */
    @GetMapping("/job-sqls")
    public ResponseEntity<List<JobSqlDTO>> getAllJobSqls(JobSqlCriteria criteria) {
        log.debug("REST request to get JobSqls by criteria: {}", criteria);
        List<JobSqlDTO> entityList = jobSqlQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /job-sqls/count} : count all the jobSqls.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/job-sqls/count")
    public ResponseEntity<Long> countJobSqls(JobSqlCriteria criteria) {
        log.debug("REST request to count JobSqls by criteria: {}", criteria);
        return ResponseEntity.ok().body(jobSqlQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /job-sqls/:id} : get the "id" jobSql.
     *
     * @param id the id of the jobSqlDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the jobSqlDTO, or with status
     *         {@code 404 (Not Found)}.
     */
    @GetMapping("/job-sqls/{id}")
    public ResponseEntity<JobSqlDTO> getJobSql(@PathVariable Long id) {
        log.debug("REST request to get JobSql : {}", id);
        Optional<JobSqlDTO> jobSqlDTO = jobSqlService.findOne(id);
        return ResponseUtil.wrapOrNotFound(jobSqlDTO);
    }

    /**
     * {@code DELETE  /job-sqls/:id} : delete the "id" jobSql.
     *
     * @param id the id of the jobSqlDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/job-sqls/{id}")
    public ResponseEntity<Void> deleteJobSql(@PathVariable Long id) {
        log.debug("REST request to delete JobSql : {}", id);
        jobSqlService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
