package com.dfire.platform.alchemy.repository;

import com.dfire.platform.alchemy.domain.JobSql;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the JobSql entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JobSqlRepository extends JpaRepository<JobSql, Long>, JpaSpecificationExecutor<JobSql> {

    List<JobSql> findByJobId(Long id);
}
