package com.ibbnjchurch.church_api.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ibbnjchurch.church_api.model.ERole;
import com.ibbnjchurch.church_api.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByName(ERole name);
}
