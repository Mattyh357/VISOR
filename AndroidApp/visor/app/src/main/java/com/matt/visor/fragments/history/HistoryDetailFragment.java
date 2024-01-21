package com.matt.visor.fragments.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.matt.visor.Journey;
import com.matt.visor.databinding.FragmentHistoryDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryDetailFragment extends Fragment {

    private FragmentHistoryDetailBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _binding = FragmentHistoryDetailBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        _binding.text.setText("History Detail");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}