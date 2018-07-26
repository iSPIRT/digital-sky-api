package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.document.DroneAcquisitionApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface DroneAcquisitionRepository<T extends DroneAcquisitionApplication> extends MongoRepository<T, String> {

    T findById(@Param("id") String id);
    Collection<T> findByApplicant(@Param("applicantId") long applicantId);
}
