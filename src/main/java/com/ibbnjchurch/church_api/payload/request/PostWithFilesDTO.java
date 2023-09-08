package com.ibbnjchurch.church_api.payload.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ibbnjchurch.church_api.model.Post;

public class PostWithFilesDTO {
    private Post post;
    private List<MultipartFile> files;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }

}
