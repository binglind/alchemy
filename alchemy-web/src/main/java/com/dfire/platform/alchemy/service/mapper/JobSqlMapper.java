package com.dfire.platform.alchemy.service.mapper;

import com.dfire.platform.alchemy.domain.*;
import com.dfire.platform.alchemy.service.dto.JobSqlDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link JobSql} and its DTO {@link JobSqlDTO}.
 */
@Mapper(componentModel = "spring", uses = {JobMapper.class})
public interface JobSqlMapper extends EntityMapper<JobSqlDTO, JobSql> {

    @Mapping(source = "job.id", target = "jobId")
    JobSqlDTO toDto(JobSql jobSql);

    @Mapping(source = "jobId", target = "job")
    JobSql toEntity(JobSqlDTO jobSqlDTO);

    default JobSql fromId(Long id) {
        if (id == null) {
            return null;
        }
        JobSql jobSql = new JobSql();
        jobSql.setId(id);
        return jobSql;
    }
}
