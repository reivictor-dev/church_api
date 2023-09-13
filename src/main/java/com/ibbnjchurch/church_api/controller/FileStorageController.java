package com.ibbnjchurch.church_api.controller;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibbnjchurch.church_api.services.files.FileStorageService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("api/file/")
public class FileStorageController {
    private Logger logger = Logger.getLogger(FileStorageController.class.getName());

    @Autowired
    FileStorageService service;

    @GetMapping("loadFile/{filename:.+}")
    public ResponseEntity<Resource> loadFile(
            @PathVariable String filename, HttpServletRequest request) throws Exception {

        Resource resource = service.loadFileAsResource(filename);

        String contentType = "";
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception e) {
            logger.info("Can't get the file type");
        }

        if (contentType.isBlank()) {
            contentType = "application/octet/stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

}
