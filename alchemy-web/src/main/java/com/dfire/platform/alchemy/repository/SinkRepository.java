package com.dfire.platform.alchemy.repository;

import com.dfire.platform.alchemy.domain.Sink;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Sink entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SinkRepository extends JpaRepository<Sink, Long>, JpaSpecificationExecutor<Sink> {

    Optional<Sink> findOneByBusinessIdAndName(Long id, String name);
}
