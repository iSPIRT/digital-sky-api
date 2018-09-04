package com.ispirit.digitalsky.service.api;


import com.ispirit.digitalsky.domain.AirspaceCategory;

import java.util.List;

public interface AirspaceCategoryService {

    AirspaceCategory createNewAirspaceCategory(AirspaceCategory airspaceCategory);

    AirspaceCategory updateAirspaceCategory(long id, AirspaceCategory airspaceCategory);

    AirspaceCategory find(long id);

    List<AirspaceCategory> findAll();
}
