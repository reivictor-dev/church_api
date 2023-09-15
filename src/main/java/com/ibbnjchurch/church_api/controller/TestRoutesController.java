package com.ibbnjchurch.church_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibbnjchurch.church_api.repository.UserRepository;
import com.ibbnjchurch.church_api.services.user.UserServices;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestRoutesController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServices services;

    @GetMapping
    public String AllAccess() {
        return "Public Content";
    }

    @PostMapping("/user")
    // @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess() {
        return "user Test";
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    public String modAccess() {
        return "Mod Test";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board";
    }

}
