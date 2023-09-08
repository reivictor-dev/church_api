package com.ibbnjchurch.church_api.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ibbnjchurch.church_api.model.Post;
import com.ibbnjchurch.church_api.model.User;
import com.ibbnjchurch.church_api.model.files.Files;
import com.ibbnjchurch.church_api.repository.PostRepository;
import com.ibbnjchurch.church_api.repository.files.FileStorageRepository;
import com.ibbnjchurch.church_api.services.files.FileStorageService;

@Service
public class PostServices {

    @Autowired
    PostRepository postRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    FileStorageRepository fileStorageRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post findById(String id) {
        return postRepository.findById(id)
                .orElse(null);
    }

    public Post createPost(String title, String text, User user, List<MultipartFile> files) throws Exception {
        try {
            Post post = new Post();

            Date date = new Date();
            if (files != null && !files.isEmpty()) {
                List<Files> fileEntities = new ArrayList<>();

                for (MultipartFile file : files) {
                    String saveFile = fileStorageService.storeFile(file);
                    Files fileEntity = new Files();
                    fileEntity.setFilePath(saveFile);
                    fileStorageRepository.save(fileEntity);
                    fileEntities.add(fileEntity);
                }
                post.setFiles(fileEntities);
            }
            post.setTitulo(title);
            post.setText(text);
            post.setUser(user);
            post.setCreatedAt(date);
            return postRepository.save(post);

        } catch (Exception e) {
            throw new Exception("Failed on authenticated user!", e);
        }
    }

    public void deletePost(String id) throws Exception {
        var post = postRepository.findById(id).orElse(null);

        if (post != null) {
            if (!post.getFiles().isEmpty()) {

                try {
                    for (Files file : post.getFiles()) {
                        fileStorageRepository.delete(file);
                        fileStorageService.deleteFile(file.getFilePath());
                    }
                } catch (Exception e) {
                    throw new Exception("Error deleting file: ", e.getCause());
                }

            }
        }

        postRepository.delete(post);
    }
}
