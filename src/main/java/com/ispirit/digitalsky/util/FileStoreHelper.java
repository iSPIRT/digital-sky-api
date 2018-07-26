package com.ispirit.digitalsky.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import static java.lang.String.format;

public class FileStoreHelper {

    public static String resolveFileName(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) return null;
        String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String fileName = StringUtils.cleanPath(multipartFile.getName());
        return format("%s.%s", fileName, extension);
    }
}
