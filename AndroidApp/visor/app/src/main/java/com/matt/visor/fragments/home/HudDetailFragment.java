// TODO remove after testing of HUD image sender

package com.matt.visor.fragments.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.matt.visor.app.CustomBinProcessor;
import com.matt.visor.app.HudUnit;
import com.matt.visor.app.SensorStatusListener;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.databinding.FragmentHudDetailBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HudDetailFragment extends Fragment implements View.OnClickListener, RecyclerViewAdapter.OnImageButtonClickListener{

    private FragmentHudDetailBinding _binding;



    //TODO test
    private TextView txt_status;
    private TextView txt_speed;

    private SeekBar sb_speed;

    private Button btn_sendSpeed;
    private Button btn_connect;

    private Button but_sendBig;


    CustomBinProcessor _cbp;
    //TODO test



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        VisorApplication app = (VisorApplication) requireActivity().getApplication();

        _binding = FragmentHudDetailBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        txt_status = _binding.txtStatus;
        txt_speed = _binding.txtSpeed;

        sb_speed = _binding.sbSpeed;
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

        btn_sendSpeed = _binding.btnSendSpeed;
        btn_sendSpeed.setOnClickListener(this);

        btn_connect = _binding.btnConnect;
        btn_connect.setOnClickListener(this);

        but_sendBig = _binding.btnSendBIG;
        but_sendBig.setOnClickListener(this);


        // Custom binary file
        File file = new File(getActivity().getFilesDir(), "images.cbi");
        _cbp = new CustomBinProcessor(file);

        // Recycler
        RecyclerViewAdapter _rva = new RecyclerViewAdapter(getContext(), this, GetImageList());
        _binding.imageRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        _binding.imageRecycler.setAdapter(_rva);




        // IMPORTANT STUFF

        app.deviceManager.getHUD().setStatusChangeListener(new SensorStatusListener() {
            @Override
            public void onChange() {
                txt_status.setText(app.deviceManager.getHUD().getStatusString());
            }
        });




        // meh
        return root;
    }


    private List<ImageButtonItem> GetImageList() {
        List<ImageButtonItem> list = new ArrayList<>();

        for (Bitmap bitmap : _cbp.getListOfBitmaps()) {
            list.add(new ImageButtonItem("X", bitmap));
        }

        return list;
    }


    private void sendBigData() {

        VisorApplication app = (VisorApplication) requireActivity().getApplication();

        File file = new File(getActivity().getFilesDir(), "images.cbi");

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

        app.deviceManager.getHUD().sendBootData(data);


    }

    private void sendDataSpeed(int speed) {

        VisorApplication app = (VisorApplication) requireActivity().getApplication();

        app.deviceManager.getHUD().sendData(HudUnit.SPEED_INSTRUCTION, speed);
    }




    @Override
    public void onClick(View view) {
        if(view.getId() == btn_connect.getId()) {
            VisorApplication app = (VisorApplication) requireActivity().getApplication();

            app.deviceManager.getHUD().connect();
            return;
        }

        if(view.getId() == btn_sendSpeed.getId()) {
            sendDataSpeed(sb_speed.getProgress());
        }
        else if(view.getId() == but_sendBig.getId()){
            sendBigData();
        }
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getContext(), "img: " + position, Toast.LENGTH_SHORT).show();

        VisorApplication app = (VisorApplication) requireActivity().getApplication();

        app.deviceManager.getHUD().sendData(HudUnit.NAVIGATION_IMG_INSTRUCTION,position);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}