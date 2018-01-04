package xyz.cybersapien.promptodroid.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by ogcybersapien on 1/1/18.
 */

public class User implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.uid);
        dest.writeString(this.email);
        dest.writeString(this.userPicture);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.uid = in.readString();
        this.email = in.readString();
        this.userPicture = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
