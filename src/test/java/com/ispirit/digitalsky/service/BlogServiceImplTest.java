package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.SecurityContextHelper;
import com.ispirit.digitalsky.domain.Blog;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.repository.BlogRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlogServiceImplTest {

    private BlogRepository blogRepository;
    private BlogServiceImpl blogService;
    private UserPrincipal userPrincipal;

    @Before
    public void setUp() throws Exception {
        blogRepository = mock(BlogRepository.class);
        blogService = new BlogServiceImpl(blogRepository);
        userPrincipal = SecurityContextHelper.setUserSecurityContext();

    }

    @Test
    public void shouldCreateBlog() throws Exception {
        //given
        Blog blog = new Blog();
        blog.setSections(asList("section1", "section2"));
        blog.setTitle("title");

        when(blogRepository.save(blog)).thenReturn(blog);

        //when
        Blog result = blogService.createNewBlog(blog);

        //then
        assertThat(result.getCreatedBy(), is(userPrincipal.getId()));
        assertThat(result.getUpdatedBy(), is(userPrincipal.getId()));
        assertThat(result.getCreatedTime(), notNullValue());
        assertThat(result.getUpdatedTime(), notNullValue());
        assertThat(result.getContent(), is("section1<=section=>section2"));
    }

    @Test
    public void shouldUpdateBlog() throws Exception {
        //given
        Blog blog = new Blog();
        blog.setSections(asList("section3", "section4"));
        blog.setTitle("title");

        Blog current = new Blog();
        current.setContent("section1<=section=>section2");
        current.setTitle("old-title");
        when(blogRepository.findOne(1L)).thenReturn(current);
        when(blogRepository.save(current)).thenReturn(current);

        //when
        Blog result = blogService.updateBlog(1L, blog);

        assertThat(result.getUpdatedBy(), is(userPrincipal.getId()));
        assertThat(result.getContent(), is("section3<=section=>section4"));
        assertThat(result.getSections(), is(blog.getSections()));
    }

    @Test
    public void shouldFindBlog() throws Exception {
        Blog current = new Blog();
        current.setContent("section1<=section=>section2");
        current.setTitle("old-title");
        when(blogRepository.findOne(1L)).thenReturn(current);

        //when
        Blog result = blogService.find(1L);

        assertThat(result, is(current));
        assertThat(result.getSections(), is(asList("section1", "section2")));
    }


    @Test
    public void shouldFindLatestBlogs() throws Exception {
        Blog blogOne = new Blog();
        blogOne.setContent("section11<=section=>section12");
        blogOne.setUpdatedTime(LocalDateTime.of(2018, Calendar.AUGUST, 21, 0, 0));

        Blog blogTwo = new Blog();
        blogTwo.setContent("section21<=section=>section22");
        blogTwo.setUpdatedTime(LocalDateTime.of(2018, Calendar.AUGUST, 21, 2, 0));

        Blog blogThree = new Blog();
        blogThree.setContent("section31<=section=>section32");
        blogThree.setUpdatedTime(LocalDateTime.of(2018, Calendar.AUGUST, 21, 3, 0));

        Blog blogFour = new Blog();
        blogFour.setContent("section41<=section=>section42");
        blogFour.setUpdatedTime(LocalDateTime.of(2018, Calendar.AUGUST, 21, 4, 0));


        //given
        when(blogRepository.findAll()).thenReturn(asList(blogOne, blogTwo, blogThree, blogFour));


        //when
        List<Blog> result = blogService.findLatest();

        //then
        assertThat(result, is(asList(blogFour, blogThree, blogTwo)));
        assertThat(result.get(0).getSections(), is(asList("section41","section42")));
        assertThat(result.get(1).getSections(), is(asList("section31","section32")));
        assertThat(result.get(2).getSections(), is(asList("section21","section22")));
    }

    @Test
    public void shouldFindAllBlogs() throws Exception {
        Blog blogOne = new Blog();
        blogOne.setContent("section11<=section=>section12");
        blogOne.setUpdatedTime(LocalDateTime.of(2018, Calendar.AUGUST, 21, 0, 0));

        Blog blogTwo = new Blog();
        blogTwo.setContent("section21<=section=>section22");
        blogTwo.setUpdatedTime(LocalDateTime.of(2018, Calendar.AUGUST, 21, 2, 0));

        Blog blogThree = new Blog();
        blogThree.setContent("section31<=section=>section32");
        blogThree.setUpdatedTime(LocalDateTime.of(2018, Calendar.AUGUST, 21, 3, 0));

        Blog blogFour = new Blog();
        blogFour.setContent("section41<=section=>section42");
        blogFour.setUpdatedTime(LocalDateTime.of(2018, Calendar.AUGUST, 21, 4, 0));


        //given
        when(blogRepository.findAll()).thenReturn(asList(blogOne, blogTwo, blogThree, blogFour));


        //when
        List<Blog> result = blogService.findAll();

        //then
        assertThat(result, is(asList(blogOne, blogTwo, blogThree, blogFour)));
        assertThat(result.get(0).getSections(), is(asList("section11","section12")));
        assertThat(result.get(1).getSections(), is(asList("section21","section22")));
        assertThat(result.get(2).getSections(), is(asList("section31","section32")));
        assertThat(result.get(3).getSections(), is(asList("section41","section42")));
    }
}