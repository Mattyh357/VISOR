package com.matt.visor.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.matt.visor.MySensor;
import com.matt.visor.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();




        List< MySensor> list = new ArrayList<>();

        list.add(new MySensor("MAP", null, MySensor.TYPE_GPS));
        list.add(new MySensor("Heart rate", null, MySensor.TYPE_HR));
        list.add(new MySensor("Cadence", "", MySensor.TYPE_CAD));
        list.add(new MySensor("Power1234", "", MySensor.TYPE_PWR));

        // RecyclerView
//        SensorRecyclerViewAdapter _rva = new SensorRecyclerViewAdapter(getContext(), app.deviceManager.getAllSensors());
        SensorRecyclerViewAdapter _rva = new SensorRecyclerViewAdapter(getContext(), list);
        _binding.myDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _binding.myDevicesRecyclerView.setAdapter(_rva);





        // meh
        final TextView textView = _binding.textHome;
        textView.setText("Home or something");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}