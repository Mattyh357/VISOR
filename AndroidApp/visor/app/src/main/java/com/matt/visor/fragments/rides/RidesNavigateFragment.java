/**
 * This class is part of the V.I.S.O.R app.
 * RidesNavigateFragment is responsible for requesting navigation direction between current location
 * and destination marker using Goggle API. Supports displaying the polyline between two points on
 * successful API request, as well as saving and removing, and navigating to the ride activity.
 *
 * @version 1.0
 * @since 20/02/2024
 */

package com.matt.visor.fragments.rides;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
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
import com.matt.visor.GoogleMap.DirectionsCallback;
import com.matt.visor.GoogleMap.GoogleDirectionsAPI;
import com.matt.visor.GoogleMap.Step;
import com.matt.visor.R;
import com.matt.visor.RideActivity;
import com.matt.visor.app.MySensorGPS;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.databinding.FragmentRidesNavigateBinding;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RidesNavigateFragment extends Fragment {

    private static final int DEFAULT_ZOOM = 15;
    private FragmentRidesNavigateBinding _binding;
    private Marker _mapMarkerOrigin;
    private Marker _mapMarkerDestination;
    private GoogleMap _map;
    private MySensorGPS _gps;
    private Polyline currentPolyline;
    private List<Step> _steps;
    private List<LatLng> _polylineRoute;

    /**
     * Inflates the layout for the navigation fragment and initializes map and GPS functionalities.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _binding = FragmentRidesNavigateBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        VisorApplication app = (VisorApplication) requireActivity().getApplication();
        _gps = app.deviceManager.getGPS();

        //MAP
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.rides_navigate_gps_map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {

                // Initial coordinates
                LatLng location = new LatLng(0, 0);

                // Markers
                _mapMarkerOrigin = googleMap.addMarker(
                        new MarkerOptions()
                                .position(location)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .title("Origin")
                );
                _mapMarkerDestination = googleMap.addMarker(new MarkerOptions().position(location).title("Destination"));

                // Camera
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));

                // Google map
                _map = googleMap;
                _map.setOnMapClickListener(this::onMapClick);

                updateStartPosition();
            });
        }

        // Buttons
        _binding.ridesNavigateBtnUpdateStart.setOnClickListener(v -> updateStartPosition());
        _binding.ridesNavigateBtnClearRoute.setOnClickListener(v -> clearRoute());
        _binding.ridesNavigateBtnStartRide.setOnClickListener(v -> {
            // Bundle the route...
            app.saveNavigation(_steps, _polylineRoute);

            Intent intent = new Intent(this.getActivity(), RideActivity.class);
            startActivity(intent);
        });

        return root;
    }

    /**
     * Retrieves the Google API key from application's metadata.
     *
     * @return The Google API key as a string, or an empty string if not found or an error occurs.
     */
    private String getApiKey() {
        try {
            ApplicationInfo app = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = app.metaData;
            return bundle.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * Requests the route from the current origin to the destination and updates the map.
     */
    private void getRoute() {

        clearRoute();

        LatLng origin = _mapMarkerOrigin.getPosition();
        LatLng destination = _mapMarkerDestination.getPosition();

        //TODO hardcoded stuff :)
//        origin = new LatLng(56.4706, -3.0119);
//        destination = new LatLng(56.4635, -2.9737);


        GoogleDirectionsAPI googleDirectionsAPI = new GoogleDirectionsAPI(getApiKey());

        googleDirectionsAPI.requestRoute(origin, destination, new DirectionsCallback() {
            @Override
            public void onSuccess(List<Step> steps, List<LatLng> polylineRoute) {
                _steps = steps;
                _polylineRoute = polylineRoute;
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

    /**
     * Draws the specified route on the map using a polyline.
     *
     * @param polyline The list of LatLng points that form the route to be drawn on the map.
     */
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

    /**
     * Updates the start position marker to the current GPS location and moves the map camera.
     */
    private void updateStartPosition() {
        Location location = _gps.getLocation();

        if (_mapMarkerOrigin != null && location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Update the marker's position
            _mapMarkerOrigin.setPosition(latLng);
            _map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

            // Fill values
            String  lat = new BigDecimal(Double.toString(latLng.latitude)).setScale(3, RoundingMode.HALF_UP).toString();
            String  lon = new BigDecimal(Double.toString(latLng.longitude)).setScale(3, RoundingMode.HALF_UP).toString();
            _binding.ridesNavigateStartLat.setText(lat);
            _binding.ridesNavigateStartLon.setText(lon);
        }
    }

    /**
     * Handles map click events to set the destination marker and request a new route.
     *
     * @param latLng The LatLng where the map was clicked.
     */
    private void onMapClick(LatLng latLng) {
        clearRoute();
        _mapMarkerDestination.setPosition(latLng);

        // Fill values
        String  lat = new BigDecimal(Double.toString(latLng.latitude)).setScale(3, RoundingMode.HALF_UP).toString();
        String  lon = new BigDecimal(Double.toString(latLng.longitude)).setScale(3, RoundingMode.HALF_UP).toString();
        _binding.ridesNavigateDestinationLat.setText(lat);
        _binding.ridesNavigateDestinationLon.setText(lon);

        // Request route
        getRoute();

        _mapMarkerDestination.setVisible(true);
    }

    /**
     * Clears the currently drawn route from the map and hides the destination marker.
     */
    private void clearRoute() {
        // Clear the previous polyline if it exists
        if (currentPolyline != null) {
            currentPolyline.remove();
        }

        VisorApplication app = (VisorApplication) requireActivity().getApplication();
        app.unSaveNavigation();

        _mapMarkerDestination.setVisible(false);
    }

    /**
     * Cleans up resources when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}