package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.AirspaceCategory;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.exception.ValidationException;
import com.ispirit.digitalsky.repository.AirspaceCategoryRepository;
import com.ispirit.digitalsky.service.api.AirspaceCategoryService;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.geojson.Polygon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AirspaceCategoryServiceImpl implements AirspaceCategoryService {

    private AirspaceCategoryRepository airspaceCategoryRepository;

    public AirspaceCategoryServiceImpl(AirspaceCategoryRepository airspaceCategoryRepository) {
        this.airspaceCategoryRepository = airspaceCategoryRepository;
    }

    @Override
    public AirspaceCategory createNewAirspaceCategory(AirspaceCategory airspaceCategory) {
        validatePolygonGeometry(airspaceCategory);
        airspaceCategory.setCreatedDate(LocalDateTime.now());
        airspaceCategory.setModifiedDate(LocalDateTime.now());
        airspaceCategory.setCreatedById(UserPrincipal.securityContext().getId());
        airspaceCategory.setModifiedById(UserPrincipal.securityContext().getId());
        return airspaceCategoryRepository.save(airspaceCategory);
    }

    @Override
    public AirspaceCategory updateAirspaceCategory(long id, AirspaceCategory airspaceCategory) {
        AirspaceCategory currentEntity = find(id);
        validatePolygonGeometry(airspaceCategory);
        currentEntity.setModifiedById(UserPrincipal.securityContext().getId());
        currentEntity.setModifiedDate(LocalDateTime.now());
        currentEntity.setName(airspaceCategory.getName());
        currentEntity.setType(airspaceCategory.getType());
        currentEntity.setGeoJson(airspaceCategory.getGeoJson());
        currentEntity.setGeoJsonString(airspaceCategory.getGeoJsonString());
        return airspaceCategoryRepository.save(currentEntity);
    }

    @Override
    public AirspaceCategory find(long id) {
        AirspaceCategory airspaceCategory = airspaceCategoryRepository.findOne(id);
        if (airspaceCategory == null) {
            throw new EntityNotFoundException("AirspaceCategory", id);
        }
        airspaceCategory.setGeoJsonFromString();
        return airspaceCategory;
    }

    @Override
    public List<AirspaceCategory> findAll() {
        Iterable<AirspaceCategory> categories = airspaceCategoryRepository.findAll();
        List<AirspaceCategory> result = new ArrayList<>();
        for (AirspaceCategory category : categories) {
            category.setGeoJsonFromString();
            result.add(category);
        }
        result.sort((o1, o2) -> o2.getModifiedDate().compareTo(o1.getModifiedDate()));
        return result;
    }

    @Override
    public Map<AirspaceCategory.Type, GeoJsonObject> findGeoJsonMapByType() {
        HashMap<AirspaceCategory.Type, GeoJsonObject> result = new HashMap<>();
        List<AirspaceCategory> airspaceCategories = findAll();
        for (AirspaceCategory airspaceCategory : airspaceCategories) {
            if (result.containsKey(airspaceCategory.getType())) {
                FeatureCollection featureCollection = (FeatureCollection) result.get(airspaceCategory.getType());
                featureCollection.getFeatures().addAll(((FeatureCollection) airspaceCategory.getGeoJson()).getFeatures());
            } else {
                result.put(airspaceCategory.getType(),airspaceCategory.getGeoJson());
            }
        }
        return result;
    }

    private void validatePolygonGeometry(AirspaceCategory airspaceCategory) {
        FeatureCollection featureCollection = (FeatureCollection) airspaceCategory.getGeoJson();
        for (Feature feature : featureCollection.getFeatures()) {
            if (!(feature.getGeometry() instanceof Polygon)) {
                throw new ValidationException(new Errors("Only polygon features accepted"));
            }
        }
    }
}
