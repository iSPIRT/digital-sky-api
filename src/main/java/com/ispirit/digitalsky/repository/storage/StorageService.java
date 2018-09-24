package com.ispirit.digitalsky.repository.storage;

import com.ispirit.digitalsky.exception.StorageException;
import com.ispirit.digitalsky.exception.StorageFileNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

    void init() throws StorageException;

    void store(List<MultipartFile> files, String newDirectory) throws StorageException;

    void storeUnderSection(List<MultipartFile> files, String newDirectory, String section);

    void store(String fileName, String content, String directory) throws StorageException;

    Stream<Path> loadAll() throws StorageException;

    Path load(String filename);

    Resource loadAsResource(String directory, String filename) throws StorageFileNotFoundException;

    void deleteAll();

}