package com.matt.visor.fragments.rides;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.matt.visor.databinding.FragmentRidesBinding;

public class RidesFragment extends Fragment {

    private FragmentRidesBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _binding = FragmentRidesBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();


        _binding.ridesButtonFreeRide.setOnClickListener(v -> Toast.makeText(getContext(), "FREE RIDE", Toast.LENGTH_SHORT).show());
        _binding.ridesButtonNavigateTo.setOnClickListener(v -> Toast.makeText(getContext(), "Navigate to", Toast.LENGTH_SHORT).show());

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}