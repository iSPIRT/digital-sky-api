package com.ispirit.digitalsky.service.api;


import com.ispirit.digitalsky.domain.Director;

public interface DirectorService {

    Director createNewDirector(Director director);

    Director updateDirector(long id, Director director);

    Director find(long id);
}
