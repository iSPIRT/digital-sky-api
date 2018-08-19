package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.OccurrenceReport;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OccurrenceReportRepository extends CrudRepository<OccurrenceReport, Long> {

    @Query("SELECT o FROM OccurrenceReport o WHERE o.operatorDroneId = :operatorDroneId order by o.createdTimestamp desc")
    List<OccurrenceReport> findByDroneId(@Param("operatorDroneId") long operatorId);

}

