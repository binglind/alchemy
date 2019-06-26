package com.dfire.platform.alchemy.service.mapper;

import com.dfire.platform.alchemy.domain.*;
import com.dfire.platform.alchemy.service.dto.SourceDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Source} and its DTO {@link SourceDTO}.
 */
@Mapper(componentModel = "spring", uses = {BusinessMapper.class})
public interface SourceMapper extends EntityMapper<SourceDTO, Source> {

    @Mapping(source = "business.id", target = "businessId")
    SourceDTO toDto(Source source);

    @Mapping(source = "businessId", target = "business")
    Source toEntity(SourceDTO sourceDTO);

    default Source fromId(Long id) {
        if (id == null) {
            return null;
        }
        Source source = new Source();
        source.setId(id);
        return source;
    }
}
