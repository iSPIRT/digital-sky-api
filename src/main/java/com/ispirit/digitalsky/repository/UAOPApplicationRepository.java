package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.document.UAOPApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface UAOPApplicationRepository extends MongoRepository<UAOPApplication, String> {

    UAOPApplication findById(@Param("id") String id);
    Collection<?> findByApplicant(@Param("applicantId") long applicantId);
}
