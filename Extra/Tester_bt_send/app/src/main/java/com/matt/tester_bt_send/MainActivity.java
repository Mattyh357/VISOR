package com.matt.tester_bt_send;

import static com.matt.tester_bt_send.BleHelper.NAVIGATION_IMG_INSTRUCTION;
import static com.matt.tester_bt_send.BleHelper.SPEED_INSTRUCTION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewAdapter.OnImageButtonClickListener {


    private TextView txt_status;
    private TextView txt_speed;

    private SeekBar sb_speed;

    private Button btn_sendSpeed;
    private Button btn_connect;

    private Button but_sendBig;

    private BleHelper _ble;


    CustomBinProcessor _cbp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txt_status = findViewById(R.id.txt_status);
        txt_speed = findViewById(R.id.txt_speed);

        sb_speed = findViewById(R.id.sb_speed);
        sb_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                txt_speed.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btn_sendSpeed = findViewById(R.id.btn_send_speed);
        btn_sendSpeed.setOnClickListener(this);

        btn_connect = findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(this);

        but_sendBig = findViewById(R.id.btn_send_BIG);
        but_sendBig.setOnClickListener(this);

        // BLE helper
        _ble = new BleHelper(this, txt_status);

        // Custom binary file
        File file = new File(getFilesDir(), "images.cbi");
        _cbp = new CustomBinProcessor(file);

        // Recycler
        RecyclerViewAdapter _rva;
        RecyclerView rv = (RecyclerView) findViewById(R.id.image_recycler);
        _rva = new RecyclerViewAdapter(this, this, GetImageList());
        rv.setLayoutManager((new GridLayoutManager(this, 3)));
        rv.setAdapter(_rva);
    }


    private List<ImageButtonItem> GetImageList() {
        List<ImageButtonItem> list = new ArrayList<>();

        for (Bitmap bitmap : _cbp.getListOfBitmaps()) {
            list.add(new ImageButtonItem("X", bitmap));
        }

        return list;
    }



    @Override
    public void onClick(View view) {
        if(view.getId() == btn_connect.getId()) {
            _ble.connect();
            return;
        }

        if(view.getId() == btn_sendSpeed.getId()) {
            sendDataSpeed(sb_speed.getProgress());
        }
        else if(view.getId() == but_sendBig.getId()){
            sendBigData();
        }
    }

    private void sendBigData() {
        File file = new File(getFilesDir(), "images.cbi");

        byte[] data = new byte[(int) file.length()];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(_ble.isConnected) {
            _ble.sendBootData(data);
        }
        else {
            txt_status.setText("not connected");
        }


    }

    private void sendDataSpeed(int speed) {
        if(_ble.isConnected) {
            _ble.sendData(SPEED_INSTRUCTION, speed);
        }
        else {
            txt_status.setText("not connected");
        }
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "img: " + position, Toast.LENGTH_SHORT).show();

        if(_ble.isConnected) {
            _ble.sendData(NAVIGATION_IMG_INSTRUCTION,position);
        }
        else {
            txt_status.setText("not connected");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        _ble.close();  // Close the BLE connection
    }

}