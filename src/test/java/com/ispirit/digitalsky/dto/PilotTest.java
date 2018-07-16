package com.ispirit.digitalsky.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ispirit.digitalsky.domain.Address;
import com.ispirit.digitalsky.domain.Pilot;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

public class PilotTest {

    @Test
    public void shouldSerialize() throws Exception {
        Address addressDtoHome = new Address("line1", "line2", "Indiranagar", "Bangalore", "India", "560001", "HOME");
        Address addressDtoOffice = new Address("line1", "line2", "Indiranagar", "Bangalore", "India", "560001", "OFFICE");
        Pilot value = new Pilot(1L, "", "name", "email", "phone", "India", LocalDate.of(1981, Month.AUGUST, 1), Arrays.asList(addressDtoHome, addressDtoOffice));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String asString = objectMapper.writeValueAsString(value);
        System.out.println(asString);
        Pilot x = objectMapper.readValue(asString, Pilot.class);
        System.out.println("x");
    }
}