package com.dfire.platform.alchemy.web.rest;

import com.dfire.platform.alchemy.client.ClientManager;
import com.dfire.platform.alchemy.client.FlinkClient;
import com.dfire.platform.alchemy.security.SecurityUtils;
import com.dfire.platform.alchemy.service.ClusterQueryService;
import com.dfire.platform.alchemy.service.ClusterService;
import com.dfire.platform.alchemy.service.dto.ClusterCriteria;
import com.dfire.platform.alchemy.service.dto.ClusterDTO;
import com.dfire.platform.alchemy.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing {@link com.dfire.platform.alchemy.domain.Cluster}.
 */
@RestController
@RequestMapping("/api")
public class ClusterResource {

    private final Logger log = LoggerFactory.getLogger(ClusterResource.class);

    private static final String ENTITY_NAME = "cluster";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ClusterService clusterService;

    private final ClusterQueryService clusterQueryService;

    private final ClientManager clientManager;

    public ClusterResource(ClusterService clusterService, ClusterQueryService clusterQueryService, ClientManager clientManager) {
        this.clusterService = clusterService;
        this.clusterQueryService = clusterQueryService;
        this.clientManager = clientManager;
    }

    /**
     * {@code POST  /clusters} : Create a new cluster.
     *
     * @param clusterDTO the clusterDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new clusterDTO, or with status {@code 400 (Bad Request)} if the cluster has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/clusters")
    public ResponseEntity<ClusterDTO> createCluster(@Valid @RequestBody ClusterDTO clusterDTO) throws Exception {
        log.debug("REST request to save Cluster : {}", clusterDTO);
        if (clusterDTO.getId() != null) {
            throw new BadRequestAlertException("A new cluster cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if (!loginUser.isPresent()) {
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        clusterDTO.setCreatedBy(loginUser.get());
        clusterDTO.setLastModifiedBy(loginUser.get());
        clusterDTO.setCreatedDate(Instant.now());
        clusterDTO.setLastModifiedDate(Instant.now());
        ClusterDTO result = clusterService.save(clusterDTO);
        return ResponseEntity.created(new URI("/api/clusters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /clusters} : Updates an existing cluster.
     *
     * @param clusterDTO the clusterDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated clusterDTO,
     * or with status {@code 400 (Bad Request)} if the clusterDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the clusterDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/clusters")
    public ResponseEntity<ClusterDTO> updateCluster(@Valid @RequestBody ClusterDTO clusterDTO) throws Exception {
        log.debug("REST request to update Cluster : {}", clusterDTO);
        if (clusterDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<String> loginUser = SecurityUtils.getCurrentUserLogin();
        if (!loginUser.isPresent()) {
            throw new BadRequestAlertException("No user was found for this cluster", ENTITY_NAME, "usernotlogin");
        }
        clusterDTO.setCreatedDate(Instant.now());
        clusterDTO.setLastModifiedDate(Instant.now());
        ClusterDTO result = clusterService.save(clusterDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, clusterDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /clusters} : get all the clusters.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of clusters in body.
     */
    @GetMapping("/clusters")
    public ResponseEntity<List<ClusterDTO>> getAllClusters(ClusterCriteria criteria, Pageable pageable, @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {
        log.debug("REST request to get Clusters by criteria: {}", criteria);
        Page<ClusterDTO> page = clusterQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /clusters/count} : count all the clusters.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/clusters/count")
    public ResponseEntity<Long> countClusters(ClusterCriteria criteria) {
        log.debug("REST request to count Clusters by criteria: {}", criteria);
        return ResponseEntity.ok().body(clusterQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /clusters/:id} : get the "id" cluster.
     *
     * @param id the id of the clusterDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the clusterDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/clusters/{id}")
    public ResponseEntity<ClusterDTO> getCluster(@PathVariable Long id) {
        log.debug("REST request to get Cluster : {}", id);
        Optional<ClusterDTO> clusterDTO = clusterService.findOne(id);
        return ResponseUtil.wrapOrNotFound(clusterDTO);
    }

    @GetMapping("/clusters/web-url/{id}")
    public ResponseEntity<Map> getClusterWebUrl(@PathVariable Long id) {
        log.debug("REST request to get Cluster : {}", id);
        FlinkClient flinkClient = clientManager.getClient(id);
        Map<String, Object> result = new HashMap<>(1);
        result.put("url", flinkClient.getWebInterfaceURL());
        return ResponseEntity.ok().body(result);
    }

    /**
     * {@code DELETE  /clusters/:id} : delete the "id" cluster.
     *
     * @param id the id of the clusterDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/clusters/{id}")
    public ResponseEntity<Void> deleteCluster(@PathVariable Long id) throws Exception {
        log.debug("REST request to delete Cluster : {}", id);
        clusterService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
