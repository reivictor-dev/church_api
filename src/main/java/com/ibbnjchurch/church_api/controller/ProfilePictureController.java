package com.ibbnjchurch.church_api.controller;

import java.io.IOException;

import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ibbnjchurch.church_api.model.files.ProfilePicture;
import com.ibbnjchurch.church_api.security.jwt.JwtUtils;
import com.ibbnjchurch.church_api.services.UserDetailsImpl;
import com.ibbnjchurch.church_api.services.files.ProfilePictureService;

@RestController
@RequestMapping("/api")
public class ProfilePictureController {

    @Autowired
    ProfilePictureService service;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/addDefaultPicture")
    public String addDefaultPicture(
            @RequestParam("image") MultipartFile file) throws IOException {
        String id = service.addDefaultPicture(file);
        return "redirect:/profilePicture/" + id;
    }

    @PostMapping("/addProfilePicture")
    public String addProfilePicture(
            @RequestParam("title") String title,
            @RequestParam("image") MultipartFile file) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        var userId = userDetails.getId();
        System.out.println(userId);
        if (userId != null) {
            String id = service.addProfilePictureToUser(userId, title, file);
            return "redirect:/api/profilePicture/" + id;
        } else {

            return "redirect:/error";
        }

    }

    @GetMapping(value = "/profilePicture/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String id) {
        ProfilePicture profilePicture = service.getProfilePicture(id);

        if (profilePicture != null) {
            Binary imageBinary = profilePicture.getImage();
            byte[] imageBytes = imageBinary.getData();

            HttpHeaders headers = new HttpHeaders();
            return ResponseEntity.ok().headers(headers).body(imageBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
