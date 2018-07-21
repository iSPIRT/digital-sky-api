package com.ispirit.digitalsky.document;

import org.springframework.web.multipart.MultipartFile;

public class ApplicationForm {

   public ApplicationForm() {
        int i =0;
   }

    public LocalDroneAcquisitionApplicationForm getFrm() {
        return frm;
    }

    public void setFrm(LocalDroneAcquisitionApplicationForm frm) {
        this.frm = frm;
    }

    public LocalDroneAcquisitionApplicationForm frm;


    public MultipartFile securityClearanceDoc;
}
