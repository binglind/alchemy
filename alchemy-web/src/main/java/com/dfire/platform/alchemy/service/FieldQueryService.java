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

import com.dfire.platform.alchemy.domain.Field;
import com.dfire.platform.alchemy.domain.*; // for static metamodels
import com.dfire.platform.alchemy.repository.FieldRepository;
import com.dfire.platform.alchemy.service.dto.FieldCriteria;
import com.dfire.platform.alchemy.service.dto.FieldDTO;
import com.dfire.platform.alchemy.service.mapper.FieldMapper;

/**
 * Service for executing complex queries for {@link Field} entities in the database.
 * The main input is a {@link FieldCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FieldDTO} or a {@link Page} of {@link FieldDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FieldQueryService extends QueryService<Field> {

    private final Logger log = LoggerFactory.getLogger(FieldQueryService.class);

    private final FieldRepository fieldRepository;

    private final FieldMapper fieldMapper;

    public FieldQueryService(FieldRepository fieldRepository, FieldMapper fieldMapper) {
        this.fieldRepository = fieldRepository;
        this.fieldMapper = fieldMapper;
    }

    /**
     * Return a {@link List} of {@link FieldDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FieldDTO> findByCriteria(FieldCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Field> specification = createSpecification(criteria);
        return fieldMapper.toDto(fieldRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link FieldDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<FieldDTO> findByCriteria(FieldCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Field> specification = createSpecification(criteria);
        return fieldRepository.findAll(specification, page)
            .map(fieldMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FieldCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Field> specification = createSpecification(criteria);
        return fieldRepository.count(specification);
    }

    /**
     * Function to convert FieldCriteria to a {@link Specification}.
     */
    private Specification<Field> createSpecification(FieldCriteria criteria) {
        Specification<Field> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Field_.id));
            }
            if (criteria.getColumnName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getColumnName(), Field_.columnName));
            }
            if (criteria.getColumnType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getColumnType(), Field_.columnType));
            }
            if (criteria.getConfig() != null) {
                specification = specification.and(buildStringSpecification(criteria.getConfig(), Field_.config));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Field_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Field_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Field_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Field_.lastModifiedDate));
            }
            if (criteria.getSourceId() != null) {
                specification = specification.and(buildSpecification(criteria.getSourceId(),
                    root -> root.join(Field_.source, JoinType.LEFT).get(Source_.id)));
            }
        }
        return specification;
    }
}
