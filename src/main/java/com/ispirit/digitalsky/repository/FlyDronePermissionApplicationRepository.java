package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.document.FlyDronePermissionApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface FlyDronePermissionApplicationRepository extends MongoRepository<FlyDronePermissionApplication, String> {

    FlyDronePermissionApplication findById(@Param("id") String id);

    Collection<FlyDronePermissionApplication> findByDroneId(long droneId);
}
