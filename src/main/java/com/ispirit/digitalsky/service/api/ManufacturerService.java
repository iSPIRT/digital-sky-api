package com.ispirit.digitalsky.service.api;


import com.ispirit.digitalsky.domain.Manufacturer;
import com.ispirit.digitalsky.exception.ManufacturerExistsException;
import com.ispirit.digitalsky.exception.ManufacturerNotFoundException;
import com.ispirit.digitalsky.exception.ManufacturerTrustedCertificateNotFoundException;
import com.ispirit.digitalsky.exception.StorageException;

public interface ManufacturerService {

    Manufacturer createNewManufacturer(Manufacturer manufacturer) throws StorageException, ManufacturerExistsException;

    Manufacturer updateManufacturer(long id, Manufacturer manufacturer);

    Manufacturer find(long id);

    Manufacturer loadByBusinessIdentifier(String businessIdentifier);

    String getCAAndTrustedCertificatePath(long id) throws ManufacturerNotFoundException, ManufacturerTrustedCertificateNotFoundException;

}
