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

import com.dfire.platform.alchemy.domain.Job;
import com.dfire.platform.alchemy.domain.*; // for static metamodels
import com.dfire.platform.alchemy.repository.JobRepository;
import com.dfire.platform.alchemy.service.dto.JobCriteria;
import com.dfire.platform.alchemy.service.dto.JobDTO;
import com.dfire.platform.alchemy.service.mapper.JobMapper;

/**
 * Service for executing complex queries for {@link Job} entities in the database.
 * The main input is a {@link JobCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link JobDTO} or a {@link Page} of {@link JobDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class JobQueryService extends QueryService<Job> {

    private final Logger log = LoggerFactory.getLogger(JobQueryService.class);

    private final JobRepository jobRepository;

    private final JobMapper jobMapper;

    public JobQueryService(JobRepository jobRepository, JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.jobMapper = jobMapper;
    }

    /**
     * Return a {@link List} of {@link JobDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<JobDTO> findByCriteria(JobCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Job> specification = createSpecification(criteria);
        return jobMapper.toDto(jobRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link JobDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<JobDTO> findByCriteria(JobCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Job> specification = createSpecification(criteria);
        return jobRepository.findAll(specification, page)
            .map(jobMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(JobCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Job> specification = createSpecification(criteria);
        return jobRepository.count(specification);
    }

    /**
     * Function to convert JobCriteria to a {@link Specification}.
     */
    private Specification<Job> createSpecification(JobCriteria criteria) {
        Specification<Job> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Job_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Job_.name));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Job_.type));
            }
            if (criteria.getRemark() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRemark(), Job_.remark));
            }
            if (criteria.getClusterJobId() != null) {
                specification = specification.and(buildStringSpecification(criteria.getClusterJobId(), Job_.clusterJobId));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Job_.status));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Job_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Job_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Job_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Job_.lastModifiedDate));
            }
            if (criteria.getBusinessId() != null) {
                specification = specification.and(buildSpecification(criteria.getBusinessId(),
                    root -> root.join(Job_.business, JoinType.LEFT).get(Business_.id)));
            }
            if (criteria.getClusterId() != null) {
                specification = specification.and(buildSpecification(criteria.getClusterId(),
                    root -> root.join(Job_.cluster, JoinType.LEFT).get(Cluster_.id)));
            }
            if (criteria.getSqlId() != null) {
                specification = specification.and(buildSpecification(criteria.getSqlId(),
                    root -> root.join(Job_.sqls, JoinType.LEFT).get(JobSql_.id)));
            }
        }
        return specification;
    }
}
