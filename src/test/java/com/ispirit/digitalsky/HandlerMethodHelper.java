package com.ispirit.digitalsky;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.HandlerMethod;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


public class HandlerMethodHelper {

    public static HandlerMethod postMethod(MockMvc mvc, String url, MediaType mediaType) throws Exception {
        return (HandlerMethod) mvc
                .perform(
                        post(url)
                                .content("{}")
                                .contentType(mediaType.toString())
                )
                .andReturn().getHandler();
    }

    public static HandlerMethod putMethod(MockMvc mvc, String url, MediaType mediaType) throws Exception {
        return (HandlerMethod) mvc
                .perform(
                        put(url)
                                .content("{}")
                                .contentType(mediaType.toString())
                )
                .andReturn().getHandler();
    }

    public static HandlerMethod getMethod(MockMvc mvc, String url) throws Exception {
        return (HandlerMethod) mvc.perform(get(url)).andReturn().getHandler();
    }
}
