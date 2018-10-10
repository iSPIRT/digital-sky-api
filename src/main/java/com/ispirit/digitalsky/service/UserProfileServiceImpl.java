package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.*;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.ManufacturerRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.repository.PilotRepository;
import com.ispirit.digitalsky.service.api.UserProfileService;

public class UserProfileServiceImpl implements UserProfileService {


    private PilotRepository pilotRepository;
    private IndividualOperatorRepository individualOperatorRepository;
    private OrganizationOperatorRepository organizationOperatorRepository;
    private ManufacturerRepository manufacturerRepository;

    public UserProfileServiceImpl(PilotRepository pilotRepository, IndividualOperatorRepository individualOperatorRepository, OrganizationOperatorRepository organizationOperatorRepository, ManufacturerRepository manufacturerRepository) {
        this.pilotRepository = pilotRepository;
        this.individualOperatorRepository = individualOperatorRepository;
        this.organizationOperatorRepository = organizationOperatorRepository;
        this.manufacturerRepository = manufacturerRepository;
    }

    @Override
    public UserProfile profile(long id) {
        Pilot pilot = pilotRepository.loadByResourceOwner(id);
        IndividualOperator individualOperator = individualOperatorRepository.loadByResourceOwner(id);
        OrganizationOperator organizationOperator = organizationOperatorRepository.loadByResourceOwner(id);
        Manufacturer manufacturer = manufacturerRepository.loadByResourceOwner(id);
        long pilotProfileId = pilot != null ? pilot.getId() : 0;
        long individualOperatorId = individualOperator != null ? individualOperator.getId() : 0;
        long organizationOperatorId = organizationOperator != null ? organizationOperator.getId() : 0;
        long manufacturerId = manufacturer != null ? manufacturer.getId() : 0;

        String pilotBusinessIdentifier = pilot != null ? pilot.getBusinessIdentifier() : null;
        String operatorBusinessIdentifier = null;

        if (individualOperator != null) {
            operatorBusinessIdentifier = individualOperator.getBusinessIdentifier();
        } else {
            if (organizationOperator != null) {
                operatorBusinessIdentifier = organizationOperator.getBusinessIdentifier();
            }
        }

        String manufacturerBusinessIdentifier = manufacturer != null ? manufacturer.getBusinessIdentifier() : null;
        return new UserProfile(id, pilotProfileId, individualOperatorId, organizationOperatorId, manufacturerId,
                pilotBusinessIdentifier, operatorBusinessIdentifier,
                manufacturerBusinessIdentifier
        );
    }

    @Override
    public String resolveOperatorBusinessIdentifier(ApplicantType applicantType, long operatorId) {
        if (applicantType == ApplicantType.INDIVIDUAL) {
            return individualOperatorRepository.findOne(operatorId).getBusinessIdentifier();
        }
        return organizationOperatorRepository.findOne(operatorId).getBusinessIdentifier();
    }
}
