package com.matt.visor.fragments.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.matt.visor.Journey;
import com.matt.visor.databinding.FragmentHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();


        List<Journey> list = new ArrayList<>();


        Journey journey = new Journey();
        journey.timeStarted = 1;
        journey.timeFinished = 2;
        journey.totalTime = 3;

        journey.totalDistance = 4;
        journey.totalElevationClimbed = 5;
        journey.totalElevationDescended = 6;
        journey.totalAvgSpeed = 7;

        list.add(journey);
        list.add(journey);
        list.add(journey);


        // RecyclerView
        HistoryRecyclerViewAdapter rva = new HistoryRecyclerViewAdapter(getContext(), list);
        _binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _binding.historyRecyclerView.setAdapter(rva);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}