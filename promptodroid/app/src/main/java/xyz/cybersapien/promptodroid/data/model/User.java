package xyz.cybersapien.promptodroid.data.model;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by ogcybersapien on 1/1/18.
 */

public class User {

    private String name;
    private String uid;
    private String email;
    private String userPicture;

    public User(@NonNull FirebaseUser user) {
        name = user.getDisplayName();
        uid = user.getUid();
        email = user.getEmail();
        if (user.getPhotoUrl() != null)
            userPicture = user.getPhotoUrl().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }
}
