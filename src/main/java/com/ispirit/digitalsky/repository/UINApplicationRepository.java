package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.document.UINApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface UINApplicationRepository extends MongoRepository<UINApplication, String> {

    UINApplication findById(@Param("id") String id);
    Collection<UINApplication> findByApplicantId(long applicantId);
}
