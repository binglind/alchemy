package com.dfire.platform.alchemy.service.impl;

import com.dfire.platform.alchemy.client.ClientManager;
import com.dfire.platform.alchemy.domain.Cluster;
import com.dfire.platform.alchemy.repository.ClusterRepository;
import com.dfire.platform.alchemy.service.ClusterService;
import com.dfire.platform.alchemy.service.dto.ClusterDTO;
import com.dfire.platform.alchemy.service.mapper.ClusterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Cluster}.
 */
@Service
@Transactional
public class ClusterServiceImpl implements ClusterService {

    private final Logger log = LoggerFactory.getLogger(ClusterServiceImpl.class);

    private final ClusterRepository clusterRepository;

    private final ClusterMapper clusterMapper;

    private final ClientManager clientManager;

    public ClusterServiceImpl(ClusterRepository clusterRepository, ClusterMapper clusterMapper,
        ClientManager clientManager) {
        this.clusterRepository = clusterRepository;
        this.clusterMapper = clusterMapper;
        this.clientManager = clientManager;
    }

    /**
     * Save a cluster.
     *
     * @param clusterDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public ClusterDTO save(ClusterDTO clusterDTO) throws Exception {
        log.debug("Request to save Cluster : {}", clusterDTO);
        boolean update = clusterDTO.getId() != null;
        Cluster cluster = clusterMapper.toEntity(clusterDTO);
        cluster = clusterRepository.save(cluster);
        ClusterDTO dto = clusterMapper.toDto(cluster);
        if(update){
            clientManager.updateClient(dto);
        }else{
            clientManager.putClient(dto);
        }
        return dto;
    }

    /**
     * Get all the clusters.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ClusterDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Clusters");
        return clusterRepository.findAll(pageable).map(clusterMapper::toDto);
    }

    /**
     * Get one cluster by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ClusterDTO> findOne(Long id) {
        log.debug("Request to get Cluster : {}", id);
        // todo 显示集群资源情况
        return clusterRepository.findById(id).map(clusterMapper::toDto);
    }

    /**
     * Delete the cluster by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) throws Exception {
        log.debug("Request to delete Cluster : {}", id);
        Cluster cluster = clusterRepository.getOne(id);
        clusterRepository.deleteById(id);
        clientManager.deleteClient(clusterMapper.toDto(cluster));
    }
}
