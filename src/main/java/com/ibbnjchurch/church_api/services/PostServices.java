package com.ibbnjchurch.church_api.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ibbnjchurch.church_api.model.Post;
import com.ibbnjchurch.church_api.model.files.Files;
import com.ibbnjchurch.church_api.repository.PostRepository;
import com.ibbnjchurch.church_api.repository.files.FileStorageRepository;
import com.ibbnjchurch.church_api.services.files.FileStorageService;
import com.ibbnjchurch.church_api.services.user.UserServices;

@Service
public class PostServices {

    @Autowired
    PostRepository postRepository;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    FileStorageRepository fileStorageRepository;

    @Autowired
    UserServices userServices;

    public List<Post> findAll() {
        System.out.println(userServices.getAuthenticatedUser());
        return postRepository.findAll();
    }

    public Post findById(String id) {
        return postRepository.findById(id)
                .orElse(null);
    }

    public Post createPost(String title, String text, List<MultipartFile> files) throws Exception {
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

                    if (file.getSize() == 0 && file.getOriginalFilename() == "") {
                        for (Files oneFile : fileEntities) {
                            fileStorageRepository.delete(oneFile);
                            fileStorageService.deleteFile(saveFile);
                            oneFile.setFilePath(null);
                            oneFile.setId(null);
                        }
                    }
                }
                post.setFiles(fileEntities);
            }

            post.setTitle(title);
            post.setText(text);
            post.setUser(userServices.getAuthenticatedUser());
            post.setCreatedAt(date);
            return postRepository.save(post);

        } catch (Exception e) {
            throw new Exception("Failed on authenticated user!", e);
        }
    }

    public Post updatePost(String id, String title, String text, List<MultipartFile> files) throws Exception {
        var postFoundedById = postRepository.findById(id).orElse(null);
        var postUserId = postFoundedById.getUser().getId();

        if (!postUserId.equals(userServices.getAuthenticatedUser().getId())) {
            throw new Exception("Error to update post, verify your credentials!");
        }
        var newEditedDate = new Date();

        postFoundedById.setTitle(title);
        postFoundedById.setText(text);
        if (files != null && !files.isEmpty()) {
            List<Files> fileEntities = new ArrayList<>();

            for (MultipartFile file : files) {
                String saveFile = fileStorageService.storeFile(file);
                Files fileEntity = new Files();
                fileEntity.setFilePath(saveFile);
                fileStorageRepository.save(fileEntity);
                fileEntities.add(fileEntity);

                if (file.getSize() == 0 && file.getOriginalFilename() == "") {
                    for (Files oneFile : fileEntities) {
                        fileStorageRepository.delete(oneFile);
                        fileStorageService.deleteFile(saveFile);
                        oneFile.setFilePath(null);
                        oneFile.setId(null);
                    }
                }
            }
            postFoundedById.setFiles(fileEntities);
        }
        postFoundedById.setCreatedAt(newEditedDate);

        return postRepository.save(postFoundedById);

    }

    public void deletePost(String id) throws Exception {
        var post = postRepository.findById(id).orElse(null);
        var postUserId = post.getUser().getId();

        if (!postUserId.equals(userServices.getAuthenticatedUser().getId())) {
            throw new Exception("Error to delete post, verify your credentials!");
        }

        if (post != null) {
            List<Files> nonEmptyFiles = post.getFiles().stream()
                    .filter(file -> file != null && file.getFilePath() != null)
                    .collect(Collectors.toList());

            for (Files file : nonEmptyFiles) {
                try {
                    fileStorageService.deleteFile(file.getFilePath());
                    fileStorageRepository.delete(file);
                } catch (Exception e) {
                    throw new Exception("Error deleting file: ", e.getCause());
                }
            }
        }

        postRepository.delete(post);
    }

}
