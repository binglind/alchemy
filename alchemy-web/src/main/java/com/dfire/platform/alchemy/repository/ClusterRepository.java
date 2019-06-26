package com.dfire.platform.alchemy.repository;

import com.dfire.platform.alchemy.domain.Cluster;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Cluster entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClusterRepository extends JpaRepository<Cluster, Long>, JpaSpecificationExecutor<Cluster> {

}
