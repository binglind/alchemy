package com.dfire.platform.alchemy.service;

import com.dfire.platform.alchemy.domain.Business;
import com.dfire.platform.alchemy.repository.BusinessRepository;
import com.dfire.platform.alchemy.service.dto.BusinessDTO;
import com.dfire.platform.alchemy.service.mapper.BusinessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link Business}.
 */
@Service
@Transactional
public class BusinessService {

    private final Logger log = LoggerFactory.getLogger(BusinessService.class);

    private final BusinessRepository businessRepository;

    private final BusinessMapper businessMapper;

    public BusinessService(BusinessRepository businessRepository, BusinessMapper businessMapper) {
        this.businessRepository = businessRepository;
        this.businessMapper = businessMapper;
    }

    /**
     * Save a business.
     *
     * @param businessDTO the entity to save.
     * @return the persisted entity.
     */
    public BusinessDTO save(BusinessDTO businessDTO) {
        log.debug("Request to save Business : {}", businessDTO);
        Business business = businessMapper.toEntity(businessDTO);
        business = businessRepository.save(business);
        return businessMapper.toDto(business);
    }

    /**
     * Get all the businesses.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BusinessDTO> findAll() {
        log.debug("Request to get all Businesses");
        return businessRepository.findAll().stream()
            .map(businessMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one business by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BusinessDTO> findOne(Long id) {
        log.debug("Request to get Business : {}", id);
        return businessRepository.findById(id)
            .map(businessMapper::toDto);
    }

    /**
     * Delete the business by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Business : {}", id);
        businessRepository.deleteById(id);
    }
}
