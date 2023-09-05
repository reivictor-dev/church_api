package com.ibbnjchurch.church_api.repository.files;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ibbnjchurch.church_api.model.files.ProfilePicture;

public interface ProfilePictureRepository extends MongoRepository<ProfilePicture, String> {

    ProfilePicture findByTitle(String title);
}
