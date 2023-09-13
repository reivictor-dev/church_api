package com.ibbnjchurch.church_api.services.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibbnjchurch.church_api.model.User;
import com.ibbnjchurch.church_api.repository.UserRepository;
import com.ibbnjchurch.church_api.security.jwt.JwtUtils;

@Service
public class UserServices {

    @Autowired
    UserRepository repository;

    @Autowired
    JwtUtils jwtUtils;

    public List<User> findAll() {
        var allUsers = repository.findAll();
        return allUsers;
    }

    public User findById(String id) {
        var foundedUserById = repository.findById(id).orElse(null);
        return foundedUserById;
    }

    public void deleteUser(String id) {
        var foundedUserById = repository.findById(id).orElse(null);

        repository.delete(foundedUserById);
    }
}
