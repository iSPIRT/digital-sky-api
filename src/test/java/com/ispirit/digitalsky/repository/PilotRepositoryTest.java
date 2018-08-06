package com.ispirit.digitalsky.repository;

import com.ispirit.digitalsky.domain.Address;
import com.ispirit.digitalsky.domain.Pilot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PilotRepositoryTest {

    @Autowired
    PilotRepository pilotRepository;

    @Test
    public void savePilotDetails() throws Exception {
        Address addressHome = new Address("HOME", "line1", "line2", "ml", "bangalore", "india", "560001");
        Address addressTwo = new Address("OFFICE", "line12", "line22", "ml", "bangalore", "india", "560001");
        Address addressHome1 = new Address("HOME", "line111", "line2", "ml", "bangalore", "india", "560001");
        Address addressTwo2 = new Address("OFFICE", "line12343", "line22", "ml", "bangalore", "india", "560001");
        Pilot pilot1 = new Pilot(1L, null, "Name", "email", "mobile", "India", LocalDate.of(1981, Month.AUGUST, 1), "", toAddressList(addressHome, addressTwo));
        Pilot pilot2 = new Pilot(2L, null, "Name", "email", "mobile", "India", LocalDate.of(1981, Month.AUGUST, 1), "", toAddressList(addressHome, addressTwo));

        Pilot savedPilot1 = pilotRepository.save(pilot1);
        Pilot savedPilot2 = pilotRepository.save(pilot2);

        pilot1 = pilotRepository.findOne(Long.valueOf(savedPilot1.getId()));


        pilot1.setAddressList(toAddressList(addressHome1, addressTwo2));

        pilotRepository.save(pilot1);
        System.out.println("x");
    }

    List<Address> toAddressList(Address... addresses) {
        ArrayList<Address> addresses1 = new ArrayList<>();
        Collections.addAll(addresses1, addresses);
        return addresses1;

    }
}