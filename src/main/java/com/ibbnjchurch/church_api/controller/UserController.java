package com.ibbnjchurch.church_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibbnjchurch.church_api.model.User;
import com.ibbnjchurch.church_api.payload.request.SignupRequest;
import com.ibbnjchurch.church_api.payload.response.MessageResponse;
import com.ibbnjchurch.church_api.repository.UserRepository;
import com.ibbnjchurch.church_api.security.jwt.JwtUtils;
import com.ibbnjchurch.church_api.services.user.UserServices;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServices userServices;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signupAdminOrMod")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already use!"));
        }

        userServices.createAdminModAndAuthenticate(signupRequest);

        return ResponseEntity.ok(new MessageResponse("User registered succesfully!"));
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable String id,
            @RequestBody User user) throws Exception {
        userServices.updateUser(id, user);

        return ResponseEntity.ok().body("http://localhost:8080/api/auth/signin");
    }

    @GetMapping("/allUsers")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userServices.findAll();
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable String id) {
        User user = userServices.findById(id);
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userServices.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
