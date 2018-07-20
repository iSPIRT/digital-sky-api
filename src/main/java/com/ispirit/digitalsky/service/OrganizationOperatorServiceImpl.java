package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.OrganizationOperator;
import com.ispirit.digitalsky.exception.OperatorProfileAlreadyExist;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.service.api.OrganizationOperatorService;
import org.springframework.transaction.annotation.Transactional;

public class OrganizationOperatorServiceImpl implements OrganizationOperatorService {

    private OrganizationOperatorRepository organizationOperatorRepository;

    private IndividualOperatorRepository individualOperatorRepository;

    public OrganizationOperatorServiceImpl(OrganizationOperatorRepository organizationOperatorRepository, IndividualOperatorRepository individualOperatorRepository) {
        this.organizationOperatorRepository = organizationOperatorRepository;
        this.individualOperatorRepository = individualOperatorRepository;
    }

    @Override
    @Transactional
    public OrganizationOperator createNewOperator(OrganizationOperator organizationOperator) {
        long resourceOwnerId = organizationOperator.getResourceOwnerId();
        if (organizationOperatorRepository.loadByResourceOwner(resourceOwnerId) != null
                || individualOperatorRepository.loadByResourceOwner(resourceOwnerId) != null) {
            throw new OperatorProfileAlreadyExist();
        }

        return organizationOperatorRepository.save(organizationOperator);
    }

    @Override
    @Transactional
    public OrganizationOperator updateOperator(long id, OrganizationOperator organizationOperator) {
        organizationOperator.setId(id);
        return organizationOperatorRepository.save(organizationOperator);
    }

    @Override
    public OrganizationOperator find(long id) {
        return organizationOperatorRepository.findOne(id);
    }
}