package com.ispirit.digitalsky.service.api;


import com.ispirit.digitalsky.domain.AirspaceCategory;
import org.geojson.GeoJsonObject;

import java.util.List;
import java.util.Map;

public interface AirspaceCategoryService {

    AirspaceCategory createNewAirspaceCategory(AirspaceCategory airspaceCategory);

    AirspaceCategory updateAirspaceCategory(long id, AirspaceCategory airspaceCategory);

    AirspaceCategory find(long id);

    List<AirspaceCategory> findAll();

    Map<AirspaceCategory.Type, GeoJsonObject> findGeoJsonMapByType();

    List<AirspaceCategory> findAllAboveHeight(long height);

    Map<AirspaceCategory.Type, GeoJsonObject> findGeoJsonMapByTypeAndHeight(long height);
}
