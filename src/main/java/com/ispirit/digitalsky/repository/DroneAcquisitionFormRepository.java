package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.document.DroneAcquisitionApplicationForm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface DroneAcquisitionFormRepository<T extends DroneAcquisitionApplicationForm> extends MongoRepository<T, String> {

    T findById(@Param("id") String id);
    Collection<T> findByApplicant(@Param("applicantId") long applicantId);
}
