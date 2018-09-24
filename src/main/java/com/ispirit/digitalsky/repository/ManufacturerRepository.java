package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.Manufacturer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ManufacturerRepository extends CrudRepository<Manufacturer, Long> {

    @Query("SELECT m FROM Manufacturer m WHERE  m.resourceOwnerId = :resourceOwnerId")
    Manufacturer loadByResourceOwner(@Param("resourceOwnerId") long resourceOwnerId);

    @Query("SELECT m FROM Manufacturer m WHERE LOWER(m.name) = LOWER(:organizationName)")
    Manufacturer findByName(@Param("organizationName") String organizationName);

}
