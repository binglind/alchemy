package com.dfire.platform.alchemy.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.dfire.platform.alchemy.domain.JobSql;
import com.dfire.platform.alchemy.domain.*; // for static metamodels
import com.dfire.platform.alchemy.repository.JobSqlRepository;
import com.dfire.platform.alchemy.service.dto.JobSqlCriteria;
import com.dfire.platform.alchemy.service.dto.JobSqlDTO;
import com.dfire.platform.alchemy.service.mapper.JobSqlMapper;

/**
 * Service for executing complex queries for {@link JobSql} entities in the database.
 * The main input is a {@link JobSqlCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link JobSqlDTO} or a {@link Page} of {@link JobSqlDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class JobSqlQueryService extends QueryService<JobSql> {

    private final Logger log = LoggerFactory.getLogger(JobSqlQueryService.class);

    private final JobSqlRepository jobSqlRepository;

    private final JobSqlMapper jobSqlMapper;

    public JobSqlQueryService(JobSqlRepository jobSqlRepository, JobSqlMapper jobSqlMapper) {
        this.jobSqlRepository = jobSqlRepository;
        this.jobSqlMapper = jobSqlMapper;
    }

    /**
     * Return a {@link List} of {@link JobSqlDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<JobSqlDTO> findByCriteria(JobSqlCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<JobSql> specification = createSpecification(criteria);
        return jobSqlMapper.toDto(jobSqlRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link JobSqlDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<JobSqlDTO> findByCriteria(JobSqlCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<JobSql> specification = createSpecification(criteria);
        return jobSqlRepository.findAll(specification, page)
            .map(jobSqlMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(JobSqlCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<JobSql> specification = createSpecification(criteria);
        return jobSqlRepository.count(specification);
    }

    /**
     * Function to convert JobSqlCriteria to a {@link Specification}.
     */
    private Specification<JobSql> createSpecification(JobSqlCriteria criteria) {
        Specification<JobSql> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), JobSql_.id));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), JobSql_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), JobSql_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), JobSql_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), JobSql_.lastModifiedDate));
            }
            if (criteria.getJobId() != null) {
                specification = specification.and(buildSpecification(criteria.getJobId(),
                    root -> root.join(JobSql_.job, JoinType.LEFT).get(Job_.id)));
            }
        }
        return specification;
    }
}
