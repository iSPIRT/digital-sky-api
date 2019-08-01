package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.FlightLogEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FlightLogRepository extends CrudRepository<FlightLogEntry, Long> {

  @Query("SELECT MAX(id) FROM FlightLogEntry fle WHERE fle.uin= :uin")
  long getLatestIdForUin(@Param("uin") String uin);

  @Query("SELECT signature FROM FlightLogEntry fle where fle.id= :id")
  String getHashForId(@Param("id") long id);

  @Query("SELECT COUNT(id)>0 FROM FlightLogEntry fle WHERE fle.uin= :uin")
  boolean checkEntriesForUin(@Param("uin") String uin);
}
