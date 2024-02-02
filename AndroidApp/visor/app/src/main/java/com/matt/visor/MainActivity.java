/**
 * This class is part of the V.I.S.O.R app.
 * The MainActivity class is responsible for configuration of NavController and AppBar.
 *
 * @version 1.0
 * @since 03/04/2023
 */



package com.matt.visor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.matt.visor.app.MySensorGPS;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // AppBar
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_rides, R.id.navigation_history, R.id.navigation_config)
                .build();

        // NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //TODO ACTIVATE STUFF
        VisorApplication app = (VisorApplication) getApplication();
        MySensorGPS gps = (MySensorGPS) app.deviceManager.getGPS();
        gps.activateSensor(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}