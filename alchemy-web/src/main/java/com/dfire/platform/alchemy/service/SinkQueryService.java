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

import com.dfire.platform.alchemy.domain.Sink;
import com.dfire.platform.alchemy.domain.*; // for static metamodels
import com.dfire.platform.alchemy.repository.SinkRepository;
import com.dfire.platform.alchemy.service.dto.SinkCriteria;
import com.dfire.platform.alchemy.service.dto.SinkDTO;
import com.dfire.platform.alchemy.service.mapper.SinkMapper;

/**
 * Service for executing complex queries for {@link Sink} entities in the database.
 * The main input is a {@link SinkCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SinkDTO} or a {@link Page} of {@link SinkDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SinkQueryService extends QueryService<Sink> {

    private final Logger log = LoggerFactory.getLogger(SinkQueryService.class);

    private final SinkRepository sinkRepository;

    private final SinkMapper sinkMapper;

    public SinkQueryService(SinkRepository sinkRepository, SinkMapper sinkMapper) {
        this.sinkRepository = sinkRepository;
        this.sinkMapper = sinkMapper;
    }

    /**
     * Return a {@link List} of {@link SinkDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SinkDTO> findByCriteria(SinkCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Sink> specification = createSpecification(criteria);
        return sinkMapper.toDto(sinkRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SinkDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SinkDTO> findByCriteria(SinkCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Sink> specification = createSpecification(criteria);
        return sinkRepository.findAll(specification, page)
            .map(sinkMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SinkCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Sink> specification = createSpecification(criteria);
        return sinkRepository.count(specification);
    }

    /**
     * Function to convert SinkCriteria to a {@link Specification}.
     */
    private Specification<Sink> createSpecification(SinkCriteria criteria) {
        Specification<Sink> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Sink_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Sink_.name));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Sink_.type));
            }
            if (criteria.getRemark() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRemark(), Sink_.remark));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Sink_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Sink_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Sink_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Sink_.lastModifiedDate));
            }
            if (criteria.getBusinessId() != null) {
                specification = specification.and(buildSpecification(criteria.getBusinessId(),
                    root -> root.join(Sink_.business, JoinType.LEFT).get(Business_.id)));
            }
        }
        return specification;
    }
}
