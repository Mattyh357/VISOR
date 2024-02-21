/**
 * This class is part of the V.I.S.O.R app.
 * HistoryFragment is responsible for displaying list of journeys got from JourneyLoader.
 *
 * @version 1.0
 * @since 21/02/2024
 */


package com.matt.visor.fragments.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.matt.visor.app.recorder.JourneyLoaderAndSaver;
import com.matt.visor.app.recorder.Journey;
import com.matt.visor.databinding.FragmentHistoryBinding;

import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding _binding;

    /**
     * Inflates the layout for the fragment's view and initializes the RecyclerView with journey data.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        List<Journey> list = JourneyLoaderAndSaver.getAllJourneys(getContext());

        // RecyclerView
        HistoryRecyclerViewAdapter rva = new HistoryRecyclerViewAdapter(getContext(), list);
        _binding.historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _binding.historyRecyclerView.setAdapter(rva);
        return root;
    }

    /**
     * Cleans up resources and nullifies the binding when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}