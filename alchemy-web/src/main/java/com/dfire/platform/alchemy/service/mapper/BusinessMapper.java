package com.dfire.platform.alchemy.service.mapper;

import com.dfire.platform.alchemy.domain.*;
import com.dfire.platform.alchemy.service.dto.BusinessDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Business} and its DTO {@link BusinessDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BusinessMapper extends EntityMapper<BusinessDTO, Business> {


    @Mapping(target = "jobs", ignore = true)
    @Mapping(target = "clusters", ignore = true)
    @Mapping(target = "sources", ignore = true)
    @Mapping(target = "sinks", ignore = true)
    @Mapping(target = "udfs", ignore = true)
    Business toEntity(BusinessDTO businessDTO);

    default Business fromId(Long id) {
        if (id == null) {
            return null;
        }
        Business business = new Business();
        business.setId(id);
        return business;
    }
}
