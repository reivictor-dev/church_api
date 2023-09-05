package com.ibbnjchurch.church_api.model.files;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profile_picture")
public class ProfilePicture {

    @Id
    private String id;

    private String title = "profile_picture";

    private Binary image;

    public ProfilePicture() {
    }

    public ProfilePicture(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Binary getImage() {
        return image;
    }

    public void setImage(Binary image) {
        this.image = image;
    }

}
