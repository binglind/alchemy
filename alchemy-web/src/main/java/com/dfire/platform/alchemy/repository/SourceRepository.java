package com.dfire.platform.alchemy.repository;

import com.dfire.platform.alchemy.domain.Source;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Source entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SourceRepository extends JpaRepository<Source, Long>, JpaSpecificationExecutor<Source> {

    Optional<Source> findOneByBusinessIdAndName(Long id, String name);
}
