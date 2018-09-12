package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.ApplicantType;
import com.ispirit.digitalsky.domain.OperatorDrone;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OperatorDroneRepository extends CrudRepository<OperatorDrone, Long> {

    @Query("SELECT od FROM OperatorDrone od WHERE od.operatorId = :operatorId and od.operatorType = :operatorType")
    List<OperatorDrone> loadByOperator(@Param("operatorId") long operatorId, @Param("operatorType") ApplicantType operatorType);
}
