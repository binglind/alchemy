package com.dfire.platform.alchemy.service.mapper;

import com.dfire.platform.alchemy.domain.*;
import com.dfire.platform.alchemy.service.dto.SinkDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Sink} and its DTO {@link SinkDTO}.
 */
@Mapper(componentModel = "spring", uses = {BusinessMapper.class})
public interface SinkMapper extends EntityMapper<SinkDTO, Sink> {

    @Mapping(source = "business.id", target = "businessId")
    SinkDTO toDto(Sink sink);

    @Mapping(source = "businessId", target = "business")
    Sink toEntity(SinkDTO sinkDTO);

    default Sink fromId(Long id) {
        if (id == null) {
            return null;
        }
        Sink sink = new Sink();
        sink.setId(id);
        return sink;
    }
}
