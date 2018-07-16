package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.Director;
import com.ispirit.digitalsky.repository.DirectorRepository;
import com.ispirit.digitalsky.service.api.DirectorService;
import org.springframework.transaction.annotation.Transactional;

public class DirectorServiceImpl implements DirectorService {

    private DirectorRepository directorRepository;

    public DirectorServiceImpl(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    @Override
    @Transactional
    public Director createNewDirector(Director pilot) {
        return directorRepository.save(pilot);
    }

    @Override
    @Transactional
    public Director updateDirector(long id, Director pilot) {
        pilot.setId(id);
        return directorRepository.save(pilot);
    }

    @Override
    public Director find(long id) {
        return directorRepository.findOne(id);
    }
}
