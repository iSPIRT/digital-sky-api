package com.ispirit.digitalsky.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ispirit.digitalsky.document.UAOPApplication;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.util.*;

public class OrganizationOperatorTest {

    @Test
    public void name() throws Exception {
        Address director1 = new Address("line1", "line2", "Indiranagar", "Bangalore", "India", "560001", "HOME");
        Address director2 = new Address("line2", "line2", "Indiranagar", "Bangalore", "India", "560001", "HOME");
        Address org = new Address("line3", "line2", "Indiranagar", "Bangalore", "India", "560001", "HOME");

        List<Director> directors = new ArrayList<>();
        directors.add(new Director(1,"","name1","email","mobile","india",null,toAddressList(director1)));
        directors.add(new Director(2, "","name2","email","mobile","india",null,toAddressList(director2)));

        OrganizationOperator organizationOperator = new OrganizationOperator(1L, "", "name", "email", "mobile", "contact", "india", toAddressList(org));


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        System.out.println(objectMapper.writeValueAsString(organizationOperator));
        System.out.println(objectMapper.writeValueAsString(directors));
    }

    @Test
    public void sample() throws Exception {
        UAOPApplication uaopApplication = new UAOPApplication();
        uaopApplication.setName("abc");
        uaopApplication.setDesignation("xyz");

        UAOPApplication uaopApplicationNew = new UAOPApplication();
        uaopApplicationNew.setDesignation("xyz");

        BeanUtils.copyProperties(uaopApplicationNew, uaopApplication);
        System.out.println("");
    }

    @Test
    public void addDays() throws Exception {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.DAY_OF_MONTH,10);
        System.out.println(instance.getTime().toString());
    }

    List<Address> toAddressList(Address... addresses){
        ArrayList<Address> list = new ArrayList<>();
        Collections.addAll(list,addresses);
        return list;
    }
}