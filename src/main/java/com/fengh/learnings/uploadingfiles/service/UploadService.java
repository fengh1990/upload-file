package com.fengh.learnings.uploadingfiles.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public interface UploadService {

    void store(MultipartFile file);

    List<Path> loadAll();

    Resource getFile(String fileName);
    void init();
    void deleteAll();
}
