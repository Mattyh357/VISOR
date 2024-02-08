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
import com.matt.visor.Utils;
import com.matt.visor.app.MySensor;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.databinding.FragmentSensorGpsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SensorGpsFragment extends Fragment {

    private FragmentSensorGpsBinding _binding;

    private static final int DEFAULT_ZOOM = 15;

    private Marker _mapMarker;
    private GoogleMap _map;
    private MySensor _sensor;
    private LatLng _lastLocation = new LatLng(0,0);
    private TableKvpAdapter _rva;


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
                // TODO zoom magic
                _map = googleMap;
            });
        }


        // Details init
        List<TableKvpItem> data = new ArrayList<>();
        data.add(new TableKvpItem("latitude", "Latitude", ""));
        data.add(new TableKvpItem("longitude", "Longitude", ""));
        data.add(new TableKvpItem("altitude", "Altitude", ""));
        data.add(new TableKvpItem("speed", "Speed", ""));
        data.add(new TableKvpItem("timeInSeconds", "Last update", ""));


        // RecyclerView
        _rva = new TableKvpAdapter(getContext(), data, R.layout.kvp_list);
        _binding.sensorGpsTableKvp.setLayoutManager(new LinearLayoutManager(getContext()));
        _binding.sensorGpsTableKvp.setAdapter(_rva);


        // GPS sensor
        _sensor = app.deviceManager.getGPS();
        _sensor.setValueChangedListener(this::onMapChange);

        return root;
    }

    /**
     * TODO comment
     * TODO null check
     */
    public void onMapChange() {
        Map<String, Object> data = _sensor.getValues();

        if (_mapMarker != null && data != null && data.size() > 0) {
            // Get the new latitude and longitude from your sensor
            double newLatitude = (double) data.get("latitude");
            double newLongitude = (double) data.get("longitude");

            LatLng newLocation = new LatLng(newLatitude, newLongitude);

            // Only change on actual update
            if(!_lastLocation.equals(newLocation)){
                _lastLocation = newLocation;

                // Update the marker's position
                _mapMarker.setPosition(newLocation);
                _map.moveCamera(CameraUpdateFactory.newLatLng(_mapMarker.getPosition()));
                displayDetails(data);
            }

        }

    }


    /**
     * Formats and displays data using KVP Adapter.
     *
     * @param data Data to be displayed - uses map with keys: latitude, longitude, altitude, speed, timeInSeconds
     */
    private void displayDetails(Map<String, Object> data) {
        if(data == null || data.size() == 0)
            return;

        // Format
        // TODO unit
        data.put("altitude", data.get("altitude") + " UNIT");
        data.put("speed", data.get("speed") + " UNIT");
        data.put("timeInSeconds", Utils.epochToDateTime((Long)data.get("timeInSeconds")));

        _rva.update(data);
    }



    @Override
    public void onPause() {
        super.onPause();
        if (_sensor != null) {
            _sensor.setValueChangedListener(null);
        }
    }

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