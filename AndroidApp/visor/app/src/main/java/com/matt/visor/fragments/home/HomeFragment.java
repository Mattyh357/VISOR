/**
 * This class is part of the V.I.S.O.R app.
 * The HomeFragment class is responsible for displaying basic info and links to:
 * - hud unit
 * - gps sensor
 * - all paired sensors
 *
 * @version 1.0
 * @since 08/02/2024
 */

package com.matt.visor.fragments.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.matt.visor.R;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding _binding;

    /**
     * Sets up HUD, and GPS card (status text + on click listener) and recyclerView for other sensors.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        VisorApplication app = (VisorApplication) requireActivity().getApplication();

        // Binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();


        // HUD
        app.deviceManager.getHUD().setStatusChangeListener(() -> {
            _binding.homeHudStatus.setText(app.deviceManager.getHUD().getStatusString());
        });
        _binding.homeHudButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_hud_detail);
        });
        _binding.homeHudStatus.setText(app.deviceManager.getHUD().getStatusString());
        // TODO navigation
        //TEST END


        // GPS
        _binding.homeBtnGps.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_sensor_gps);
        });
        // TODO navigation
        app.deviceManager.getGPS().setStatusChangeListener(() -> {
            _binding.homeGpsStatus.setText(app.deviceManager.getGPS().getStatusString());
        });
        _binding.homeGpsStatus.setText(app.deviceManager.getGPS().getStatusString());


        // RecyclerView for sensors
        SensorRecyclerViewAdapter _rva = new SensorRecyclerViewAdapter(getContext(), app.deviceManager.getAllSensors());
        _binding.myDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _binding.myDevicesRecyclerView.setAdapter(_rva);


        // return
        return root;
    }


    // TODO remove listener for sensors?
    // TODO on pause and resume


    @Override
    public void onDestroyView() {
        VisorApplication app = (VisorApplication) requireActivity().getApplication();

        app.deviceManager.getGPS().setStatusChangeListener(null);
        app.deviceManager.getHUD().setStatusChangeListener(null);
        _binding = null;


        super.onDestroyView();
    }
}