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

import com.dfire.platform.alchemy.domain.Source;
import com.dfire.platform.alchemy.domain.*; // for static metamodels
import com.dfire.platform.alchemy.repository.SourceRepository;
import com.dfire.platform.alchemy.service.dto.SourceCriteria;
import com.dfire.platform.alchemy.service.dto.SourceDTO;
import com.dfire.platform.alchemy.service.mapper.SourceMapper;

/**
 * Service for executing complex queries for {@link Source} entities in the database.
 * The main input is a {@link SourceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SourceDTO} or a {@link Page} of {@link SourceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SourceQueryService extends QueryService<Source> {

    private final Logger log = LoggerFactory.getLogger(SourceQueryService.class);

    private final SourceRepository sourceRepository;

    private final SourceMapper sourceMapper;

    public SourceQueryService(SourceRepository sourceRepository, SourceMapper sourceMapper) {
        this.sourceRepository = sourceRepository;
        this.sourceMapper = sourceMapper;
    }

    /**
     * Return a {@link List} of {@link SourceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SourceDTO> findByCriteria(SourceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Source> specification = createSpecification(criteria);
        return sourceMapper.toDto(sourceRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SourceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SourceDTO> findByCriteria(SourceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Source> specification = createSpecification(criteria);
        return sourceRepository.findAll(specification, page)
            .map(sourceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SourceCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Source> specification = createSpecification(criteria);
        return sourceRepository.count(specification);
    }

    /**
     * Function to convert SourceCriteria to a {@link Specification}.
     */
    private Specification<Source> createSpecification(SourceCriteria criteria) {
        Specification<Source> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Source_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Source_.name));
            }
            if (criteria.getTableType() != null) {
                specification = specification.and(buildSpecification(criteria.getTableType(), Source_.tableType));
            }
            if (criteria.getSourceType() != null) {
                specification = specification.and(buildSpecification(criteria.getSourceType(), Source_.sourceType));
            }
            if (criteria.getRemark() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRemark(), Source_.remark));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Source_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Source_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Source_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Source_.lastModifiedDate));
            }
            if (criteria.getBusinessId() != null) {
                specification = specification.and(buildSpecification(criteria.getBusinessId(),
                    root -> root.join(Source_.business, JoinType.LEFT).get(Business_.id)));
            }
        }
        return specification;
    }
}
