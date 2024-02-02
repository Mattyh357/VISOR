package com.matt.visor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.matt.visor.app.VisorApplication;

public class SplashScreen extends AppCompatActivity {

    private ProgressBar _progressBar;

    @SuppressWarnings("FieldCanBeLocal")
    private final int WAIT_TIME = 2100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        _progressBar = findViewById(R.id.splash_progressBar);

        System.out.println("Splash onCreate");


        new Thread(() -> {
            loadStuff();
            waitForNoReason();
            startApp();
            finish();
        }).start();

    }


    /**
     * TODO comments and implementation
     */
    private void loadStuff() {
        VisorApplication app = (VisorApplication) getApplication();
        app.loadDevices(this);
//        app.loadListOfJourneys(this);
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