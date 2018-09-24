package com.ispirit.digitalsky.service;

import com.ispirit.digitalsky.domain.Manufacturer;
import com.ispirit.digitalsky.exception.ManufacturerIdInvalidException;
import com.ispirit.digitalsky.exception.ManufacturerProfileAlreadyExist;
import com.ispirit.digitalsky.repository.ManufacturerRepository;
import com.ispirit.digitalsky.repository.storage.StorageService;
import com.ispirit.digitalsky.service.api.ManufacturerService;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ManufacturerServiceImpl implements ManufacturerService {

    private ManufacturerRepository manufacturerRepository;
    private StorageService storageService;
    public static final String MANUFACTURER_DIGITALCERTIFICATE_ROOT_PATH = "manufacturer_digital_certificates";

    public ManufacturerServiceImpl(ManufacturerRepository manufacturerRepository, StorageService storageService) {
        this.manufacturerRepository = manufacturerRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public Manufacturer createNewManufacturer(Manufacturer manufacturer) {
        long resourceOwnerId = manufacturer.getResourceOwnerId();
        if (manufacturerRepository.loadByResourceOwner(resourceOwnerId) != null) {
            throw new ManufacturerProfileAlreadyExist();
        }
        Manufacturer savedManufacturer =  manufacturerRepository.save(manufacturer);

        if(manufacturer.getTrustedCertificateDoc() != null) {
            List documents = new ArrayList();
            documents.add(manufacturer.getTrustedCertificateDoc());
            storageService.storeUnderSection( documents, String.valueOf(savedManufacturer.getId()), MANUFACTURER_DIGITALCERTIFICATE_ROOT_PATH);
        }
        return savedManufacturer;
    }

    @Override
    @Transactional
    public Manufacturer updateManufacturer(long id, Manufacturer manufacturer) {
        manufacturer.setId(id);
        Manufacturer savedManufacturer =  manufacturerRepository.save(manufacturer);

        if(manufacturer.getTrustedCertificateDoc() != null) {
            List documents = new ArrayList();
            documents.add(manufacturer.getTrustedCertificateDoc());
            storageService.storeUnderSection(documents, String.valueOf(id), MANUFACTURER_DIGITALCERTIFICATE_ROOT_PATH);
        }
        return savedManufacturer;
    }

    @Override
    public Manufacturer find(long id) {
        return manufacturerRepository.findOne(id);
    }

    @Override
    public Manufacturer findByName(String orgName) {
        Manufacturer manufacturer = manufacturerRepository.findByName(orgName);
        return manufacturer;
    }

    @Override
    public String getDigitalCertificatePath(long manufacturerId) {
        Manufacturer manufacturer = manufacturerRepository.findOne(manufacturerId);

        if (manufacturer != null) {
            String fileName = MANUFACTURER_DIGITALCERTIFICATE_ROOT_PATH + "//" + manufacturerId + "//" + manufacturer.getTrustedCertificateDocName();
            Path path = storageService.load(fileName);
            return path.toString();
        }
        else {
            throw new ManufacturerIdInvalidException();
        }
    }
}