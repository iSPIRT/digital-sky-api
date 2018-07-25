package com.ispirit.digitalsky.document;

import com.ispirit.digitalsky.domain.ModeOfAcquisition;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="importedDroneAcquisitionApplicationForms")
@TypeAlias("importedDroneAcquisitionApplicationForm")
@Persistent
public class ImportedDroneAcquisitionApplicationForm extends DroneAcquisitionApplicationForm {

    @Field("importMode")
    private ModeOfAcquisition acquisitionMode;

    public ModeOfAcquisition getAcquisitionMode() {
        return acquisitionMode;
    }

    public void setAcquisitionMode(ModeOfAcquisition acquisitionMode) {
        this.acquisitionMode = acquisitionMode;
    }

}
