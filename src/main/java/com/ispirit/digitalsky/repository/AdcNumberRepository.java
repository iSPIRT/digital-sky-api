package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.AdcNumber;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AdcNumberRepository extends CrudRepository<AdcNumber, Long> {

  @Query("SELECT MAX(id) FROM AdcNumber ad WHERE ad.validForDate= :validForDate")
  long loadLatestAdcNumberOfDate(@Param("validForDate")LocalDateTime validForDate);

  @Query("SELECT COUNT(id) FROM AdcNumber ad where ad.validForDate= :validForDate")
  long findNumberOfEntriesForDate(@Param("validForDate")LocalDateTime validForDate);

}
