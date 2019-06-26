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

import com.dfire.platform.alchemy.domain.Udf;
import com.dfire.platform.alchemy.domain.*; // for static metamodels
import com.dfire.platform.alchemy.repository.UdfRepository;
import com.dfire.platform.alchemy.service.dto.UdfCriteria;
import com.dfire.platform.alchemy.service.dto.UdfDTO;
import com.dfire.platform.alchemy.service.mapper.UdfMapper;

/**
 * Service for executing complex queries for {@link Udf} entities in the database.
 * The main input is a {@link UdfCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UdfDTO} or a {@link Page} of {@link UdfDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UdfQueryService extends QueryService<Udf> {

    private final Logger log = LoggerFactory.getLogger(UdfQueryService.class);

    private final UdfRepository udfRepository;

    private final UdfMapper udfMapper;

    public UdfQueryService(UdfRepository udfRepository, UdfMapper udfMapper) {
        this.udfRepository = udfRepository;
        this.udfMapper = udfMapper;
    }

    /**
     * Return a {@link List} of {@link UdfDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UdfDTO> findByCriteria(UdfCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Udf> specification = createSpecification(criteria);
        return udfMapper.toDto(udfRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UdfDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UdfDTO> findByCriteria(UdfCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Udf> specification = createSpecification(criteria);
        return udfRepository.findAll(specification, page)
            .map(udfMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UdfCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Udf> specification = createSpecification(criteria);
        return udfRepository.count(specification);
    }

    /**
     * Function to convert UdfCriteria to a {@link Specification}.
     */
    private Specification<Udf> createSpecification(UdfCriteria criteria) {
        Specification<Udf> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Udf_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Udf_.name));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Udf_.type));
            }
            if (criteria.getDependency() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDependency(), Udf_.dependency));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Udf_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Udf_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Udf_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Udf_.lastModifiedDate));
            }
            if (criteria.getRemark() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRemark(), Udf_.remark));
            }
            if (criteria.getBusinessId() != null) {
                specification = specification.and(buildSpecification(criteria.getBusinessId(),
                    root -> root.join(Udf_.business, JoinType.LEFT).get(Business_.id)));
            }
        }
        return specification;
    }
}
