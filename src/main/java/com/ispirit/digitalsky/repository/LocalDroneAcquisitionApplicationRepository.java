package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.document.LocalDroneAcquisitionApplication;

import java.util.Collection;

public interface LocalDroneAcquisitionApplicationRepository extends DroneAcquisitionApplicationRepository<LocalDroneAcquisitionApplication> {

    @Override
    LocalDroneAcquisitionApplication findById(String id);

    @Override
    Collection<LocalDroneAcquisitionApplication> findByApplicantId(long applicantId);
}
