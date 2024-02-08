package com.matt.visor.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.matt.visor.databinding.FragmentSensorDetailBinding;

public class SensorDetail extends Fragment {

    private FragmentSensorDetailBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _binding = FragmentSensorDetailBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        // meh
        final TextView textView = _binding.text;
        textView.setText("SensorDetail");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}