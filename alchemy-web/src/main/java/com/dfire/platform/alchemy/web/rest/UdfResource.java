package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.security.SecurityUtils;
import com.dfire.platform.alchemy.service.UdfService;
import com.dfire.platform.alchemy.web.rest.errors.BadRequestAlertException;
import com.dfire.platform.alchemy.service.dto.UdfDTO;
import com.dfire.platform.alchemy.service.dto.UdfCriteria;
import com.dfire.platform.alchemy.service.UdfQueryService;

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
 * REST controller for managing {@link com.dfire.platform.alchemy.domain.Udf}.
 */
@RestController
@RequestMapping("/api")
public class UdfResource {

    private final Logger log = LoggerFactory.getLogger(UdfResource.class);

    private static final String ENTITY_NAME = "udf";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UdfService udfService;

    private final UdfQueryService udfQueryService;

    public UdfResource(UdfService udfService, UdfQueryService udfQueryService) {
        this.udfService = udfService;
        this.udfQueryService = udfQueryService;
    }

    /**
     * {@code POST  /udfs} : Create a new udf.
     *
     * @param udfDTO the udfDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new udfDTO, or with status {@code 400 (Bad Request)} if the udf has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/udfs")
    public ResponseEntity<UdfDTO> createUdf(@Valid @RequestBody UdfDTO udfDTO) throws URISyntaxException {
        log.debug("REST request to save Udf : {}", udfDTO);
        if (udfDTO.getId() != null) {
            throw new BadRequestAlertException("A new udf cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if(!loginUser.isPresent()){
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        udfDTO.setCreatedBy(loginUser.get());
        udfDTO.setCreatedDate(Instant.now());
        udfDTO.setLastModifiedBy(loginUser.get());
        udfDTO.setLastModifiedDate(Instant.now());
        UdfDTO result = udfService.save(udfDTO);
        return ResponseEntity.created(new URI("/api/udfs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /udfs} : Updates an existing udf.
     *
     * @param udfDTO the udfDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated udfDTO,
     * or with status {@code 400 (Bad Request)} if the udfDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the udfDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/udfs")
    public ResponseEntity<UdfDTO> updateUdf(@Valid @RequestBody UdfDTO udfDTO) throws URISyntaxException {
        log.debug("REST request to update Udf : {}", udfDTO);
        if (udfDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if(!loginUser.isPresent()){
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        udfDTO.setLastModifiedBy(loginUser.get());
        udfDTO.setLastModifiedDate(Instant.now());
        UdfDTO result = udfService.save(udfDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, udfDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /udfs} : get all the udfs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of udfs in body.
     */
    @GetMapping("/udfs")
    public ResponseEntity<List<UdfDTO>> getAllUdfs(UdfCriteria criteria, Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get Udfs by criteria: {}", criteria);
        Page<UdfDTO> page = udfQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /udfs/count} : count all the udfs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/udfs/count")
    public ResponseEntity<Long> countUdfs(UdfCriteria criteria) {
        log.debug("REST request to count Udfs by criteria: {}", criteria);
        return ResponseEntity.ok().body(udfQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /udfs/:id} : get the "id" udf.
     *
     * @param id the id of the udfDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the udfDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/udfs/{id}")
    public ResponseEntity<UdfDTO> getUdf(@PathVariable Long id) {
        log.debug("REST request to get Udf : {}", id);
        Optional<UdfDTO> udfDTO = udfService.findOne(id);
        return ResponseUtil.wrapOrNotFound(udfDTO);
    }

    /**
     * {@code DELETE  /udfs/:id} : delete the "id" udf.
     *
     * @param id the id of the udfDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/udfs/{id}")
    public ResponseEntity<Void> deleteUdf(@PathVariable Long id) {
        log.debug("REST request to delete Udf : {}", id);
        udfService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
