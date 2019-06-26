package com.dfire.platform.alchemy.service.mapper;

import com.dfire.platform.alchemy.domain.*;
import com.dfire.platform.alchemy.service.dto.ClusterDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cluster} and its DTO {@link ClusterDTO}.
 */
@Mapper(componentModel = "spring", uses = {BusinessMapper.class})
public interface ClusterMapper extends EntityMapper<ClusterDTO, Cluster> {

    @Mapping(source = "business.id", target = "businessId")
    ClusterDTO toDto(Cluster cluster);

    @Mapping(source = "businessId", target = "business")
    @Mapping(target = "jobs", ignore = true)
    Cluster toEntity(ClusterDTO clusterDTO);

    default Cluster fromId(Long id) {
        if (id == null) {
            return null;
        }
        Cluster cluster = new Cluster();
        cluster.setId(id);
        return cluster;
    }
}
