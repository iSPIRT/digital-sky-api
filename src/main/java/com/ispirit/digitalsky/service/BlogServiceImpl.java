package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.Blog;
import com.ispirit.digitalsky.domain.UserPrincipal;
import com.ispirit.digitalsky.repository.BlogRepository;
import com.ispirit.digitalsky.service.api.BlogService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class BlogServiceImpl implements BlogService {

    private BlogRepository blogRepository;

    public BlogServiceImpl(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    public Blog createNewBlog(Blog blog) {
        blog.resolveContentFromSections();
        blog.setCreatedBy(UserPrincipal.securityContext().getId());
        blog.setUpdatedBy(UserPrincipal.securityContext().getId());
        blog.setCreatedTime(LocalDateTime.now());
        blog.setUpdatedTime(LocalDateTime.now());
        Blog saveBlog = blogRepository.save(blog);
        saveBlog.resolveSectionsFromContent();
        return saveBlog;
    }

    @Override
    public Blog updateBlog(long id, Blog blog) {
        Blog currentEntity = blogRepository.findOne(id);
        currentEntity.setTitle(blog.getTitle());
        currentEntity.setSections(blog.getSections());
        currentEntity.setUpdatedBy(UserPrincipal.securityContext().getId());
        currentEntity.setUpdatedTime(LocalDateTime.now());
        currentEntity.resolveContentFromSections();
        Blog updatedEntity = blogRepository.save(currentEntity);
        updatedEntity.resolveSectionsFromContent();
        return updatedEntity;
    }

    @Override
    public Blog find(long id) {
        Blog blog = blogRepository.findOne(id);
        blog.resolveSectionsFromContent();
        return blog;
    }

    @Override
    public List<Blog> findLatest() {
        List<Blog> blogList = new ArrayList<>();
        for (Blog blog : blogRepository.findAll()) {
            blogList.add(blog);
        }
        blogList.sort((o1, o2) -> o2.getUpdatedTime().compareTo(o1.getUpdatedTime()));
        List<Blog> result = blogList.stream().limit(3).collect(toList());
        result.stream().forEach(blog -> blog.resolveSectionsFromContent());
        return result;
    }

    @Override
    public List<Blog> findAll() {
        List<Blog> blogList = new ArrayList<>();
        for (Blog blog : blogRepository.findAll()) {
            blog.resolveSectionsFromContent();
            blogList.add(blog);
        }
        return blogList;
    }
}
