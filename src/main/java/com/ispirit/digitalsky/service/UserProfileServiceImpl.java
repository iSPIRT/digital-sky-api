package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.IndividualOperator;
import com.ispirit.digitalsky.domain.OrganizationOperator;
import com.ispirit.digitalsky.domain.Pilot;
import com.ispirit.digitalsky.domain.UserProfile;
import com.ispirit.digitalsky.repository.IndividualOperatorRepository;
import com.ispirit.digitalsky.repository.OrganizationOperatorRepository;
import com.ispirit.digitalsky.repository.PilotRepository;
import com.ispirit.digitalsky.service.api.UserProfileService;

public class UserProfileServiceImpl implements UserProfileService {


    private PilotRepository pilotRepository;
    private IndividualOperatorRepository individualOperatorRepository;
    private OrganizationOperatorRepository organizationOperatorRepository;

    public UserProfileServiceImpl(PilotRepository pilotRepository, IndividualOperatorRepository individualOperatorRepository, OrganizationOperatorRepository organizationOperatorRepository) {
        this.pilotRepository = pilotRepository;
        this.individualOperatorRepository = individualOperatorRepository;
        this.organizationOperatorRepository = organizationOperatorRepository;
    }

    @Override
    public UserProfile profile(long id) {
        Pilot pilot = pilotRepository.loadByResourceOwner(id);
        IndividualOperator individualOperator = individualOperatorRepository.loadByResourceOwner(id);
        OrganizationOperator organizationOperator = organizationOperatorRepository.loadByResourceOwner(id);
        long pilotProfileId = pilot != null ? pilot.getId() : 0;
        long individualOperatorId = individualOperator != null ? individualOperator.getId() : 0;
        long organizationOperatorId = organizationOperator != null ? organizationOperator.getId() : 0;
        return new UserProfile(id, pilotProfileId, individualOperatorId, organizationOperatorId);
    }
}
