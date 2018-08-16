package com.ispirit.digitalsky.service.api;


import com.ispirit.digitalsky.domain.Blog;

import java.util.List;

public interface BlogService {

    Blog createNewBlog(Blog blog);

    Blog updateBlog(long id, Blog blog);

    Blog find(long id);

    List<Blog> findLatest();

    List<Blog> findAll();
}
