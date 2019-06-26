package com.dfire.platform.alchemy.service.mapper;

import com.dfire.platform.alchemy.domain.*;
import com.dfire.platform.alchemy.service.dto.FieldDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Field} and its DTO {@link FieldDTO}.
 */
@Mapper(componentModel = "spring", uses = {SourceMapper.class})
public interface FieldMapper extends EntityMapper<FieldDTO, Field> {

    @Mapping(source = "source.id", target = "sourceId")
    FieldDTO toDto(Field field);

    @Mapping(source = "sourceId", target = "source")
    Field toEntity(FieldDTO fieldDTO);

    default Field fromId(Long id) {
        if (id == null) {
            return null;
        }
        Field field = new Field();
        field.setId(id);
        return field;
    }
}
