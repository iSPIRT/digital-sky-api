package com.ispirit.digitalsky.service.api;


import com.ispirit.digitalsky.domain.Manufacturer;

public interface ManufacturerService {

    Manufacturer createNewManufacturer(Manufacturer manufacturer);

    Manufacturer updateManufacturer(long id, Manufacturer manufacturer);

    Manufacturer find(long id);

}
