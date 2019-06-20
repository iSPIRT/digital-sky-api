package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.FicNumber;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface FicNumberRepository extends CrudRepository<FicNumber, Long> {

  @Query("SELECT MAX(id) FROM FicNumber fic WHERE fic.validForDate= :validForDate")
  long loadLatestFicNumberOfDate(@Param("validForDate")LocalDateTime validForDate);

  @Query("SELECT COUNT(id) FROM FicNumber fic where fic.validForDate= :validForDate")
  long findNumberOfEntriesForDate(@Param("validForDate")LocalDateTime validForDate);

}
