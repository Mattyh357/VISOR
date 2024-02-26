package com.matt.visor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.matt.visor.app.MySensorGPS;
import com.matt.visor.app.NavigatorThingy;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.app.recorder.Formatter;
import com.matt.visor.app.recorder.RecorderListener;

import java.util.List;

public class RideActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, RecorderListener {

//    private TableKvpAdapter _rva;
    private ImageButton _btnStartPauseSave;
    private ImageButton _btnDiscard;
    private ImageButton _btnSave;
    private ConstraintLayout _bottomBar;


    private TextView _txtElapsed;
    private TextView _txtDistance;
    private TextView _txtSpeed;
    private ImageView _imgManeuverIcon;
    private TextView _txtManeuverDistance;


    // Service
    private RecorderService _recorderService;
    private boolean isServiceBound = false;
    private boolean _running = false;
    private boolean _paused = false;


    // Map
    private static final int DEFAULT_ZOOM = 15;
    private GoogleMap _map;
    private Polyline _polylineActual;
    private Polyline _polylineRoute;
    private Marker _mapMarker;
    private MySensorGPS _gps;

    private NavigatorThingy _navigator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.rides_gps_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Buttons
        _btnStartPauseSave = findViewById(R.id.ride_btn_startPauseSave);
        _btnDiscard = findViewById(R.id.ride_btn_discard);
        _btnSave = findViewById(R.id.ride_btn_save);
        _bottomBar = findViewById(R.id.ride_bottom_bar);

        //Texts and images
        _txtElapsed = findViewById(R.id.rides_txt_elapsed);
        _txtDistance = findViewById(R.id.rides_txt_distance);
        _txtSpeed = findViewById(R.id.rides_txt_speed);
        _imgManeuverIcon = findViewById(R.id.rides_img_maneuverIcon);
        _imgManeuverIcon = findViewById(R.id.rides_img_maneuverIcon);
        _txtManeuverDistance = findViewById(R.id.rides_img_maneuverDistance);

        // onClickLListeners
        _btnDiscard.setOnClickListener(this);
        _btnSave.setOnClickListener(this);
        _btnStartPauseSave.setOnClickListener(this);

