package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.IndividualOperator;
import com.ispirit.digitalsky.exception.OperatorProfileAlreadyExist;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.service.api.IndividualOperatorService;
import org.springframework.transaction.annotation.Transactional;

public class IndividualOperatorServiceImpl implements IndividualOperatorService {

    private IndividualOperatorRepository individualOperatorRepository;

    private OrganizationOperatorRepository organizationOperatorRepository;

    public IndividualOperatorServiceImpl(IndividualOperatorRepository IndividualOperatorRepository, OrganizationOperatorRepository organizationOperatorRepository) {
        this.individualOperatorRepository = IndividualOperatorRepository;
        this.organizationOperatorRepository = organizationOperatorRepository;
    }

    @Override
    @Transactional
    public IndividualOperator createNewOperator(IndividualOperator individualOperator) {
        long resourceOwnerId = individualOperator.getResourceOwnerId();
        if (individualOperatorRepository.loadByResourceOwner(resourceOwnerId) != null
                || organizationOperatorRepository.loadByResourceOwner(resourceOwnerId) != null) {
            throw new OperatorProfileAlreadyExist();
        }
        return individualOperatorRepository.save(individualOperator);
    }

    @Override
    @Transactional
    public IndividualOperator updateOperator(long id, IndividualOperator operator) {
        operator.setId(id);
        return individualOperatorRepository.save(operator);
    }

    @Override
    public IndividualOperator find(long id) {
        return individualOperatorRepository.findOne(id);
    }
}
