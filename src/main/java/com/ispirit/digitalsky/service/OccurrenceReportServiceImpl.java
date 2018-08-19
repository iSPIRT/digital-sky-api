package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.OccurrenceReport;
import com.ispirit.digitalsky.repository.OccurrenceReportRepository;
import com.ispirit.digitalsky.service.api.OccurrenceReportService;

import java.util.List;

public class OccurrenceReportServiceImpl implements OccurrenceReportService {

    private OccurrenceReportRepository repository;

    public OccurrenceReportServiceImpl(OccurrenceReportRepository repository) {
        this.repository = repository;
    }

    @Override
    public OccurrenceReport createNew(OccurrenceReport occurrenceReport) {
        return repository.save(occurrenceReport);
    }

    @Override
    public OccurrenceReport find(long id) {
        return repository.findOne(id);
    }

    @Override
    public List<OccurrenceReport> findByDroneId(long operatorDroneId) {
        return repository.findByDroneId(operatorDroneId);
    }
}
