package com.ibbnjchurch.church_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ibbnjchurch.church_api.model.Post;
import com.ibbnjchurch.church_api.model.User;
import com.ibbnjchurch.church_api.services.PostServices;
import com.ibbnjchurch.church_api.services.UserDetailsServiceImpl;
import com.ibbnjchurch.church_api.services.files.FileStorageService;

@RestController
@RequestMapping(value = "/api/post")
public class PostController {

    @Autowired
    PostServices services;

    @Autowired
    UserDetailsServiceImpl serviceImpl;

    @Autowired
    FileStorageService FileStorageService;

    @PostMapping(value = "/createPost", consumes = { "multipart/form-data", "application/json" })
    public ResponseEntity<?> creatingPost(
            @RequestParam String title,
            @RequestParam String text,
            @RequestPart List<MultipartFile> files) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = serviceImpl.getAuthenticatedUser(authentication);

        for (MultipartFile file : files) {
            if (!isValidImageType(file) && !file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("File type not supported! For images make sure that are: JPG, JPEG, PNG;\n" + //
                                "For videos: MP4, MKV");
            }
        }
        var createdPost = services.createPost(title, text, user, files);
        return ResponseEntity.ok(createdPost);
    }

    @PutMapping(value = "/updatePost/{id}", consumes = { "multipart/form-data", "application/json" })
    public ResponseEntity<?> updatingPost(
            @PathVariable String id,
            @RequestParam String title,
            @RequestParam String text,
            @RequestPart List<MultipartFile> files) throws Exception {
        for (MultipartFile file : files) {
            if (!isValidImageType(file) && !file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("File type not supported! For images make sure that are: JPG, JPEG, PNG;\n" + //
                                "For videos: MP4, MKV");
            }
        }
        var updatedPost = services.updatePost(id, text, title, files);
        return ResponseEntity.ok(updatedPost);
    }

    @GetMapping(value = "/posts")
    public ResponseEntity<List<Post>> findAll() {
        List<Post> posts = services.findAll();
        return ResponseEntity.ok().body(posts);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Post> findById(@PathVariable String id) {
        Post postById = services.findById(id);
        return ResponseEntity.ok().body(postById);
    }

    @DeleteMapping(value = "/deletePost/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id) throws Exception {
        services.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isValidImageType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/png") || contentType.equals("image/jpeg")
                || contentType.equals("image/jpg") || contentType.equals("video/mp4")
                || contentType.equals("video/x-matroska") || contentType.equals("video/webm"));
    }
}
