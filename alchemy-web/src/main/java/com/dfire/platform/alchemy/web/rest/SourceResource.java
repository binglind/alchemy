package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.security.SecurityUtils;
import com.dfire.platform.alchemy.service.SourceService;
import com.dfire.platform.alchemy.web.rest.errors.BadRequestAlertException;
import com.dfire.platform.alchemy.service.dto.SourceDTO;
import com.dfire.platform.alchemy.service.dto.SourceCriteria;
import com.dfire.platform.alchemy.service.SourceQueryService;

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
 * REST controller for managing {@link com.dfire.platform.alchemy.domain.Source}.
 */
@RestController
@RequestMapping("/api")
public class SourceResource {

    private final Logger log = LoggerFactory.getLogger(SourceResource.class);

    private static final String ENTITY_NAME = "source";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SourceService sourceService;

    private final SourceQueryService sourceQueryService;

    public SourceResource(SourceService sourceService, SourceQueryService sourceQueryService) {
        this.sourceService = sourceService;
        this.sourceQueryService = sourceQueryService;
    }

    /**
     * {@code POST  /sources} : Create a new source.
     *
     * @param sourceDTO the sourceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sourceDTO, or with status {@code 400 (Bad Request)} if the source has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sources")
    public ResponseEntity<SourceDTO> createSource(@Valid @RequestBody SourceDTO sourceDTO) throws URISyntaxException {
        log.debug("REST request to save Source : {}", sourceDTO);
        if (sourceDTO.getId() != null) {
            throw new BadRequestAlertException("A new source cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if(!loginUser.isPresent()){
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        sourceDTO.setCreatedBy(loginUser.get());
        sourceDTO.setCreatedDate(Instant.now());
        sourceDTO.setLastModifiedBy(loginUser.get());
        sourceDTO.setLastModifiedDate(Instant.now());
        SourceDTO result = sourceService.save(sourceDTO);
        return ResponseEntity.created(new URI("/api/sources/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sources} : Updates an existing source.
     *
     * @param sourceDTO the sourceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sourceDTO,
     * or with status {@code 400 (Bad Request)} if the sourceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sourceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sources")
    public ResponseEntity<SourceDTO> updateSource(@Valid @RequestBody SourceDTO sourceDTO) throws URISyntaxException {
        log.debug("REST request to update Source : {}", sourceDTO);
        if (sourceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if(!loginUser.isPresent()){
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        sourceDTO.setLastModifiedBy(loginUser.get());
        sourceDTO.setLastModifiedDate(Instant.now());
        SourceDTO result = sourceService.save(sourceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sourceDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /sources} : get all the sources.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sources in body.
     */
    @GetMapping("/sources")
    public ResponseEntity<List<SourceDTO>> getAllSources(SourceCriteria criteria, Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get Sources by criteria: {}", criteria);
        Page<SourceDTO> page = sourceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * {@code GET  /sources/count} : count all the sources.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
    */
    @GetMapping("/sources/count")
    public ResponseEntity<Long> countSources(SourceCriteria criteria) {
        log.debug("REST request to count Sources by criteria: {}", criteria);
        return ResponseEntity.ok().body(sourceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sources/:id} : get the "id" source.
     *
     * @param id the id of the sourceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sourceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sources/{id}")
    public ResponseEntity<SourceDTO> getSource(@PathVariable Long id) {
        log.debug("REST request to get Source : {}", id);
        Optional<SourceDTO> sourceDTO = sourceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sourceDTO);
    }

    /**
     * {@code DELETE  /sources/:id} : delete the "id" source.
     *
     * @param id the id of the sourceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sources/{id}")
    public ResponseEntity<Void> deleteSource(@PathVariable Long id) {
        log.debug("REST request to delete Source : {}", id);
        sourceService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
