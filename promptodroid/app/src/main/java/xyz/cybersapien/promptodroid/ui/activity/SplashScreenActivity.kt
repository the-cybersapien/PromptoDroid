package xyz.cybersapien.promptodroid.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import xyz.cybersapien.promptodroid.R
import xyz.cybersapien.promptodroid.utils.isInternetConnected
import java.util.*

/**
 * Created by ogcybersapien on 24/1/18.
 */
class SplashScreenActivity : AppCompatActivity() {

    lateinit var fireAuth: FirebaseAuth
    lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_splash)

        fireAuth = FirebaseAuth.getInstance()
        authStateListener = createAuthStateListener()
    }

    override fun onStart() {
        super.onStart()
        fireAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        fireAuth.removeAuthStateListener(authStateListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            launchMainActivity()
        }
    }

    private fun createAuthStateListener(): FirebaseAuth.AuthStateListener =
            FirebaseAuth.AuthStateListener {
                val fireUser = it.currentUser
                if (fireUser == null) {
                    if (!isInternetConnected(this@SplashScreenActivity)) {
                        getAlertDialog().show()
                    } else {
                        // User is signed out
                        // check if user wants to exit
                        if (signInIntent == 1) {
                            Toast.makeText(this@SplashScreenActivity, R.string.back_to_exit, Toast.LENGTH_SHORT)
                                    .show()
                        } else if (signInIntent >= 2) {
                            finish()
                            return@AuthStateListener
                        }
                        startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setLogo(R.drawable.ic_computer)
                                .setTheme(R.style.AppTheme)
                                .setAvailableProviders(
                                        Arrays.asList<AuthUI.IdpConfig>(
                                                AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                                        )
                                ).build(), RC_SIGN_IN)
                        signInIntent++
                    }
                } else {
                    launchMainActivity()
                }
            }

    private fun launchMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    private fun getAlertDialog() =
            AlertDialog.Builder(this)
                    .setTitle(R.string.no_connect)
                    .setMessage(R.string.internet_req)
                    .setPositiveButton(R.string.open_intenet) { _, _ ->
                        startActivityForResult(Intent(Settings.ACTION_WIRELESS_SETTINGS), RC_INTERNET_SETTINGS)
                    }.setNegativeButton(R.string.close_app) { dialog, _ ->
                        dialog.dismiss()
                    }.setOnDismissListener {
                        finish()
                    }.create()

    companion object {
        const val RC_INTERNET_SETTINGS = -100
        const val RC_SIGN_IN = 200
        var signInIntent: Int = 0
    }
}