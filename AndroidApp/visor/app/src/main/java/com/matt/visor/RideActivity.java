package com.matt.visor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RideActivity extends AppCompatActivity {

    private TableKvpAdapter _rva;


    private int _counter = 0;


    private ImageButton _btnStartPauseSave;
    private ImageButton _btnDiscard;
    private ImageButton _btnSave;
    private ConstraintLayout _bottomBar;

    private boolean _running = false;
    private boolean _paused = false;

    private RecorderService myService;


    private boolean isServiceBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        _btnStartPauseSave = findViewById(R.id.ride_btn_startPauseSave);
        _btnDiscard = findViewById(R.id.ride_btn_discard);
        _btnSave = findViewById(R.id.ride_btn_save);
        _bottomBar = findViewById(R.id.ride_bottom_bar);


        // onClickLListeners
        _btnStartPauseSave.setOnClickListener(v -> {
            if(!_running && !_paused)
                onStartRide();
            else if (_running && !_paused) {
                onPauseRide();
            } else if (_running && _paused) {
                onResumeRide();
            }
        });

        _btnDiscard.setOnClickListener(v -> {
            onResetRide();
        });


        _btnSave.setOnClickListener(v -> onSaveRide());




        init();

    }




    private void onStartRide() {
        System.out.println("Start");

        _running = true;
        _paused = false;

        // Buttons
        _btnStartPauseSave.setImageResource(R.drawable.icon_ride_pause);
        showExtraIcons(false);
        //sps = pause


        startService();
    }

    private void onPauseRide() {
        System.out.println("Pause");

        _running = true;
        _paused = true;

        // Buttons
        _btnStartPauseSave.setImageResource(R.drawable.icon_ride_start);
        showExtraIcons(true);


        myService.pause();
    }

    private void onResumeRide() {
        System.out.println("Continue");

        _paused = false;

        // Buttons
        _btnStartPauseSave.setImageResource(R.drawable.icon_ride_pause);
        showExtraIcons(false);

        myService.resume();
    }

    private void onResetRide() {
        System.out.println("reset");

        stopService();
        init();
    }

    private void onSaveRide() {

        // TODO get all data...

        // TODO save data


        myService.getAllData();


        // Reset
        onResetRide();

    }








    private void startService() {
        Intent serviceIntent = new Intent(this, RecorderService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        isServiceBound = true;

    }

    private void stopService () {


        System.out.println("Stop bound ");

        if (isServiceBound) {
            unbindService(connection);
            isServiceBound = false;
        }

        System.out.println("Stopping service - intent");
        Intent serviceIntent = new Intent(this, RecorderService.class);
        stopService(serviceIntent);
    }


    private void showExtraIcons(boolean show) {
        if(show) {
            _bottomBar.setBackgroundResource(R.drawable.ride_bg_bottom_full);
            _btnDiscard.setVisibility(View.VISIBLE);
            _btnSave.setVisibility(View.VISIBLE);
        }
        else {
            _bottomBar.setBackgroundResource(R.drawable.ride_bg_bottom_half);
            _btnDiscard.setVisibility(View.INVISIBLE);
            _btnSave.setVisibility(View.INVISIBLE);
        }
    }


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



//    private void exitDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
//
//
//        builder.setCancelable(false);
//
//        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
//            dialog.dismiss();
//            ActivityCompat.requestPermissions((Activity)_context, new String[]{permission}, PERMISSION_ALL);
//        });
//
//        builder.setNegativeButton(R.string.no, (dialog, which) -> {
//            dialog.dismiss();
//            _callback.onPermissionDenied(permission);
//        });
//
//        AlertDialog alert = builder.create();
//        alert.show();
//    }


    private void init() {

        _running = false;
        _paused = false;

        _btnStartPauseSave.setImageResource(R.drawable.icon_ride_start);
        showExtraIcons(false);

        List<TableKvpItem> initData = new ArrayList<>();

        initData.add(new TableKvpItem("time", "MOVING TIME", "00:00:00"));
        initData.add(new TableKvpItem("distance", "DISTANCE (km)", String.valueOf(_counter)));
        initData.add(new TableKvpItem("avgSpeed", "AVG SPEED (km/h)", String.valueOf(_counter)));
        initData.add(new TableKvpItem("speed", "SPEED (km/h)", String.valueOf(_counter)));
        initData.add(new TableKvpItem("ascend", "TOTAL ASCEND (m)", String.valueOf(_counter)));
//        list.add(new TableKvpItem("value4", "??? (bpm)", String.valueOf(_counter)));
//        list.add(new TableKvpItem("value4", "CALORIES (kcal)", String.valueOf(counter)));

        _rva = new TableKvpAdapter(this, initData, R.layout.kvp_grid);
        RecyclerView rv2 = findViewById(R.id.rv_test2);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 1;
            }
        });
        rv2.setLayoutManager(gridLayoutManager);

        rv2.setAdapter(_rva);

    }














    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            RecorderService.LocalBinder binder = (RecorderService.LocalBinder) service;
            myService = binder.getService();
            myService.start(data -> printData(data));
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };


    private void printData(Map<String, Object> data) {
        System.out.println("onNewData: " + data);
        System.out.println("onNewData: " + data);
        System.out.println("onNewData: " + data);

        String time = "time";

        if(data != null)
            time = Utils.secondsToTime((Integer) data.get("time"));

        // Add formatted data and stuff
        Map<String, Object> formatted = new LinkedHashMap<>();
        formatted.put("time", time);

        runOnUiThread(() -> {
            _rva.update(formatted);
        });
    }

}