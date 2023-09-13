package com.ibbnjchurch.church_api.services.files;

import java.io.IOException;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ibbnjchurch.church_api.model.User;
import com.ibbnjchurch.church_api.model.files.ProfilePicture;
import com.ibbnjchurch.church_api.repository.UserRepository;
import com.ibbnjchurch.church_api.repository.files.ProfilePictureRepository;
import com.ibbnjchurch.church_api.security.jwt.JwtUtils;

@Service
public class ProfilePictureService {

    @Autowired
    ProfilePictureRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtUtils jwtUtils;

    public String addDefaultPicture(MultipartFile file) throws IOException {
        ProfilePicture picture = new ProfilePicture();
        try {
            picture.setImage(
                    new Binary(BsonBinarySubType.BINARY, file.getBytes()));
            picture = repository.insert(picture);

            return picture.getId();
        } catch (IOException e) {
            throw new IOException("Error! Verify the file! " + e.getMessage());
        }
    }

    public String addProfilePicture(String title, MultipartFile file) throws IOException {
        ProfilePicture picture = new ProfilePicture(title);
        try {
            picture.setImage(
                    new Binary(BsonBinarySubType.BINARY, file.getBytes()));
            picture = repository.insert(picture);

            return picture.getId();
        } catch (IOException e) {
            throw new IOException("Error! Verify the file! " + e.getMessage());
        }
    }

    public ProfilePicture getProfilePicture(String id) {
        return repository.findById(id).get();
    }

    public String addProfilePictureToUser(String userId, String title, MultipartFile file) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(null);

        if (!user.getId().equals(jwtUtils.authenticatedUser().getId())) {
            throw new Exception("Error to set the profile picture, verify your credentials!");
        }

        byte[] imageBytes = file.getBytes();

        Binary imageBinary = new Binary(imageBytes);

        ProfilePicture profilePicture = new ProfilePicture(title);
        profilePicture.setImage(imageBinary);
        repository.save(profilePicture);
        user.setProfilePicture(profilePicture);

        userRepository.save(user);

        return profilePicture.getId();

    }
}