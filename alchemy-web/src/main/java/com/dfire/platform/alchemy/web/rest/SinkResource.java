package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.security.SecurityUtils;
import com.dfire.platform.alchemy.service.SinkService;
import com.dfire.platform.alchemy.web.rest.errors.BadRequestAlertException;
import com.dfire.platform.alchemy.service.dto.SinkDTO;
import com.dfire.platform.alchemy.service.dto.SinkCriteria;
import com.dfire.platform.alchemy.service.SinkQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
 * REST controller for managing {@link com.dfire.platform.alchemy.domain.Sink}.
 */
@RestController
@RequestMapping("/api")
public class SinkResource {

    private final Logger log = LoggerFactory.getLogger(SinkResource.class);

    private static final String ENTITY_NAME = "sink";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SinkService sinkService;

    private final SinkQueryService sinkQueryService;

    public SinkResource(SinkService sinkService, SinkQueryService sinkQueryService) {
        this.sinkService = sinkService;
        this.sinkQueryService = sinkQueryService;
    }

    /**
     * {@code POST  /sinks} : Create a new sink.
     *
     * @param sinkDTO the sinkDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sinkDTO, or with status {@code 400 (Bad Request)} if the sink has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sinks")
    public ResponseEntity<SinkDTO> createSink(@Valid @RequestBody SinkDTO sinkDTO) throws URISyntaxException {
        log.debug("REST request to save Sink : {}", sinkDTO);
        if (sinkDTO.getId() != null) {
            throw new BadRequestAlertException("A new sink cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if(!loginUser.isPresent()){
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        sinkDTO.setCreatedBy(loginUser.get());
        sinkDTO.setCreatedDate(Instant.now());
        sinkDTO.setLastModifiedBy(loginUser.get());
        sinkDTO.setLastModifiedDate(Instant.now());
        SinkDTO result = sinkService.save(sinkDTO);
        return ResponseEntity.created(new URI("/api/sinks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sinks} : Updates an existing sink.
     *
     * @param sinkDTO the sinkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sinkDTO,
     * or with status {@code 400 (Bad Request)} if the sinkDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sinkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sinks")
    public ResponseEntity<SinkDTO> updateSink(@Valid @RequestBody SinkDTO sinkDTO) throws URISyntaxException {
        log.debug("REST request to update Sink : {}", sinkDTO);
        if (sinkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if(!loginUser.isPresent()){
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        sinkDTO.setLastModifiedBy(loginUser.get());
        sinkDTO.setLastModifiedDate(Instant.now());
        SinkDTO result = sinkService.save(sinkDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sinkDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /sinks} : get all the sinks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sinks in body.
     */
    @GetMapping("/sinks")
    public ResponseEntity<List<SinkDTO>> getAllSinks(SinkCriteria criteria, Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get Sinks by criteria: {}", criteria);
        Page<SinkDTO> page = sinkQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * {@code GET  /sinks/count} : count all the sinks.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
    */
    @GetMapping("/sinks/count")
    public ResponseEntity<Long> countSinks(SinkCriteria criteria) {
        log.debug("REST request to count Sinks by criteria: {}", criteria);
        return ResponseEntity.ok().body(sinkQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sinks/:id} : get the "id" sink.
     *
     * @param id the id of the sinkDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sinkDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sinks/{id}")
    public ResponseEntity<SinkDTO> getSink(@PathVariable Long id) {
        log.debug("REST request to get Sink : {}", id);
        Optional<SinkDTO> sinkDTO = sinkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sinkDTO);
    }

    /**
     * {@code DELETE  /sinks/:id} : delete the "id" sink.
     *
     * @param id the id of the sinkDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sinks/{id}")
    public ResponseEntity<Void> deleteSink(@PathVariable Long id) {
        log.debug("REST request to delete Sink : {}", id);
        sinkService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
