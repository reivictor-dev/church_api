package com.ibbnjchurch.church_api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibbnjchurch.church_api.payload.request.LoginRequest;
import com.ibbnjchurch.church_api.payload.request.SignupRequest;
import com.ibbnjchurch.church_api.payload.response.JwtResponse;
import com.ibbnjchurch.church_api.payload.response.MessageResponse;
import com.ibbnjchurch.church_api.repository.RoleRepository;
import com.ibbnjchurch.church_api.repository.UserRepository;
import com.ibbnjchurch.church_api.repository.files.ProfilePictureRepository;
import com.ibbnjchurch.church_api.security.jwt.JwtUtils;
import com.ibbnjchurch.church_api.services.user.UserDetailsImpl;
import com.ibbnjchurch.church_api.services.user.UserServices;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        @Autowired
        AuthenticationManager authenticationManager;

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

        @Autowired
        JwtUtils jwtUtils;

        @PostMapping("/signin")
        public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest LoginRequest) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(LoginRequest.getUsername(),
                                                LoginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities()
                                .stream()
                                .map(item -> item.getAuthority())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(
                                new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                                                userDetails.getEmail(), roles));
        }

        @PostMapping("/signup")
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

                userServices.createUserAndAuthenticate(signupRequest);

                return ResponseEntity.ok(new MessageResponse("User registered succesfully!"));
        }
}
