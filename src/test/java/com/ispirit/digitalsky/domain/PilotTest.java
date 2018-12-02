package com.ispirit.digitalsky.domain;


import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PilotTest {


    @Test
    public void shouldResolveDroneCategoryFromList() {
        //given
        Pilot pilot = new Pilot(1);
        pilot.getDroneCategoryTypes().add(DroneCategoryType.MEDIUM);
        pilot.getDroneCategoryTypes().add(DroneCategoryType.LARGE);

        //when
        pilot.resolveDroneCategoryFromList();

        //then
        assertThat(pilot.getDroneCategory(), is("MEDIUM;LARGE"));
    }

    @Test
    public void shouldResolveListFromDroneCategory() {
        //given
        Pilot pilot = new Pilot(1, "DRAFT","name","","","",null,"MEDIUM;LARGE",null);

        //when
        pilot.resolveResolveListFromDroneCategory();

        //then
        assertThat(pilot.getDroneCategoryTypes().size(), is(2));
        assertThat(pilot.getDroneCategoryTypes().contains(DroneCategoryType.LARGE), is(true));
        assertThat(pilot.getDroneCategoryTypes().contains(DroneCategoryType.MEDIUM), is(true));
    }

    @Test
    public void shouldResolveListFromSingleDroneCategory() {
        //given
        Pilot pilot = new Pilot(1, "DRAFT","name","","","",null,"LARGE",null);

        //when
        pilot.resolveResolveListFromDroneCategory();

        //then
        assertThat(pilot.getDroneCategoryTypes().size(), is(1));
        assertThat(pilot.getDroneCategoryTypes().contains(DroneCategoryType.LARGE), is(true));
    }

    @Test
    public void shouldResolveListFromEmptyDroneCategory() {
        //given
        Pilot pilot = new Pilot(1, "DRAFT","name","","","",null,"",null);

        //when
        pilot.resolveResolveListFromDroneCategory();

        //then
        assertThat(pilot.getDroneCategoryTypes().size(), is(0));
    }
}