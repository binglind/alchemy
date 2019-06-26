package com.dfire.platform.alchemy.repository;

import com.dfire.platform.alchemy.domain.Udf;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Udf entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UdfRepository extends JpaRepository<Udf, Long>, JpaSpecificationExecutor<Udf> {

    Optional<Udf> findOneByBusinessIdAndName(Long id, String name);

}
