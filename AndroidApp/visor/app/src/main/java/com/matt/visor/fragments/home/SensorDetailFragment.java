/**
 * This class is part of the V.I.S.O.R app.
 * The SensorDetailFragment is a placeholder class which is supposed to be used to display details
 * for connected sensor. Currently only displays the name.
 *
 * @version 1.0
 * @since 21/02/2024
 */


package com.matt.visor.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.matt.visor.app.MySensor;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.databinding.FragmentSensorDetailBinding;

public class SensorDetailFragment extends Fragment {

    private FragmentSensorDetailBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _binding = FragmentSensorDetailBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();


        Bundle bundle = getArguments();
        if(bundle != null){

            VisorApplication app = (VisorApplication) requireActivity().getApplication();
            int sensorType = bundle.getInt("SensorType");
            MySensor sensor = app.deviceManager.getSensor(sensorType);

            _binding.sensorDetailName.setText(sensor.getName());
        }

        // meh
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}