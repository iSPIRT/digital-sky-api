package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.Manufacturer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ManufacturerRepository extends CrudRepository<Manufacturer, Long> {

    @Query("SELECT p FROM Manufacturer p WHERE  p.resourceOwnerId = :resourceOwnerId")
    Manufacturer loadByResourceOwner(@Param("resourceOwnerId") long resourceOwnerId);

}