        // Initialize data fields
        resetView();
    }

    @Override
    public void onClick(View v) {
        // START PAUSE RESUME
        if(v.getId() == R.id.ride_btn_startPauseSave) {
            // START
            if(!_running && !_paused){
                _running = true;
                _paused = false;

                Intent serviceIntent = new Intent(this, RecorderService.class);
                ContextCompat.startForegroundService(this, serviceIntent);
                bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
                isServiceBound = true;
            }
            // PAUSE
            else if (_running && !_paused) {
                _paused = true;
                _recorderService.pause();
            }
            // RESUME
            else if (_running && _paused) {
                _paused = false;
                _recorderService.resume();
            }
        }
        // RESET
        else if(v.getId() == R.id.ride_btn_discard) {
            //TODO dialog
            unbindAndStopService();
            resetView();
        }
        else if(v.getId() == R.id.ride_btn_save) {
            // TODO dialog
            saveRide();
            unbindAndStopService();
            resetView();
        }


        setBottomBar();
    }



    private void unbindAndStopService() {
        if (isServiceBound) {
            unbindService(connection);
            isServiceBound = false;
        }

        Intent serviceIntent = new Intent(this, RecorderService.class);
        stopService(serviceIntent);
    }

    private void saveRide() {

        // TODO get all data...


        System.out.println("Saving route");

        // TODO center map on the thing I want to save

        // TODO display saving loading bar
        _recorderService.saveData(_map, this);

        // Hide loading bar with some on some listener
    }


    private void setBottomBar() {
        // Start pause icon
        if (_running && !_paused) {
            _btnStartPauseSave.setImageResource(R.drawable.icon_ride_pause);
        } else {
            _btnStartPauseSave.setImageResource(R.drawable.icon_ride_start);
        }

        // background
        if (_running && _paused) {
            _bottomBar.setBackgroundResource(R.drawable.ride_bg_bottom_full);
        } else {
            _bottomBar.setBackgroundResource(R.drawable.ride_bg_bottom_half);
        }

        // Visibility save / discard buttons
        int visibility = (_running && _paused) ? View.VISIBLE : View.INVISIBLE;
        _btnDiscard.setVisibility(visibility);
        _btnSave.setVisibility(visibility);
    }




    private void resetView() {
        _running = false;
        _paused = false;
        setBottomBar();

        _txtElapsed.setText("00:00:00");
        _txtSpeed.setText("0");
        _txtDistance.setText("0");
    }

    /*

        BACK BUTTON

     */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        if(_running){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle(R.string.dialog_confirm);
            builder.setMessage("Please stop ride first"); // TODO text
            builder.setPositiveButton("OK", (dialog, id) -> {});

            AlertDialog alert = builder.create();
            alert.show();
        }

        else
            super.onBackPressed();
    }







    /*

    SERVICE

     */






    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            RecorderService.LocalBinder binder = (RecorderService.LocalBinder) service;
            _recorderService = binder.getService();
            _recorderService.start(RideActivity.this);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };




    @Override
    public void onNewData(double speed, double distance, LatLng latLng) {

        sendDataToHud(speed, latLng);


        runOnUiThread(() -> {
            // Polyline
            List<LatLng> points = _polylineActual.getPoints();
            points.add(latLng);
            _polylineActual.setPoints(points);

            //Data fields
            _txtDistance.setText(Formatter.formatDistance(distance, false));
            _txtSpeed.setText(Formatter.formatSpeed(speed, false));

            //Image
            if(_navigator != null) {
                //_imgNavIcon
                System.out.println("Nav: " + _navigator.getManeuverString());

                VisorApplication app = (VisorApplication) getApplication();

                _imgManeuverIcon.setImageBitmap(app.getImageForId(_navigator.getManeuverID()));
                _txtManeuverDistance.setText(Formatter.formatDistance(_navigator.getDistanceToNext(), true));
            }
        });
    }

    private void sendDataToHud(double speed, LatLng latLng) {

        VisorApplication app = (VisorApplication) getApplication();

        if(_navigator == null) {
            int speedRounded = (int)speed;
            app.deviceManager.getHUD().sendSpeed(speedRounded);
            return;
        }

        if(_navigator.update(latLng))
            app.deviceManager.getHUD().sendImg(_navigator.getManeuverID());

    }

    @Override
    public void onEstimatedTimeChange(int elapsedTime) {
        runOnUiThread(() -> _txtElapsed.setText(Formatter.secondsAsElapsedTime(elapsedTime)));
    }

    @Override
    public void onSavingComplete() {
        System.out.println("COMPLETE");
        System.out.println("COMPLETE");
        System.out.println("COMPLETE");
        System.out.println("COMPLETE");
        System.out.println("COMPLETE");
        System.out.println("COMPLETE");
    }






    /*

    MAP

     */




    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        VisorApplication app = (VisorApplication) getApplication();

        // Initial coordinates
        Location last = app.deviceManager.getGPS().getLocation();
        LatLng latLng;

        if(last == null)
            latLng = new LatLng(0, 0);
        else
            latLng = new LatLng(last.getLatitude(), last.getLongitude());

        _mapMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        _map = googleMap;

        // ROUTE
        if(app.isNavigationSaved()){
            // TODO steps
            _navigator = new NavigatorThingy(app.getSteps());
            drawRouteOnMap(app.getPolylineRoute());
        }


        // Initialize polyline
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(15)
                .color(Color.RED);
        _polylineActual = _map.addPolyline(polylineOptions);

        _gps = app.deviceManager.getGPS();
        _gps.setValueChangedListener(() -> {
            Location location = _gps.getLocation();
            if (_mapMarker != null && location != null) {
                // Update the marker's position
                _mapMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                _map.moveCamera(CameraUpdateFactory.newLatLng(_mapMarker.getPosition()));
            }
        });
    }


    private void drawRouteOnMap(List<LatLng> polyline) {
        System.out.println("DRAWING");

        runOnUiThread(() -> {
            if (_map != null) {

                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(polyline)
                        .width(10)
                        .color(Color.BLUE);

                _polylineRoute = _map.addPolyline(polylineOptions);
            }
        });
    }




}