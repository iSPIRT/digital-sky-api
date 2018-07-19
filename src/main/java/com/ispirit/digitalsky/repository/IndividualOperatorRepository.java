package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.IndividualOperator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface IndividualOperatorRepository extends CrudRepository<IndividualOperator, Long> {

    @Query("SELECT o FROM IndividualOperator o WHERE  o.resourceOwnerId = :resourceOwnerId")
    IndividualOperator loadByResourceOwner(@Param("resourceOwnerId") long resourceOwnerId);
}
