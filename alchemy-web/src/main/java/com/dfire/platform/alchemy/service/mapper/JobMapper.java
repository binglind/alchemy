package com.dfire.platform.alchemy.service.mapper;

import com.dfire.platform.alchemy.domain.*;
import com.dfire.platform.alchemy.service.dto.JobDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Job} and its DTO {@link JobDTO}.
 */
@Mapper(componentModel = "spring", uses = {BusinessMapper.class, ClusterMapper.class})
public interface JobMapper extends EntityMapper<JobDTO, Job> {

    @Mapping(source = "business.id", target = "businessId")
    @Mapping(source = "cluster.id", target = "clusterId")
    JobDTO toDto(Job job);

    @Mapping(source = "businessId", target = "business")
    @Mapping(source = "clusterId", target = "cluster")
    @Mapping(target = "sqls", ignore = true)
    Job toEntity(JobDTO jobDTO);

    default Job fromId(Long id) {
        if (id == null) {
            return null;
        }
        Job job = new Job();
        job.setId(id);
        return job;
    }
}
