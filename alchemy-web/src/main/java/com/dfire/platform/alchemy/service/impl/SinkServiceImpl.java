package com.dfire.platform.alchemy.service.impl;

import com.dfire.platform.alchemy.service.SinkService;
import com.dfire.platform.alchemy.domain.Sink;
import com.dfire.platform.alchemy.repository.SinkRepository;
import com.dfire.platform.alchemy.service.dto.SinkDTO;
import com.dfire.platform.alchemy.service.mapper.SinkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Sink}.
 */
@Service
@Transactional
public class SinkServiceImpl implements SinkService {

    private final Logger log = LoggerFactory.getLogger(SinkServiceImpl.class);

    private final SinkRepository sinkRepository;

    private final SinkMapper sinkMapper;

    public SinkServiceImpl(SinkRepository sinkRepository, SinkMapper sinkMapper) {
        this.sinkRepository = sinkRepository;
        this.sinkMapper = sinkMapper;
    }

    /**
     * Save a sink.
     *
     * @param sinkDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public SinkDTO save(SinkDTO sinkDTO) {
        log.debug("Request to save Sink : {}", sinkDTO);
        Sink sink = sinkMapper.toEntity(sinkDTO);
        sink = sinkRepository.save(sink);
        return sinkMapper.toDto(sink);
    }

    /**
     * Get all the sinks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SinkDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sinks");
        return sinkRepository.findAll(pageable)
            .map(sinkMapper::toDto);
    }


    /**
     * Get one sink by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SinkDTO> findOne(Long id) {
        log.debug("Request to get Sink : {}", id);
        return sinkRepository.findById(id)
            .map(sinkMapper::toDto);
    }

    /**
     * Delete the sink by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Sink : {}", id);
        sinkRepository.deleteById(id);
    }
}
