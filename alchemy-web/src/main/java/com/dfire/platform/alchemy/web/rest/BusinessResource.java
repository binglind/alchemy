package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.security.SecurityUtils;
import com.dfire.platform.alchemy.service.BusinessService;
import com.dfire.platform.alchemy.web.rest.errors.BadRequestAlertException;
import com.dfire.platform.alchemy.service.dto.BusinessDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.dfire.platform.alchemy.domain.Business}.
 */
@RestController
@RequestMapping("/api")
public class BusinessResource {

    private final Logger log = LoggerFactory.getLogger(BusinessResource.class);

    private static final String ENTITY_NAME = "business";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BusinessService businessService;

    public BusinessResource(BusinessService businessService) {
        this.businessService = businessService;
    }

    /**
     * {@code POST  /businesses} : Create a new business.
     *
     * @param businessDTO the businessDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new businessDTO, or with status {@code 400 (Bad Request)} if the business has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/businesses")
    public ResponseEntity<BusinessDTO> createBusiness(@Valid @RequestBody BusinessDTO businessDTO) throws URISyntaxException {
        log.debug("REST request to save Business : {}", businessDTO);
        if (businessDTO.getId() != null) {
            throw new BadRequestAlertException("A new business cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if(!loginUser.isPresent()){
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        businessDTO.setCreatedBy(loginUser.get());
        businessDTO.setCreatedDate(Instant.now());
        BusinessDTO result = businessService.save(businessDTO);
        return ResponseEntity.created(new URI("/api/businesses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /businesses} : Updates an existing business.
     *
     * @param businessDTO the businessDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessDTO,
     * or with status {@code 400 (Bad Request)} if the businessDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the businessDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/businesses")
    public ResponseEntity<BusinessDTO> updateBusiness(@Valid @RequestBody BusinessDTO businessDTO) throws URISyntaxException {
        log.debug("REST request to update Business : {}", businessDTO);
        if (businessDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        BusinessDTO result = businessService.save(businessDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, businessDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /businesses} : get all the businesses.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businesses in body.
     */
    @GetMapping("/businesses")
    public List<BusinessDTO> getAllBusinesses() {
        log.debug("REST request to get all Businesses");
        return businessService.findAll();
    }

    /**
     * {@code GET  /businesses/:id} : get the "id" business.
     *
     * @param id the id of the businessDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businessDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/businesses/{id}")
    public ResponseEntity<BusinessDTO> getBusiness(@PathVariable Long id) {
        log.debug("REST request to get Business : {}", id);
        Optional<BusinessDTO> businessDTO = businessService.findOne(id);
        return ResponseUtil.wrapOrNotFound(businessDTO);
    }

    /**
     * {@code DELETE  /businesses/:id} : delete the "id" business.
     *
     * @param id the id of the businessDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/businesses/{id}")
    public ResponseEntity<Void> deleteBusiness(@PathVariable Long id) {
        log.debug("REST request to delete Business : {}", id);
        businessService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
