package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.document.LocalDroneAcquisitionApplicationForm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface LocalDroneAcquisitionFormRepository extends MongoRepository<LocalDroneAcquisitionApplicationForm, String> {

    LocalDroneAcquisitionApplicationForm findById(@Param("id") String id);
    Collection<?> findByApplicant(@Param("applicantId") long applicantId);
}
