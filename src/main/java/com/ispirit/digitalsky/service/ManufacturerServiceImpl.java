package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.Manufacturer;
import com.ispirit.digitalsky.domain.User;
import com.ispirit.digitalsky.exception.ManufacturerProfileAlreadyExist;
import com.ispirit.digitalsky.repository.ManufacturerRepository;
import com.ispirit.digitalsky.service.api.ManufacturerService;
import com.ispirit.digitalsky.service.api.UserService;
import org.springframework.transaction.annotation.Transactional;

public class ManufacturerServiceImpl implements ManufacturerService {

    private ManufacturerRepository manufacturerRepository;
    private UserService userService;

    public ManufacturerServiceImpl(ManufacturerRepository manufacturerRepository, UserService userService) {
        this.manufacturerRepository = manufacturerRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public Manufacturer createNewManufacturer(Manufacturer manufacturer) {
        long resourceOwnerId = manufacturer.getResourceOwnerId();
        if (manufacturerRepository.loadByResourceOwner(resourceOwnerId) != null) {
            throw new ManufacturerProfileAlreadyExist();
        }

        return manufacturerRepository.save(manufacturer);
    }

    @Override
    @Transactional
    public Manufacturer updateManufacturer(long id, Manufacturer manufacturer) {
        manufacturer.setId(id);
        return manufacturerRepository.save(manufacturer);
    }

    @Override
    public Manufacturer find(long id) {
        return manufacturerRepository.findOne(id);
    }

    @Override
    public Manufacturer findByName(String fullName) {
         User user = userService.findUserByName(fullName);
         Manufacturer manufacturer = manufacturerRepository.loadByResourceOwner(user.getId());
         return manufacturer;
    }
}