package com.ibbnjchurch.church_api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ibbnjchurch.church_api.model.Post;

public interface PostRepository extends MongoRepository<Post, String> {

}
