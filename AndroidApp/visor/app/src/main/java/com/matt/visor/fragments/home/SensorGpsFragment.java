/**
 * This class is part of the V.I.S.O.R app.
 * The SensorGpsFragment class is responsible for basic info about gps sensor:
 * - map with current location marker
 * - Latitude
 * - Longitude
 * - Altitude
 * - Speed
 * - Last update time
 *
 * @version 1.0
 * @since 08/02/2024
 */

package com.matt.visor.fragments.home;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matt.visor.R;
import com.matt.visor.TableKvpAdapter;
import com.matt.visor.TableKvpItem;
import com.matt.visor.app.MySensorGPS;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.app.recorder.Formatter;
import com.matt.visor.databinding.FragmentSensorGpsBinding;

import java.util.ArrayList;
import java.util.List;

public class SensorGpsFragment extends Fragment {

    private FragmentSensorGpsBinding _binding;

    private static final int DEFAULT_ZOOM = 15;

    private Marker _mapMarker;
    private GoogleMap _map;
    private MySensorGPS _gps;
    private LatLng _lastLocation = new LatLng(0,0);
    private TableKvpAdapter _rva;


    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * Initializes map, details table, and GPS sensor listener.
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

        _binding = FragmentSensorGpsBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();


        //MAP
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.sensor_gps_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                LatLng location = new LatLng(-34, 151);  // Initial coordinates
                _mapMarker = googleMap.addMarker(new MarkerOptions().position(location).title("position"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
                _map = googleMap;
            });
        }


        // Details init
        List<TableKvpItem<?>> data = new ArrayList<>();
        data.add(new TableKvpItem<>("latitude", "Latitude", ""));
        data.add(new TableKvpItem<>("longitude", "Longitude", ""));
        data.add(new TableKvpItem<>("altitude", "Altitude", ""));
        data.add(new TableKvpItem<>("speed", "Speed", ""));
        data.add(new TableKvpItem<>("timeInSeconds", "Last update", ""));


        // RecyclerView
        _rva = new TableKvpAdapter(getContext(), data, R.layout.kvp_list, null);
        _binding.sensorGpsTableKvp.setLayoutManager(new LinearLayoutManager(getContext()));
        _binding.sensorGpsTableKvp.setAdapter(_rva);


        // GPS sensor
        _gps = app.deviceManager.getGPS();
        _gps.setValueChangedListener(this::onMapChange);

        return root;
    }

    /**
     * Handles map changes by updating the marker's position to the new location and passes the
     * location to a method that prints is.
     */
    public void onMapChange() {
        Location location = _gps.getLocation();

        if (_mapMarker != null && location != null) {
            LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());

            // Only change on actual update
            if(!_lastLocation.equals(newLocation)){
                _lastLocation = newLocation;

                // Update the marker's position
                _mapMarker.setPosition(newLocation);
                _map.moveCamera(CameraUpdateFactory.newLatLng(_mapMarker.getPosition()));
                displayDetails(location);
            }
        }
    }


    /**
     * Displays detailed information about a location.
     *
     * @param location The location object to display details for.
     */
    private void displayDetails(Location location) {
        if(location == null)
            return;

        // Format
        // TODO units
        _rva.update("latitude",  location.getLatitude());
        _rva.update("longitude",  location.getLongitude());
        _rva.update("altitude",  Formatter.formatDistance(location.getAltitude(), true));
        _rva.update("speed", Formatter.formatSpeed(location.getSpeed(), true));
        _rva.update("timeInSeconds", Formatter.epochToDateAndTime(location.getTime() / 1000));
    }


    /**
     * Detaches the GPS value changed listener when the fragment is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (_gps != null) {
            _gps.setValueChangedListener(null);
        }
    }

    /**
     * Cleans up resources, including map and binding, when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (_map != null) {
            _map.clear();
            _map = null;
        }

        _binding = null;
    }
}