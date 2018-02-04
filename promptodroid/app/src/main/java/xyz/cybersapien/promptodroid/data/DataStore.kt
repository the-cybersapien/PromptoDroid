package xyz.cybersapien.promptodroid.data

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import xyz.cybersapien.promptodroid.data.model.User
import xyz.cybersapien.promptodroid.utils.USER_KEY

object DataStore {
    private val rootDatabaseInstance: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun getUserDataReference(user: User): DatabaseReference {
        return rootDatabaseInstance.getReference(USER_KEY + "/" + user.uid)
    }
}