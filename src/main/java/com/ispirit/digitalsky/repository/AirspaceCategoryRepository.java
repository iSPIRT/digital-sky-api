package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.AirspaceCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AirspaceCategoryRepository extends CrudRepository<AirspaceCategory, Long> {

  @Query("SELECT ai FROM AirspaceCategory ai WHERE ai.minAltitude<:height AND ai.tempStartTime<=:endTime AND ai.tempEndTime >=:startTime order by ai.createdDate desc")
  List<AirspaceCategory> findWithHeightAndTime(@Param("height") long height, @Param("startTime")LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

  @Query("SELECT ai FROM AirspaceCategory ai WHERE ai.minAltitude<:height AND ai.tempStartTime is NULL AND ai.tempEndTime is NULL order by ai.createdDate desc")
  List<AirspaceCategory> findWithHeight(@Param("height") long height);

}

