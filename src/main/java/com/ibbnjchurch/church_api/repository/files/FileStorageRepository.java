package com.ibbnjchurch.church_api.repository.files;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ibbnjchurch.church_api.model.files.Files;

public interface FileStorageRepository extends MongoRepository<Files, String> {

}
