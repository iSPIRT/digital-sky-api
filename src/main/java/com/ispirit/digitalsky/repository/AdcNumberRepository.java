package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.AdcNumber;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AdcNumberRepository extends CrudRepository<AdcNumber, Long> {

  @Query("SELECT ad FROM AdcNumber ad WHERE ad.id = MAX(id) and ad.validForDate= :validForDate")
  AdcNumber loadLatestAdcNumberOfDate(@Param("validForDate")LocalDate validForDate);


}
