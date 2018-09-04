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
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class AirspaceCategoryServiceImplTest {

    private AirspaceCategoryRepository repository;
    private AirspaceCategoryService airspaceCategoryService;
    private UserPrincipal userPrincipal;

    @Before
    public void setUp() throws Exception {
        userPrincipal = SecurityContextHelper.setUserSecurityContext();
        repository = mock(AirspaceCategoryRepository.class);
        airspaceCategoryService = new AirspaceCategoryServiceImpl(repository);
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
    public void shouldGetAirspaceCategory() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, geoJson);
        when(repository.findOne(1L)).thenReturn(airspaceCategory);

        //when
        AirspaceCategory result = airspaceCategoryService.find(1L);

        assertThat(result, is(airspaceCategory));
    }

    @Test
    public void shouldGetAllAirspaceCategory() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        AirspaceCategory airspaceCategoryGreenOne = new AirspaceCategory("Sample1", AirspaceCategory.Type.GREEN, geoJson);
        AirspaceCategory airspaceCategoryGreenTwo = new AirspaceCategory("Sample2", AirspaceCategory.Type.GREEN, geoJson);
        AirspaceCategory airspaceCategoryAmberOne = new AirspaceCategory("Sample1", AirspaceCategory.Type.AMBER, geoJson);
        AirspaceCategory airspaceCategoryAmberTwo = new AirspaceCategory("Sample2", AirspaceCategory.Type.AMBER, geoJson);
        AirspaceCategory airspaceCategoryRedOne = new AirspaceCategory("Sample1", AirspaceCategory.Type.RED, geoJson);
        AirspaceCategory airspaceCategoryRedTwo = new AirspaceCategory("Sample2", AirspaceCategory.Type.RED, geoJson);

        when(repository.findAll()).thenReturn(asList(
                airspaceCategoryGreenOne,
                airspaceCategoryAmberOne,
                airspaceCategoryRedOne,
                airspaceCategoryRedTwo,
                airspaceCategoryGreenTwo,
                airspaceCategoryAmberTwo));

        //when
        List<AirspaceCategory> result = airspaceCategoryService.findAll();

        //then
        assertThat(result.size(), is(6));
        assertThat(result, is(asList(airspaceCategoryGreenOne, airspaceCategoryGreenTwo, airspaceCategoryAmberOne, airspaceCategoryAmberTwo, airspaceCategoryRedOne, airspaceCategoryRedTwo)));
    }
}