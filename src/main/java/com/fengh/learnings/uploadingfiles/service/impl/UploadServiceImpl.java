package com.fengh.learnings.uploadingfiles.service.impl;

import com.fengh.learnings.uploadingfiles.config.UploadProperties;
import com.fengh.learnings.uploadingfiles.exception.UploadException;
import com.fengh.learnings.uploadingfiles.exception.UploadFileNotFoundException;
import com.fengh.learnings.uploadingfiles.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UploadServiceImpl implements UploadService {

    private final Path path;

    @Autowired
    public UploadServiceImpl(UploadProperties properties) {
        this.path = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new UploadException("文件为空");
            }
            Path path = Paths.get(Objects.requireNonNull(file.getOriginalFilename()));
            Path absolutePath = this.path.resolve(path).normalize().toAbsolutePath();
            if (!absolutePath.getParent().equals(this.path.toAbsolutePath())) {
                throw new UploadException("文件不能存储在当前文件夹以外");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, absolutePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UploadException(e.getMessage(), e);
        }
    }

    @Override
    public List<Path> loadAll() {
        try (Stream<Path> walk = Files.walk(this.path, 1);) {
            return walk.filter(s -> Files.isRegularFile(s)).map(this.path::relativize).collect(Collectors.toList());
        } catch (IOException e) {
            throw new UploadException("读取文件夹中所有文件操作失败", e);
        }
    }

    @Override
    public Resource getFile(String fileName) {
        Path realPath = this.path.resolve(fileName);
        try {
            UrlResource resource = new UrlResource(realPath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            throw new UploadFileNotFoundException("找不到文件，" + fileName);
        } catch (MalformedURLException e) {
            throw new UploadFileNotFoundException("找不到文件，" + fileName, e);
        }
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(this.path);
        } catch (IOException e) {
            throw new UploadException("初始化文件失败", e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            FileSystemUtils.deleteRecursively(this.path);
        } catch (IOException e) {
            throw new UploadException("删除文件出错",e);
        }
    }
}
