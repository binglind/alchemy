package com.dfire.platform.alchemy.service.impl;

import com.dfire.platform.alchemy.service.JobSqlService;
import com.dfire.platform.alchemy.domain.JobSql;
import com.dfire.platform.alchemy.repository.JobSqlRepository;
import com.dfire.platform.alchemy.service.dto.JobSqlDTO;
import com.dfire.platform.alchemy.service.mapper.JobSqlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link JobSql}.
 */
@Service
@Transactional
public class JobSqlServiceImpl implements JobSqlService {

    private final Logger log = LoggerFactory.getLogger(JobSqlServiceImpl.class);

    private final JobSqlRepository jobSqlRepository;

    private final JobSqlMapper jobSqlMapper;

    public JobSqlServiceImpl(JobSqlRepository jobSqlRepository, JobSqlMapper jobSqlMapper) {
        this.jobSqlRepository = jobSqlRepository;
        this.jobSqlMapper = jobSqlMapper;
    }

    /**
     * Save a jobSql.
     *
     * @param jobSqlDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public JobSqlDTO save(JobSqlDTO jobSqlDTO) {
        log.debug("Request to save JobSql : {}", jobSqlDTO);
        JobSql jobSql = jobSqlMapper.toEntity(jobSqlDTO);
        jobSql = jobSqlRepository.save(jobSql);
        return jobSqlMapper.toDto(jobSql);
    }

    /**
     * Get all the jobSqls.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<JobSqlDTO> findAll() {
        log.debug("Request to get all JobSqls");
        return jobSqlRepository.findAll().stream()
            .map(jobSqlMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one jobSql by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<JobSqlDTO> findOne(Long id) {
        log.debug("Request to get JobSql : {}", id);
        return jobSqlRepository.findById(id)
            .map(jobSqlMapper::toDto);
    }

    /**
     * Delete the jobSql by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete JobSql : {}", id);
        jobSqlRepository.deleteById(id);
    }
}
