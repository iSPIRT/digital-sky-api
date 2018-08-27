package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.Manufacturer;
import com.ispirit.digitalsky.exception.ManufacturerProfileAlreadyExist;
import com.ispirit.digitalsky.repository.ManufacturerRepository;
import com.ispirit.digitalsky.service.api.ManufacturerService;
import org.springframework.transaction.annotation.Transactional;

public class ManufacturerServiceImpl implements ManufacturerService {

    private ManufacturerRepository manufacturerRepository;

    public ManufacturerServiceImpl(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
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
}