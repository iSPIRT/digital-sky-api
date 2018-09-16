package com.ispirit.digitalsky;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AssertionHelper {

    public static void assertPreAuthorizeWithAdmin(HandlerMethod handlerMethod){
        assertThat(handlerMethod.hasMethodAnnotation(PreAuthorize.class), is(true));
        assertThat(handlerMethod.getMethodAnnotation(PreAuthorize.class).value(), is("hasRole('ADMIN')"));
    }
}
