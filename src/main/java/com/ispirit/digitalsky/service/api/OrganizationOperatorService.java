package com.ispirit.digitalsky.service.api;


import com.ispirit.digitalsky.domain.OrganizationOperator;

public interface OrganizationOperatorService {

    OrganizationOperator createNewOperator(OrganizationOperator organizationOperator);

    OrganizationOperator updateOperator(long id, OrganizationOperator organizationOperator);

    OrganizationOperator find(long id);
}
