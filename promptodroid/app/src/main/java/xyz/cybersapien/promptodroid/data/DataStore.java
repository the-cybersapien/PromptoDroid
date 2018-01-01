package xyz.cybersapien.promptodroid.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import xyz.cybersapien.promptodroid.data.model.User;
import xyz.cybersapien.promptodroid.utils.Constants;

/**
 * Created by ogcybersapien on 1/1/18.
 */

public class DataStore {

    private static DataStore dataStoreInstance;
    private FirebaseDatabase rootDatabaseInstance;

    public static DataStore getInstance() {
        if (dataStoreInstance == null) {
            dataStoreInstance = new DataStore();
        }
        return dataStoreInstance;
    }

    private DataStore() {
        rootDatabaseInstance = FirebaseDatabase.getInstance();
    }

    public DatabaseReference getUserDataReference(User user) {
        return rootDatabaseInstance.getReference(Constants.USER_KEY + "/" + user.getUid());
    }

}

