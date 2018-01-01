package xyz.cybersapien.promptodroid.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import xyz.cybersapien.promptodroid.R;
import xyz.cybersapien.promptodroid.utils.NetUtils;

public class SplashScreen extends AppCompatActivity {

    public static final int RC_INTERNET_SETTINGS = -100;
    public static final int RC_SIGN_IN = 200;
    private static int signInIntent = 0;

    private FirebaseAuth fireAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash);

        fireAuth = FirebaseAuth.getInstance();
        authStateListener = getAuthStateListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fireAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fireAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            launchMainActivity();
        }
    }

    private FirebaseAuth.AuthStateListener getAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    if (!NetUtils.isInternetConnected(SplashScreen.this)) {
                        getAlertDialog().show();
                    } else {
                        // User is signed out
                        // Check if user wants to exit
                        if (signInIntent == 1) {
                            Toast.makeText(SplashScreen.this, "Press back again to exit!", Toast.LENGTH_SHORT).show();
                        } else if (signInIntent == 2) {
                            finish();
                            return;
                        }
                        startActivityForResult(AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setLogo(R.drawable.ic_computer)
                                .setTheme(R.style.AppTheme)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                                )).build(), RC_SIGN_IN);
                        signInIntent++;
                    }
                } else {
                    launchMainActivity();
                }
            }
        };
    }

    private void launchMainActivity() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
    }

    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("No Connectivity")
                .setMessage("First Run requires internet connection")
                .setPositiveButton("Open Internet Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), RC_INTERNET_SETTINGS);
                    }
                })
                .setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finish();
                    }
                });
        return builder.create();
    }
}
