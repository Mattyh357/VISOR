/**
 * This class is part of the V.I.S.O.R app.
 * The RidesFragment class is responsible display two "buttons" that will take the user either to
 * RideActivity directly (free ride) and NavigateFragment (navigate to)
 *
 * @version 1.0
 * @since 08/02/2024
 */

package com.matt.visor.fragments.rides;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.matt.visor.R;
import com.matt.visor.RideActivity;
import com.matt.visor.databinding.FragmentRidesBinding;

public class RidesFragment extends Fragment {

    private FragmentRidesBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _binding = FragmentRidesBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();


        _binding.ridesButtonFreeRide.setOnClickListener(v -> {
            Intent intent = new Intent(this.getActivity(), RideActivity.class);
            startActivity(intent);
        });

        _binding.ridesButtonNavigateTo.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_rides_navigate);
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }


}