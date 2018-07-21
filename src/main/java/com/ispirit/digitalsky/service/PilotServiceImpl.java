package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.Pilot;
import com.ispirit.digitalsky.exception.PilotProfileAlreadyExist;
import com.ispirit.digitalsky.repository.PilotRepository;
import com.ispirit.digitalsky.service.api.PilotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public class PilotServiceImpl implements PilotService {

    private PilotRepository pilotRepository;

    public PilotServiceImpl(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }

    @Override
    @Transactional
    public Pilot createNewPilot(Pilot pilot) {
        if (pilotRepository.loadByResourceOwner(pilot.getResourceOwnerId()) != null) {
            throw new PilotProfileAlreadyExist();
        }
        return pilotRepository.save(pilot);
    }

    @Override
    @Transactional

    public Pilot updatePilot(long id, Pilot pilot) {
        pilot.setId(id);
        return pilotRepository.save(pilot);
    }

    @Override
    public Pilot find(long id) {
        return pilotRepository.findOne(id);
    }
}
