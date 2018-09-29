package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.DroneDevice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface DroneDeviceRepository extends CrudRepository<DroneDevice, Long> {

    @Query("SELECT d FROM DroneDevice d WHERE LOWER(d.deviceId) = LOWER(:uniqueDeviceCode)")
    DroneDevice findByDeviceId(@Param("uniqueDeviceCode") String uniqueDeviceCode);

    @Query("SELECT d.deviceId FROM DroneDevice d WHERE LOWER(d.operatorBusinessIdentifier) = LOWER(:operatorBusinessIdentifier) AND d.registrationStatus = 'REGISTERED'")
    Collection<String> findRegisteredDroneDeviceIds(@Param("operatorBusinessIdentifier") String operatorBusinessIdentifier);

}
