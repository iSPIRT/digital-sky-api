package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.Pilot;
import com.ispirit.digitalsky.exception.PilotProfileAlreadyExist;
import com.ispirit.digitalsky.repository.PilotRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.PilotService;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

public class PilotServiceImpl implements PilotService {

    private PilotRepository pilotRepository;
    private StorageService storageService;

    public PilotServiceImpl(PilotRepository pilotRepository, StorageService storageService) {
        this.pilotRepository = pilotRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public Pilot createNewPilot(Pilot pilot) {
        if (pilotRepository.loadByResourceOwner(pilot.getResourceOwnerId()) != null) {
            throw new PilotProfileAlreadyExist();
        }
        Pilot savedEntity = pilotRepository.save(pilot);
        MultipartFile trainingCertificate = pilot.getTrainingCertificate();
        if (trainingCertificate != null && !trainingCertificate.isEmpty()) {
            storageService.store(singletonList(trainingCertificate), pilotDocumentFolder(pilot));
        }
        return savedEntity;
    }

    @Override
    @Transactional
    public Pilot updatePilot(long id, Pilot pilot) {
        pilot.setId(id);
        Pilot updatedEntity = pilotRepository.save(pilot);
        MultipartFile trainingCertificate = pilot.getTrainingCertificate();
        if (trainingCertificate != null && !trainingCertificate.isEmpty()) {
            storageService.store(singletonList(trainingCertificate), pilotDocumentFolder(pilot));
        }
        return updatedEntity;
    }

    @Override
    public Pilot find(long id) {
        return pilotRepository.findOne(id);
    }

    @Override
    public Pilot findByBusinessIdentifier(String id) {
        return pilotRepository.loadByBusinessIdentifier(id);
    }

    @Override
    public Resource trainingCertificate(Pilot pilot) {
        return storageService.loadAsResource(pilotDocumentFolder(pilot), pilot.getTrainingCertificateDocName());
    }

    private String pilotDocumentFolder(Pilot pilot) {
        return format("Pilot-%s", pilot.getId());
    }
}
