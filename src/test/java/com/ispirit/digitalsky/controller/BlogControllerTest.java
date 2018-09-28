package com.ispirit.digitalsky.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ispirit.digitalsky.TestContext;
import com.ispirit.digitalsky.domain.Blog;
import com.ispirit.digitalsky.service.api.BlogService;
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
import static com.ispirit.digitalsky.HandlerMethodHelper.getMethod;
import static com.ispirit.digitalsky.HandlerMethodHelper.postMethod;
import static com.ispirit.digitalsky.HandlerMethodHelper.putMethod;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = BlogController.class, secure = false)
@Import({TestContext.class})
public class BlogControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BlogService blogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateNewBlog() throws Exception {
        //given
        Blog blog = new Blog();
        blog.setSections(asList("section-one", "section-two"));
        blog.setTitle("B1");

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        post("/api/blog")
                                .content(objectMapper.writeValueAsString(blog))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.CREATED.value()));
        ArgumentCaptor<Blog> argumentCaptor = ArgumentCaptor.forClass(Blog.class);
        verify(blogService).createNewBlog(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getSections(), is(blog.getSections()));
        assertThat(argumentCaptor.getValue().getTitle(), is(blog.getTitle()));
    }

    @Test
    public void shouldUpdateBlog() throws Exception {
        //given
        Blog blog = new Blog();
        blog.setSections(asList("section-one", "section-two"));
        blog.setTitle("B1");

        when(blogService.find(1L)).thenReturn(blog);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        put("/api/blog/1")
                                .content(objectMapper.writeValueAsString(blog))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        ArgumentCaptor<Blog> argumentCaptor = ArgumentCaptor.forClass(Blog.class);
        verify(blogService).updateBlog(eq(1L), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getSections(), is(blog.getSections()));
        assertThat(argumentCaptor.getValue().getTitle(), is(blog.getTitle()));
    }

    @Test
    public void shouldNotUpdateBlogIfNotFound() throws Exception {
        //given
        Blog blog = new Blog();
        blog.setSections(asList("section-one", "section-two"));
        blog.setTitle("B1");
        when(blogService.find(1L)).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc
                .perform(
                        put("/api/blog/1")
                                .content(objectMapper.writeValueAsString(blog))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus. NOT_FOUND.value()));
        verify(blogService, never()).updateBlog(anyLong(), any());
    }

    @Test
    public void shouldGetBlog() throws Exception {
        //given
        Blog blog = new Blog();
        blog.setSections(asList("section-one", "section-two"));
        blog.setTitle("B1");

        when(blogService.find(1L)).thenReturn(blog);

        //when
        MockHttpServletResponse response = mvc
                .perform(get("/api/blog/1"))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        Blog responseBlog = objectMapper.readValue(response.getContentAsString(), Blog.class);
        assertThat(responseBlog.getSections(), is(blog.getSections()));
        assertThat(responseBlog.getTitle(), is(blog.getTitle()));
    }

    @Test
    public void shouldNotGetIfBlogNotFound() throws Exception {
        //given
        when(blogService.find(1L)).thenReturn(null);

        //when
        MockHttpServletResponse response = mvc
                .perform(get("/api/blog/1"))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void shouldGetLatestBlogs() throws Exception {
        //given
        Blog blog = new Blog();
        blog.setSections(asList("section-one", "section-two"));
        blog.setTitle("B1");

        when(blogService.findLatest()).thenReturn(asList(blog));

        //when
        MockHttpServletResponse response = mvc
                .perform(get("/api/blog/latest"))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        List<Blog> blogs = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<Blog>>() {
        });
        assertThat(blogs. size(), is(1));
    }

    @Test
    public void shouldGetAllBlogs() throws Exception {
        //given
        Blog blog = new Blog();
        blog.setSections(asList("section-one", "section-two"));
        blog.setTitle("B1");

        when(blogService.findAll()).thenReturn(asList(blog));

        //when
        MockHttpServletResponse response = mvc
                .perform(get("/api/blog/list"))
                .andReturn()
                .getResponse();

        //then
        assertThat(response.getStatus(), is(HttpStatus.OK.value()));
        List<Blog> blogs = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<Blog>>() {
        });
        assertThat(blogs. size(), is(1));
    }

    @Test
    public void shouldMakeSureOnlyAdminAccess() throws Exception {
        assertPreAuthorizeWithAdmin(postMethod(mvc, "/api/blog", MediaType.APPLICATION_JSON));
        assertPreAuthorizeWithAdmin(putMethod(mvc, "/api/blog/1", MediaType.APPLICATION_JSON));
        assertPreAuthorizeWithAdmin(getMethod(mvc, "/api/blog/list"));
    }
}