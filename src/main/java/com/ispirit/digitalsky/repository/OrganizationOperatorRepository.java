package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.OrganizationOperator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface OrganizationOperatorRepository extends CrudRepository<OrganizationOperator, Long> {

    @Query("SELECT o FROM OrganizationOperator o WHERE  o.resourceOwnerId = :resourceOwnerId")
    OrganizationOperator loadByResourceOwner(@Param("resourceOwnerId") long resourceOwnerId);

    @Query("SELECT o FROM OrganizationOperator o WHERE  Lower(o.businessIdentifier) = Lower(:businessIdentifier)")
    OrganizationOperator loadByBusinessIdentifier(@Param("businessIdentifier") String businessIdentifier);
}
