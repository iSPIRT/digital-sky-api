package com.ispirit.digitalsky.controller;

import com.ispirit.digitalsky.domain.Blog;
import com.ispirit.digitalsky.dto.Errors;
import com.ispirit.digitalsky.service.api.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.ispirit.digitalsky.controller.BlogController.BLOG_RESOURCE_BASE_PATH;

@RestController
@RequestMapping(BLOG_RESOURCE_BASE_PATH)
public class BlogController {

    public static final String BLOG_RESOURCE_BASE_PATH = "/api/blog";

    private BlogService blogService;

    @Autowired
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addBlog(@Valid @RequestBody Blog blog) {
        Blog savedEntity = blogService.createNewBlog(blog);
        return new ResponseEntity<>(savedEntity, HttpStatus.CREATED);

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBlog(@PathVariable(value = "id") long id, @Valid @RequestBody Blog blogPayload) {
        Blog blog = blogService.find(id);

        if (blog == null) {
            return new ResponseEntity<>(new Errors("Blog not found"), HttpStatus.NOT_FOUND);
        }

        Blog updatedEntity = blogService.updateBlog(id, blogPayload);

        return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> get(@PathVariable("id") long id) {
        Blog blog = blogService.find(id);

        if (blog == null) {
            return new ResponseEntity<>(new Errors("Blog details not found"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(blog, HttpStatus.OK);
    }

    @RequestMapping(value = "/latest", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> getLatest() {
        List<Blog> blogList = blogService.findLatest();
        return new ResponseEntity<>(blogList, HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> getAll() {
        List<Blog> blogList = blogService.findAll();
        return new ResponseEntity<>(blogList, HttpStatus.OK);
    }
}
