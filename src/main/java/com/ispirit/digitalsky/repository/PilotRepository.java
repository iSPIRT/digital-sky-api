package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.Pilot;
import com.ispirit.digitalsky.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PilotRepository extends CrudRepository<Pilot, Long> {

    @Query("SELECT p FROM Pilot p WHERE  p.resourceOwnerId = :resourceOwnerId")
    Pilot loadByResourceOwner(@Param("resourceOwnerId") long resourceOwnerId);

}
