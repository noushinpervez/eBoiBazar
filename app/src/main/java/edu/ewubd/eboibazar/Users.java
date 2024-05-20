package edu.ewubd.eboibazar;

public class Users {

    String userId, name, email, photoURL;

    public Users(String userId, String name, String email, String photoURL) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.photoURL = photoURL;
    }

    public Users() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}