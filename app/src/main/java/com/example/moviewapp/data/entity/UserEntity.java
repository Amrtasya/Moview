package com.example.moviewapp.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username;
    private String email;
    private String password;
    private String profileImage;
    private String bio;

    public UserEntity(String username, String email, String password,
                      String profileImage, String bio) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
        this.bio = bio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getBio() {
        return bio;
    }
}