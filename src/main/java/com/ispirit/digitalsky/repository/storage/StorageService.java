package com.ispirit.digitalsky.repository.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

    void init();

    void store(List<MultipartFile> files, String newDirectory);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String directory, String filename);

    void deleteAll();

}