package xyz.cybersapien.promptodroid.data.model

import android.os.Parcelable
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.parcel.Parcelize

/**
 * Created by ogcybersapien on 1/1/18.
 */
@Parcelize
class User(
        var name: String? = null,
        var uid: String? = null,
        var email: String? = null,
        var userPicture: String? = null) : Parcelable {

    constructor(user: FirebaseUser) : this() {
        name = user.displayName
        uid = user.uid
        email = user.email
        userPicture = user.photoUrl?.toString()

    }
}
