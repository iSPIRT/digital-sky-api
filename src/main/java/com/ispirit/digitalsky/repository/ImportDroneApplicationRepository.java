package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.document.ImportDroneApplication;

import java.util.Collection;

public interface ImportDroneApplicationRepository extends DroneAcquisitionApplicationRepository<ImportDroneApplication> {
    @Override
    ImportDroneApplication findById(String id);

    @Override
    Collection<ImportDroneApplication> findByApplicantId(long applicantId);
}
