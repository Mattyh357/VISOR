/**
 * This class is part of the V.I.S.O.R app.
 * Splash screen - Responsible for loading saved configuration, devices, and navigation images.
 *
 * @version 1.0
 * @since 0/01/2024
 */

package com.matt.visor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.matt.visor.app.VisorApplication;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private ProgressBar _progressBar;

    @SuppressWarnings("FieldCanBeLocal")
    private final int WAIT_TIME = 1500;

    private PermissionHelper _permissionHelper;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        _progressBar = findViewById(R.id.splash_progressBar);

        // Permissions
        List<String> listOfPermissions = getListOfPermissions();

        //Permission helper
        _permissionHelper = new PermissionHelper(this, listOfPermissions, new PermissionHelper.Callback() {
            @Override
            public void onAllPermissionsGranted() {
                System.out.println("all ok :) please continue");
                startLoading();
            }
            public void onPermissionDenied(String permission) {
                showDeniedDialog(permission);
            }
        });

        _permissionHelper.start();
    }

    /**
     * Hardcoded list of permissions required for proper function of the app
     * @return List of permissions
     */
    @NonNull
    private static List<String> getListOfPermissions() {
        List<String> listOfPermissions = new ArrayList<>();
        // Map
        listOfPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        listOfPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        listOfPermissions.add(Manifest.permission.INTERNET);

        // Bluetooth
        listOfPermissions.add(Manifest.permission.BLUETOOTH);
        listOfPermissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        listOfPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        listOfPermissions.add(Manifest.permission.BLUETOOTH_SCAN);

        //Service
        listOfPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
        return listOfPermissions;
    }

    /**
     *  Instantiates app loading process on a separate thread.
     */
    private void startLoading() {
        new Thread(() -> {
            loadStuff();
            waitForNoReason();
            startApp();
            finish();
        }).start();
    }


    /**
     * Load everything that needs to be loaded
     */
    private void loadStuff() {
        VisorApplication app = (VisorApplication) getApplication();
        app.loadDevices(this);

        app.LoadImages();
    }


    /**
     * Callback for the result from requesting permissions
     *
     * @param requestCode The request code passed in requestPermissions
     * @param permissions The requested permissions. Never null.
     * @param grantResults  The grant results for the corresponding permissions which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        _permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Displays a dialog indicating a permission has been denied and that app will not work.
     *
     * @param permission The name of the permission that was denied.
     */
    private void showDeniedDialog(String permission) {
        System.out.println("Permission DENIED: " + permission);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.permission_denied)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {});
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Starts the main activity of the application.
     */
    private void startApp() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * No comment
     */
    private void waitForNoReason() {
        int numOfSteps = 5;
        int sleepTime = (int)(WAIT_TIME / numOfSteps);

        for (int progress=0; progress<numOfSteps; progress+=1) {
            try {
                Thread.sleep(sleepTime);
                _progressBar.setProgress(progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}