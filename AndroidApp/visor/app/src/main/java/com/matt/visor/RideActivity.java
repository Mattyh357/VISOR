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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.matt.visor.app.MySensorGPS;
import com.matt.visor.app.MySensorGPS_FAKE;
import com.matt.visor.app.Navigator;
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

    private Navigator _navigator;
    private boolean _saveInProgress;


    /**
     * Initializes activity state, sets up UI components, and prepares map and navigation functionalities.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
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

        // if this is demo -> pretend that I clicked start
        VisorApplication app = (VisorApplication) getApplication();
        if(app.deviceManager.getGPS() instanceof MySensorGPS_FAKE){
            View view = new View(this);
            view.setId(R.id.ride_btn_startPauseSave);
            onClick(view);
        }

        // Not demo, but have navigation, so change the nav icon to straight to indicate forward
        _imgManeuverIcon.setImageBitmap(app.getImageForId(0));
        _txtManeuverDistance.setText("Let's go!");

    }

    /**
     * Handles button clicks for starting, pausing, and saving the ride as well as showing dialogs for confirmations.
     * Responsible for control buttons: Start, Pause, Stop, Discard, Save
     *
     * @param v The view that was clicked.
     */
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
            new AlertDialog.Builder(this, R.style.MyDialogTheme)
                    .setTitle(R.string.dialog_confirm)
                    .setMessage("Are you sure you want to discard the ride?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        unbindAndStopService();
                        resetView();
                    })
                    .setNegativeButton("No", (dialog, which) -> {})
                    .show();
        }
        // Save
        else if(v.getId() == R.id.ride_btn_save) {
            new AlertDialog.Builder(this, R.style.MyDialogTheme)
                    .setTitle(R.string.dialog_confirm)
                    .setMessage("Are you sure you want to stop and save the ride?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        saveRide();
                        unbindAndStopService();
                    })
                    .setNegativeButton("No", (dialog, which) -> {})
                    .show();
        }


        setBottomBar();
    }


    /**
     * Disconnects from the service and stops it.
     */
    private void unbindAndStopService() {
        if (isServiceBound) {
            unbindService(connection);
            isServiceBound = false;
        }

        Intent serviceIntent = new Intent(this, RecorderService.class);
        stopService(serviceIntent);
    }

    /**
     * Saves the current ride, triggers data preparation and storage processes.
     */
    private void saveRide() {
        System.out.println("Saving route");

        _saveInProgress = true;
        prepareMapForSave();

        _recorderService.saveData(_map, this);
    }

    /**
     * Prepares the map for saving by adjusting the view to include all relevant route points.
     */
    private void prepareMapForSave() {
        if(_polylineRoute != null)
            _polylineRoute.remove();

        if (_polylineActual != null && !_polylineActual.getPoints().isEmpty()) {
            List<LatLng> points = _polylineActual.getPoints(); // Get all points in the polyline
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : points) {
                builder.include(point);
            }

            LatLngBounds bounds = builder.build();
            // Move the camera
            _map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        }
    }

    /**
     * Updates the state and icons of the bottom navigation bar based on the current recording status.
     */
    private void setBottomBar() {
        // Start pause icon
        if (_running && !_paused) {
            _btnStartPauseSave.setImageResource(R.drawable.icon_ride_pause);
        } else if (_running && _paused) {
            _btnStartPauseSave.setImageResource(R.drawable.icon_ride_resume);
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


    /**
     * Resets the view to its initial state and updates the UI elements to reflect no active ride.
     */
    private void resetView() {
        _saveInProgress = false;
        _running = false;
        _paused = false;
        setBottomBar();

        if(_map != null && _mapMarker != null)
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(_mapMarker.getPosition(), DEFAULT_ZOOM));

        _txtElapsed.setText("00:00:00");
        _txtSpeed.setText("0");
        _txtDistance.setText("0");
    }

    /******************************
     **      BACK BUTTON         **
     ******************************/

    /**
     * Handles the action when the back arrow is clicked to have the same as when back button is clicked.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Overrides the default back button behavior to include a confirmation dialog when a ride is active.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        if(_running){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle(R.string.dialog_confirm);
            builder.setMessage("Please save or discard the ride first.");
            builder.setPositiveButton("OK", (dialog, id) -> {});

            AlertDialog alert = builder.create();
            alert.show();
        }

        else
            super.onBackPressed();
    }

    /**************************
     **      SERVICE         **
     **************************/

    private final ServiceConnection connection = new ServiceConnection() {

        /**
         * Establishes a connection with the RecorderService and starts the service.
         *
         * @param className The concrete component name of the service that has been connected.
         * @param service The IBinder of the Service's communication channel.
         */
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            RecorderService.LocalBinder binder = (RecorderService.LocalBinder) service;
            _recorderService = binder.getService();
            _recorderService.start(RideActivity.this);
        }

        /**
         * Handles the disconnection from the service.
         *
         * @param arg0 The concrete component name of the service that has been disconnected.
         */
        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    /**
     * Updates UI components with new data received from sensors or GPS.
     *
     * @param speed The current speed.
     * @param distance The total distance covered.
     * @param latLng The current GPS coordinates.
     */
    @Override
    public void onNewData(double speed, double distance, LatLng latLng) {

        if(_navigator != null)
            _navigator.update(latLng);

        sendDataToHUD(speed, distance);

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

    /**
     * Sends current navigation data to the HUD unit.
     *
     * @param speed The current speed.
     * @param distance The current distance covered.
     */
    private void sendDataToHUD(double speed, double distance) {

        VisorApplication app = (VisorApplication) getApplication();

        Double maneuverDistance = _navigator != null ? _navigator.getDistanceToNext() : null;
        int maneuverIcon = _navigator != null ? _navigator.getManeuverID() : -1;

        app.deviceManager.getHUD().sendData(speed, distance, maneuverDistance, maneuverIcon);
    }

    /**
     * Updates the elapsed time display on the UI thread.
     *
     * @param elapsedTime The new elapsed time to display.
     */
    @Override
    public void onEstimatedTimeChange(int elapsedTime) {
        runOnUiThread(() -> _txtElapsed.setText(Formatter.secondsAsElapsedTime(elapsedTime)));
    }

    /**
     * Displays a dialog confirming the successful save of the ride and resets the UI.
     *
     * @param journeyID The ID of the journey that was saved.
     */
    @Override
    public void onSavingComplete(String journeyID) {
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle(R.string.dialog_confirm);
            builder.setMessage("Ride saved.");
            builder.setPositiveButton("OK", (dialog, id) -> resetView());

            AlertDialog alert = builder.create();
            alert.show();
        });
    }


    /*********************
    **      MAP         **
    *********************/

    /**
     * Called when the map is ready for use, sets up the initial location and route if available.
     *
     * @param googleMap The GoogleMap object ready for interaction.
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
            _navigator = new Navigator(app.getSteps());
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
            if (_mapMarker != null && location != null && !_saveInProgress) {
                // Update the marker's position
                _mapMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                _map.moveCamera(CameraUpdateFactory.newLatLng(_mapMarker.getPosition()));
            }
        });
    }

    /**
     * Draws a predefined route on the map.
     *
     * @param polyline A list of LatLng points defining the route.
     */
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