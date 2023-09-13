package com.ibbnjchurch.church_api.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibbnjchurch.church_api.model.ERole;
import com.ibbnjchurch.church_api.model.Role;
import com.ibbnjchurch.church_api.model.User;
import com.ibbnjchurch.church_api.model.files.ProfilePicture;
import com.ibbnjchurch.church_api.payload.request.SignupRequest;
import com.ibbnjchurch.church_api.payload.response.MessageResponse;
import com.ibbnjchurch.church_api.repository.RoleRepository;
import com.ibbnjchurch.church_api.repository.UserRepository;
import com.ibbnjchurch.church_api.repository.files.ProfilePictureRepository;
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
    RoleRepository roleRepository;

    @Autowired
    ProfilePictureRepository profilePictureRepository;

    @Autowired
    PasswordEncoder encoder;

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

        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()), null, null);

        Set<String> newRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (newRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            newRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;

                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        ProfilePicture defaulPicture = profilePictureRepository.findByTitle("profile_picture");

        user.setProfilePicture(defaulPicture);
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered succesfully!"));
    }

    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userServices.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
