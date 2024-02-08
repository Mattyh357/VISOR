package com.matt.visor.fragments.rides;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.matt.visor.GoogleMapsAPI.DirectionsCallback;
import com.matt.visor.GoogleMapsAPI.GoogleDirectionsAPI;
import com.matt.visor.GoogleMapsAPI.Waypoint;
import com.matt.visor.R;
import com.matt.visor.app.MySensor;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.databinding.FragmentRidesNavigateBinding;

import java.util.List;
import java.util.Map;

public class RidesNavigateFragment extends Fragment {

    private FragmentRidesNavigateBinding _binding;


    private Marker _mapMarkerOrigin;
    private Marker _mapMarkerDestination;
    private GoogleMap _map;

    private MySensor _sensor;
    private Polyline currentPolyline;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _binding = FragmentRidesNavigateBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        VisorApplication app = (VisorApplication) requireActivity().getApplication();
        _sensor = app.deviceManager.getGPS();


        //MAP
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.rides_navigate_gps_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {

                // Initial coordinates
                LatLng location = new LatLng(-34, 151);

                // Markers
                _mapMarkerOrigin = googleMap.addMarker(
                        new MarkerOptions()
                                .position(location)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .title("Origin")
                );
                _mapMarkerDestination = googleMap.addMarker(new MarkerOptions().position(location).title("Destination"));

                // Camera
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

                // Google map
                _map = googleMap;
                _map.setOnMapClickListener(this::onMapClick);


                updateStartPosition();

            });
        }
        //MAP

        // Buttons

        _binding.ridesNavigateBtnUpdateStart.setOnClickListener(v -> updateStartPosition());
        _binding.ridesNavigateBtnGetRoute.setOnClickListener(v -> getRoute());

        return root;
    }

    private String getApiKey() {

        try {
            ApplicationInfo app = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            String apiKey = bundle.getString("com.google.android.geo.API_KEY");

            return apiKey;
            // Now you can use the apiKey variable for whatever you need
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // Handle the error gracefully here
        }

        return "";
    }

    private void getRoute() {

        clearRoute();

        String key = getApiKey();

        LatLng origin = _mapMarkerOrigin.getPosition();
        LatLng destination = _mapMarkerDestination.getPosition();

        //TODO hardcoded stuff :)
        origin = new LatLng(56.4706, -3.0119);
        destination = new LatLng(56.4635, -2.9737);


        GoogleDirectionsAPI googleDirectionsAPI = new GoogleDirectionsAPI(key);

        googleDirectionsAPI.requestRoute(origin, destination, new DirectionsCallback() {
            @Override
            public void onSuccess(List<Waypoint> waypoints, List<LatLng> polylineRoute) {
                printHumanReadableDirections(waypoints);
                drawRouteOnMap(polylineRoute);
            }

            @Override
            public void onFail(String response) {
                System.out.println("Fail");
            }

            @Override
            public void onError(Exception e) {
                System.out.println("Error: " + e);
            }
        });

    }


    private void printHumanReadableDirections(List<Waypoint> list) {

        for (Waypoint waypoint : list) {
            StringBuilder waypointDetails = new StringBuilder();

            // Instruction
            waypointDetails.append("Instruction: ").append(waypoint.instruction).append("\n");

            // Distance
            waypointDetails.append("Distance: ").append(waypoint.distanceText).append(" (")
                    .append(waypoint.distanceValue).append(" meters)").append("\n");

            // Duration
            waypointDetails.append("Duration: ").append(waypoint.durationText).append(" (")
                    .append(waypoint.durationValue).append(" seconds)").append("\n");

            // Start Location
            waypointDetails.append("Start Location: Lat ").append(waypoint.startLocation.latitude)
                    .append(", Lng ").append(waypoint.startLocation.longitude).append("\n");

            // End Location
            waypointDetails.append("End Location: Lat ").append(waypoint.endLocation.latitude)
                    .append(", Lng ").append(waypoint.endLocation.longitude).append("\n");

            // Maneuver (if available)
            if (waypoint.maneuver != null && !waypoint.maneuver.isEmpty()) {
                waypointDetails.append("Maneuver: ").append(waypoint.maneuver).append("\n");
            }

            // Print the waypoint details
            Log.i("WaypointDetails", waypointDetails.toString());
        }

    }





    private void drawRouteOnMap(List<LatLng> polyline) {
        System.out.println("DRAWING");

        getActivity().runOnUiThread(() -> {
            if (_map != null) {

                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(polyline)
                        .width(10)
                        .color(Color.RED);

                currentPolyline = _map.addPolyline(polylineOptions);
            }
        });
    }






    private void updateStartPosition() {
        System.out.println("Updating start position");

        Map<String, Object> data = _sensor.getValues();

        if (_mapMarkerOrigin != null && data != null && data.size() > 0) {

            // Get the new latitude and longitude from your sensor
            double newLatitude = (double) data.get("latitude");
            double newLongitude = (double) data.get("longitude");

            LatLng newLocation = new LatLng(newLatitude, newLongitude);

            // Update the marker's position
            _mapMarkerOrigin.setPosition(newLocation);
            _map.animateCamera(CameraUpdateFactory.newLatLngZoom(_mapMarkerOrigin.getPosition(), 14));
        }
    }

    private void onMapClick(LatLng latLng) {
        clearRoute();
        _mapMarkerDestination.setPosition(latLng);
    }

    private void clearRoute() {
        // Clear the previous polyline if it exists
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}