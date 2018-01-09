package xyz.cybersapien.promptodroid

import android.support.multidex.MultiDexApplication

import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.database.FirebaseDatabase

import io.fabric.sdk.android.Fabric

/**
 * Created by ogcybersapien on 31/12/17.
 */

class PromptodroidApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        val database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)

        val crashCore = CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG).build()
        Fabric.with(this, crashCore)
    }
}