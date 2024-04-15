/**
 * This class is part of the V.I.S.O.R app.
 * Represents user interface for testing connected HUD unit with the options of sending speed or images.
 *
 * @version 1.0
 * @since 08/02/2024
 */

package com.matt.visor.fragments.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.matt.visor.R;
import com.matt.visor.RideActivity;
import com.matt.visor.TableKvpAdapter;
import com.matt.visor.TableKvpItem;
import com.matt.visor.TableKvpOnClickListener;
import com.matt.visor.app.MySensorGPS;
import com.matt.visor.app.MySensorGPS_FAKE;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.databinding.FragmentHudDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class HudDetailFragment extends Fragment implements TableKvpOnClickListener {

    private FragmentHudDetailBinding _binding;
    private int _distance = 0;
    private int _speed = 0;

    /**
     * Initializes the fragment's view and sets up UI components.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The root View for the fragment's layout.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        VisorApplication app = (VisorApplication) requireActivity().getApplication();

        _binding = FragmentHudDetailBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();


        // Seekbar - speed

        _binding.sbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _binding.txtSpeed.setText(String.valueOf(progress));
                _speed = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendDataSpeedAndDistance();
            }
        });


        _binding.sbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _binding.txtDistance.setText(String.valueOf(progress));
                _distance = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendDataSpeedAndDistance();
            }
        });


        _binding.sbSpeed.setProgress(0);
        _binding.sbDistance.setProgress(0);


        // Buttons
        _binding.btnRunDemo.setOnClickListener(v -> onRunDemoClick());

        // Images
        List<TableKvpItem<?>> data = new ArrayList<>();

        int i = 0;
        for (Bitmap bitmap :  app.get_listOfImages()) {
            data.add(new TableKvpItem<>(String.valueOf(i),null, bitmap));
            i++;
        }

        // Recycler
        TableKvpAdapter _rva = new TableKvpAdapter(getContext(), data, R.layout.kvp_image, this);
        _binding.imageRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4));
        _binding.imageRecycler.setAdapter(_rva);

        // Root
        return root;
    }

    /**
     * Shows dialog with info about the demo and when YES clicked starts the demo.
     * Demo is started by replacing the GPS sensor with fake one and redirecting the ride activity.
     */
    private void onRunDemoClick() {

        new AlertDialog.Builder(getContext(), R.style.MyDialogTheme)
                .setTitle(R.string.dialog_confirm)
                .setMessage("The demo feature will override device's GPS and will follow a pre-set route until you choose to stop, or reach the destination. \n\n You can stop the demo by saving or discarding the ride and pressing the back button.   \n\n Would you like to start?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    VisorApplication app = (VisorApplication) requireActivity().getApplication();
                    app.deviceManager.setGPS(new MySensorGPS_FAKE());
                    app.saveNavigation(MySensorGPS_FAKE.getFakeSteps(), MySensorGPS_FAKE.getFacePoly());

                    Intent intent = new Intent(this.getActivity(), RideActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("No", (dialog, which) -> {})
                .show();
    }

    /**
     * Sends the current speed and distance to the device.
     *
     */
    private void sendDataSpeedAndDistance() {
        VisorApplication app = (VisorApplication) requireActivity().getApplication();
        app.deviceManager.getHUD().sendSpeedAndDistance(_speed, _distance);
    }

    /**
     * Handles item click events in the TableKvpAdapter and sends image to the device.
     *
     * @param item The TableKvpItem that was clicked.
     */
    @Override
    public void onItemClick(TableKvpItem item) {
        System.out.println("On item click : " + item.getKey());

        VisorApplication app = (VisorApplication) requireActivity().getApplication();
        if(app.deviceManager.getHUD().isConnected())
            app.deviceManager.getHUD().sendNav(Integer.parseInt(item.getKey()), 0);
        else
            Toast.makeText(getContext(), "Not connected",  Toast.LENGTH_SHORT).show();
    }

    /**
     * Cleans up resources when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }


    /**
     * Removes Fake GPS sensor on load - if fake gps is used.
     */
    @Override
    public void onResume() {
        super.onResume();

        VisorApplication app = (VisorApplication) requireActivity().getApplication();
        if(app.deviceManager.getGPS() instanceof MySensorGPS_FAKE){
            app.deviceManager.setGPS(new MySensorGPS());
            app.deviceManager.getGPS().activateSensor(getActivity());
            app.unSaveNavigation();


            new AlertDialog.Builder(getContext(), R.style.MyDialogTheme)
                    .setTitle("Demo ended")
                    .setMessage("Demo feature ended and the override is canceled. The application is now using the phone's GPS. ")
                    .setPositiveButton("OK", (dialog, which) -> {})
                    .show();
        }
    }
}