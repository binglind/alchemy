package com.dfire.platform.alchemy.service.mapper;

import com.dfire.platform.alchemy.domain.*;
import com.dfire.platform.alchemy.service.dto.UdfDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Udf} and its DTO {@link UdfDTO}.
 */
@Mapper(componentModel = "spring", uses = {BusinessMapper.class})
public interface UdfMapper extends EntityMapper<UdfDTO, Udf> {

    @Mapping(source = "business.id", target = "businessId")
    UdfDTO toDto(Udf udf);

    @Mapping(source = "businessId", target = "business")
    Udf toEntity(UdfDTO udfDTO);

    default Udf fromId(Long id) {
        if (id == null) {
            return null;
        }
        Udf udf = new Udf();
        udf.setId(id);
        return udf;
    }
}
