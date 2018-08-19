package com.ispirit.digitalsky.service.api;


import com.ispirit.digitalsky.domain.OccurrenceReport;

import java.util.List;

public interface OccurrenceReportService {

    OccurrenceReport createNew(OccurrenceReport occurrenceReport);

    OccurrenceReport find(long id);

    List<OccurrenceReport> findByDroneId(long operatorDroneId);

}
