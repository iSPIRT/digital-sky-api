package com.ispirit.digitalsky.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.domain.AirspaceCategory;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.exception.ValidationException;
import com.ispirit.digitalsky.repository.AirspaceCategoryRepository;
import com.ispirit.digitalsky.service.api.AirspaceCategoryService;
import org.apache.commons.io.IOUtils;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@TestPropertySource(
    locations = "classpath:application.yml")
public class AirspaceCategoryServiceImplTest {

    private AirspaceCategoryRepository repository;
    private AirspaceCategoryService airspaceCategoryService;
    private AirspaceCategoryService unmockedAirspaceCategoryService;
    private UserPrincipal userPrincipal;

    @Autowired
    private AirspaceCategoryRepository unmockedRepository;

    @Before
    public void setUp() throws Exception {
        userPrincipal = SecurityContextHelper.setUserSecurityContext();
        repository = mock(AirspaceCategoryRepository.class);
        airspaceCategoryService = new AirspaceCategoryServiceImpl(repository);
        unmockedAirspaceCategoryService = new AirspaceCategoryServiceImpl(unmockedRepository);
    }

    @Test
    public void shouldCheckForPolygonGeometryBeforeSave() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonMultiShape.json"), "UTF-8");
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, geoJson);

        //when
        try {
            airspaceCategoryService.createNewAirspaceCategory(airspaceCategory);
            fail("should have thrown validation exception");
        } catch (ValidationException e) {
        }
    }

    @Test
    public void shouldSaveAirspaceCategory() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, geoJson);
        System.out.println(new ObjectMapper().writeValueAsString(airspaceCategory));
        //when
        airspaceCategoryService.createNewAirspaceCategory(airspaceCategory);

        //then
        ArgumentCaptor<AirspaceCategory> argumentCaptor = ArgumentCaptor.forClass(AirspaceCategory.class);
        verify(repository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getCreatedById(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getModifiedById(), is(userPrincipal.getId()));

        assertThat(argumentCaptor.getValue().getCreatedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getModifiedDate(), notNullValue());
    }

    @Test
    public void shouldSaveAirspaceCategoryWithAltitude() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        long altitudeVal = 20;
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, new ObjectMapper().readValue(geoJson, GeoJsonObject.class),altitudeVal);
        System.out.println(new ObjectMapper().writeValueAsString(airspaceCategory));
        //when
        airspaceCategoryService.createNewAirspaceCategory(airspaceCategory);

        //then
        ArgumentCaptor<AirspaceCategory> argumentCaptor = ArgumentCaptor.forClass(AirspaceCategory.class);
        verify(repository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getCreatedById(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getModifiedById(), is(userPrincipal.getId()));

        assertThat(argumentCaptor.getValue().getCreatedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getModifiedDate(), notNullValue());

        assertThat(argumentCaptor.getValue().getMinAltitude(),is(altitudeVal));
    }

    @Test
    public void shouldSaveAirspaceCategoryWithAltitudeAndTime() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        long altitudeVal = 20;
        LocalDateTime tempStartTime = LocalDateTime.now().plusDays(2);
        LocalDateTime tempEndTime = LocalDateTime.now().plusDays(2).plusHours(3);
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, new ObjectMapper().readValue(geoJson, GeoJsonObject.class),altitudeVal,tempStartTime,tempEndTime);
        System.out.println(new ObjectMapper().writeValueAsString(airspaceCategory));
        //when
        airspaceCategoryService.createNewAirspaceCategory(airspaceCategory);

        //then
        ArgumentCaptor<AirspaceCategory> argumentCaptor = ArgumentCaptor.forClass(AirspaceCategory.class);
        verify(repository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getCreatedById(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getModifiedById(), is(userPrincipal.getId()));

        assertThat(argumentCaptor.getValue().getCreatedDate(), notNullValue());
        assertThat(argumentCaptor.getValue().getModifiedDate(), notNullValue());

        assertThat(argumentCaptor.getValue().getMinAltitude(),is(altitudeVal));
        assertThat(argumentCaptor.getValue().getTempStartTime(),is(tempStartTime));
        assertThat(argumentCaptor.getValue().getTempEndTime(),is(tempEndTime));
    }

    @Test
    public void shouldCheckForExistingAirspaceCategoryBeforeUpdate() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonMultiShape.json"), "UTF-8");
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, geoJson);

        //when
        try {
            airspaceCategoryService.updateAirspaceCategory(1L, airspaceCategory);
            fail("should have thrown EntityNotFoundException");
        } catch (EntityNotFoundException e) {
        }
    }

    @Test
    public void shouldCheckForPolygonGeometryBeforeUpdate() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonMultiShape.json"), "UTF-8");
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, geoJson);
        when(repository.findOne(1L)).thenReturn(airspaceCategory);

        //when
        try {
            airspaceCategoryService.updateAirspaceCategory(1L, airspaceCategory);
            fail("should have thrown validation exception");
        } catch (ValidationException e) {
        }
    }

    @Test
    public void shouldUpdateAirspaceCategory() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, geoJson);
        when(repository.findOne(1L)).thenReturn(airspaceCategory);

        //when
        airspaceCategoryService.updateAirspaceCategory(1L, airspaceCategory);

        //then
        ArgumentCaptor<AirspaceCategory> argumentCaptor = ArgumentCaptor.forClass(AirspaceCategory.class);
        verify(repository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getModifiedById(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getModifiedDate(), notNullValue());
    }

    @Test
    public void shouldUpdateAirspaceCategoryWithDateTime() throws Exception {
        //given
        long altitudeVal = 20;
        LocalDateTime tempStartTime = LocalDateTime.now().plusDays(2);
        LocalDateTime tempEndTime = LocalDateTime.now().plusDays(2).plusHours(3);
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, new ObjectMapper().readValue(geoJson, GeoJsonObject.class),altitudeVal,tempStartTime,tempEndTime);
        when(repository.findOne(1L)).thenReturn(airspaceCategory);

        //when
        airspaceCategoryService.updateAirspaceCategory(1L, airspaceCategory);

        //then
        ArgumentCaptor<AirspaceCategory> argumentCaptor = ArgumentCaptor.forClass(AirspaceCategory.class);
        verify(repository).save(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getModifiedById(), is(userPrincipal.getId()));
        assertThat(argumentCaptor.getValue().getModifiedDate(), notNullValue());
    }

    @Test
    public void shouldGetAirspaceCategory() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        GeoJsonObject geoJsonObject = new ObjectMapper().readValue(geoJson, GeoJsonObject.class);

        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, geoJson);
        when(repository.findOne(1L)).thenReturn(airspaceCategory);

        //when
        AirspaceCategory result = airspaceCategoryService.find(1L);

        assertThat(result, is(airspaceCategory));
        assertThat(result.getGeoJson(), is(geoJsonObject));
    }

    @Test
    public void shouldGetAllAirspaceCategory() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        GeoJsonObject geoJsonObject = new ObjectMapper().readValue(geoJson, GeoJsonObject.class);
        AirspaceCategory airspaceCategoryGreenOne = new AirspaceCategory("Sample1", AirspaceCategory.Type.GREEN, geoJson);
        airspaceCategoryGreenOne.setModifiedDate(now);

        AirspaceCategory airspaceCategoryGreenTwo = new AirspaceCategory("Sample2", AirspaceCategory.Type.GREEN, geoJson);
        airspaceCategoryGreenTwo.setModifiedDate(now.plusMinutes(1));

        AirspaceCategory airspaceCategoryAmberOne = new AirspaceCategory("Sample1", AirspaceCategory.Type.AMBER, geoJson);
        airspaceCategoryAmberOne.setModifiedDate(now.plusMinutes(2));

        AirspaceCategory airspaceCategoryAmberTwo = new AirspaceCategory("Sample2", AirspaceCategory.Type.AMBER, geoJson);
        airspaceCategoryAmberTwo.setModifiedDate(now.plusMinutes(3));

        AirspaceCategory airspaceCategoryRedOne = new AirspaceCategory("Sample1", AirspaceCategory.Type.RED, geoJson);
        airspaceCategoryRedOne.setModifiedDate(now.plusMinutes(4));

        AirspaceCategory airspaceCategoryRedTwo = new AirspaceCategory("Sample2", AirspaceCategory.Type.RED, geoJson);
        airspaceCategoryRedTwo.setModifiedDate(now.plusMinutes(5));

        when(repository.findAll()).thenReturn(asList(
                airspaceCategoryGreenOne,
                airspaceCategoryRedOne,
                airspaceCategoryAmberOne,
                airspaceCategoryGreenTwo,
                airspaceCategoryRedTwo,
                airspaceCategoryAmberTwo));

        //when
        List<AirspaceCategory> result = airspaceCategoryService.findAll();

        //then
        assertThat(result.size(), is(6));
        assertThat(result, is(asList(airspaceCategoryRedTwo, airspaceCategoryRedOne,airspaceCategoryAmberTwo,  airspaceCategoryAmberOne, airspaceCategoryGreenTwo, airspaceCategoryGreenOne)));
        assertThat(result.get(0).getGeoJson(), is(geoJsonObject));
        assertThat(result.get(1).getGeoJson(), is(geoJsonObject));
        assertThat(result.get(2).getGeoJson(), is(geoJsonObject));
        assertThat(result.get(3).getGeoJson(), is(geoJsonObject));
        assertThat(result.get(4).getGeoJson(), is(geoJsonObject));
        assertThat(result.get(5).getGeoJson(), is(geoJsonObject));
    }

    @Test
    public void shouldGetAirspaceGeoJsonGroupedByType() throws Exception {
        //given
        String greenOne = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[90.00,35.00]}}]}";
        String greenTwo = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[91.00,36.00]}}]}";
        String amberOne = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[92.00,37.00]}}]}";
        String amberTwo = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[93.00,38.00]}}]}";
        String redOne = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"Point\",\"coordinates\":[94.00,39.00]}}]}";
        AirspaceCategory airspaceCategoryGreenOne = new AirspaceCategory("Sample1", AirspaceCategory.Type.GREEN, greenOne);
        AirspaceCategory airspaceCategoryGreenTwo = new AirspaceCategory("Sample1", AirspaceCategory.Type.GREEN, greenTwo);
        AirspaceCategory airspaceCategoryAmberOne = new AirspaceCategory("Sample1", AirspaceCategory.Type.AMBER, amberOne);
        AirspaceCategory airspaceCategoryAmberTwo = new AirspaceCategory("Sample1", AirspaceCategory.Type.AMBER, amberTwo);
        AirspaceCategory airspaceCategoryRedOne = new AirspaceCategory("Sample1", AirspaceCategory.Type.RED, redOne);

        when(repository.findAll()).thenReturn(asList(airspaceCategoryGreenOne, airspaceCategoryGreenTwo, airspaceCategoryAmberOne, airspaceCategoryAmberTwo, airspaceCategoryRedOne));

        //when
        Map<AirspaceCategory.Type, GeoJsonObject> geoJsonMapByType = airspaceCategoryService.findGeoJsonMapByType();

        assertThat(((FeatureCollection) geoJsonMapByType.get(AirspaceCategory.Type.GREEN)).getFeatures().size(),is(2));
        assertThat(((FeatureCollection) geoJsonMapByType.get(AirspaceCategory.Type.AMBER)).getFeatures().size(),is(2));
        assertThat(((FeatureCollection) geoJsonMapByType.get(AirspaceCategory.Type.RED)).getFeatures().size(),is(1));

    }
}