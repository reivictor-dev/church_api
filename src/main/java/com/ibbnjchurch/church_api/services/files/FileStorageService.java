package com.ibbnjchurch.church_api.services.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ibbnjchurch.church_api.config.FileStorageConfig;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(FileStorageConfig fileStorageConfig) throws Exception {
        Path path = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath().normalize();

        this.fileStorageLocation = path;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new Exception("Could not create the directory", e);
        }
    }

    private String generateFilename(MultipartFile file) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_'at'_HH.mm.ss.SSS");
        String timestamp = dateFormat.format(new Date());

        String filenameOriginal = file.getOriginalFilename();
        String extension = "";
        if (filenameOriginal != null) {
            var lastIndex = filenameOriginal.lastIndexOf(".");
            if (lastIndex != -1) {
                extension = filenameOriginal.substring(lastIndex);
            }
        }
        String newFileName = "IBBNJ_Image" + timestamp + extension;

        return StringUtils.cleanPath(newFileName);

    }

    public String storeFile(MultipartFile file) throws Exception {

        try {
            String filename = generateFilename(file);
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (Exception e) {
            throw new Exception("Could not storage. Try again!", e);
        }
    }

    public Resource loadFileAsResource(String filename) throws Exception {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new Exception("File not found!");
            }
        } catch (Exception e) {
            throw new Exception("File not found " + filename, e);
        }
    }

    public void deleteFile(String filename) throws IOException {

        Path filePath = this.fileStorageLocation.resolve(filename).normalize();

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new IOException("Error to delete this file" + filename, e);
        }
    }
}
