package com.ibbnjchurch.church_api.services.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ibbnjchurch.church_api.model.ERole;
import com.ibbnjchurch.church_api.model.Role;
import com.ibbnjchurch.church_api.model.User;
import com.ibbnjchurch.church_api.model.files.ProfilePicture;
import com.ibbnjchurch.church_api.payload.request.SignupRequest;
import com.ibbnjchurch.church_api.repository.RoleRepository;
import com.ibbnjchurch.church_api.repository.UserRepository;
import com.ibbnjchurch.church_api.repository.files.ProfilePictureRepository;

@Service
public class UserServices {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ProfilePictureRepository profilePictureRepository;

    @Autowired
    PasswordEncoder encoder;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Not authenticated user!");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElse(null);

        return user;

    }

    public User createUserAndAuthenticate(SignupRequest signupRequest) {
        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()), null, null);

        Set<String> newRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (newRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }

        ProfilePicture defaulPicture = profilePictureRepository.findByTitle("profile_picture");

        user.setProfilePicture(defaulPicture);
        user.setRoles(roles);
        userRepository.save(user);

        return user;
    }

    public User createAdminModAndAuthenticate(SignupRequest signupRequest) {
        User user = createUserAndAuthenticate(signupRequest);

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
        user.setRoles(roles);
        userRepository.save(user);

        return user;
    }

    public User updateUser(String id, User user) throws Exception {
        User foundedUserById = userRepository.findById(id).orElse(null);
        var loggedUser = getAuthenticatedUser();

        if (!loggedUser.getId().equals(foundedUserById.getId())) {
            throw new Exception("Try again later!");
        }

        String newPassword = encoder.encode(user.getPassword());

        foundedUserById.setUsername(user.getUsername());
        foundedUserById.setEmail(user.getEmail());
        foundedUserById.setPassword(newPassword);
        userRepository.save(foundedUserById);

        return foundedUserById;
    }

    public List<User> findAll() {
        var allUsers = userRepository.findAll();
        return allUsers;
    }

    public User findById(String id) {
        var foundedUserById = userRepository.findById(id).orElse(null);
        return foundedUserById;
    }

    public void deleteUser(String id) {
        var foundedUserById = userRepository.findById(id).orElse(null);

        userRepository.delete(foundedUserById);
    }

}
