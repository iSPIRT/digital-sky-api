package com.ispirit.digitalsky.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.domain.AirspaceCategory;
import com.ispirit.digitalsky.exception.EntityNotFoundException;
import com.ispirit.digitalsky.service.api.AirspaceCategoryService;
import org.apache.commons.io.IOUtils;
import org.geojson.GeoJsonObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.ispirit.digitalsky.AssertionHelper.assertPreAuthorizeWithAdmin;
import static com.ispirit.digitalsky.HandlerMethodHelper.postMethod;
import static com.ispirit.digitalsky.HandlerMethodHelper.putMethod;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = AirspaceCategoryController.class, secure = false)
@Import({TestContext.class})
public class AirspaceCategoryControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    AirspaceCategoryService airspaceCategoryService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void shouldBeAbleToCreateAirspaceCategory() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, geoJson);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/airspaceCategory")
                                .content(objectMapper.writeValueAsString(airspaceCategory))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn()
                .getResponse();

        //then
        ArgumentCaptor<AirspaceCategory> argumentCaptor = ArgumentCaptor.forClass(AirspaceCategory.class);
        verify(airspaceCategoryService).createNewAirspaceCategory(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getName(), is(airspaceCategory.getName()));
        assertThat(argumentCaptor.getValue().getGeoJsonString(), is(objectMapper.writeValueAsString(airspaceCategory.getGeoJson())));
        assertThat(argumentCaptor.getValue().getType(), is(airspaceCategory.getType()));
        assertThat(response.getStatus(), is(HttpStatus.CREATED.value()));
    }

    @Test
    public void shouldValidateCreateAirspaceCategory() throws Exception {
        //given
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, (GeoJsonObject) null);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/airspaceCategory")
                                .content(objectMapper.writeValueAsString(airspaceCategory))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void shouldBeAbleToUpdateAirspaceCategory() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, geoJson);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        put("/api/airspaceCategory/1")
                                .content(objectMapper.writeValueAsString(airspaceCategory))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn()
                .getResponse();

        //then
        ArgumentCaptor<AirspaceCategory> argumentCaptor = ArgumentCaptor.forClass(AirspaceCategory.class);
        verify(airspaceCategoryService).updateAirspaceCategory(eq(1L), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getName(), is(airspaceCategory.getName()));
        assertThat(argumentCaptor.getValue().getGeoJsonString(), is(objectMapper.writeValueAsString(airspaceCategory.getGeoJson())));
        assertThat(argumentCaptor.getValue().getType(), is(airspaceCategory.getType()));
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
    }

    @Test
    public void shouldValidateUpdateAirspaceCategory() throws Exception {
        //given
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, (GeoJsonObject) null);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        put("/api/airspaceCategory/1")
                                .content(objectMapper.writeValueAsString(airspaceCategory))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void shouldReturnNotFoundIfAirspaceCategoryNotFoundForUpdate() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, geoJson);

        when(airspaceCategoryService.updateAirspaceCategory(eq(1L), any())).thenThrow(new EntityNotFoundException("AirspaceCategory", 1L));

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        put("/api/airspaceCategory/1")
                                .content(objectMapper.writeValueAsString(airspaceCategory))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void shouldGetAirspaceCategory() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        AirspaceCategory airspaceCategory = new AirspaceCategory("Sample", AirspaceCategory.Type.GREEN, geoJson);

        when(airspaceCategoryService.find(1)).thenReturn(airspaceCategory);

        //when
        MockHttpServletResponse response = mvc
                .perform(get("/api/airspaceCategory/1"))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        AirspaceCategory responseEntity = objectMapper.readValue(response.getContentAsString(), AirspaceCategory.class);
        assertThat(responseEntity.getGeoJson(), is(airspaceCategory.getGeoJson()));
        assertThat(responseEntity.getName(), is(airspaceCategory.getName()));
        assertThat(responseEntity.getType(), is(airspaceCategory.getType()));
    }

    @Test
    public void shouldGetAllAirspaceCategory() throws Exception {
        //given
        String geoJson = IOUtils.toString(this.getClass().getResourceAsStream("/geoJsonPolygon.json"), "UTF-8");
        AirspaceCategory airspaceCategoryOne = new AirspaceCategory("Sample1", AirspaceCategory.Type.GREEN, geoJson);
        AirspaceCategory airspaceCategoryTwo = new AirspaceCategory("Sample2", AirspaceCategory.Type.GREEN, geoJson);

        when(airspaceCategoryService.findAll()).thenReturn(asList(airspaceCategoryOne, airspaceCategoryTwo));

        //when
        MockHttpServletResponse response = mvc
                .perform(get("/api/airspaceCategory/list"))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        List<AirspaceCategory> responseList = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<AirspaceCategory>>() {
        });
        assertThat(responseList.size(), is(2));
        assertThat(responseList.get(0).getGeoJson(), is(airspaceCategoryOne.getGeoJson()));
        assertThat(responseList.get(1).getGeoJson(), is(airspaceCategoryTwo.getGeoJson()));
    }

    @Test
    public void shouldMakeSureOnlyAdminAccess() throws Exception {
        assertPreAuthorizeWithAdmin(postMethod(mvc, "/api/airspaceCategory", MediaType.APPLICATION_JSON));
        assertPreAuthorizeWithAdmin(putMethod(mvc, "/api/airspaceCategory/1", MediaType.APPLICATION_JSON));
    }
}