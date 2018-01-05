package xyz.cybersapien.promptodroid;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

/**
 * Created by ogcybersapien on 31/12/17.
 */

public class PromptodroidApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        CrashlyticsCore crashCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, crashCore);
    }
}
