/**
 * This class is part of the V.I.S.O.R app.
 * GoogleDirectionsAPI is responsible for requesting and processing Google direction API.
 * Requires API key.
 *
 * @version 1.0
 * @since 20/02/2024
 */

package com.matt.visor.GoogleMap;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GoogleDirectionsAPI {

    private static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private final String _APIkey;
    private String _travelMode; // TODO travelmode


    /**
     * Initializes the GoogleDirectionsAPI with the provided API key.
     *
     * @param APIkey The Google API key used for accessing the Directions API.
     */
    public GoogleDirectionsAPI(String APIkey) {
        _APIkey = APIkey;
    }

    /**
     * Requests a route between the specified origin and destination.
     *
     * @param origin The starting point of the route.
     * @param destination The end point of the route.
     * @param callback The callback to handle the response.
     */
    public void requestRoute(LatLng origin, LatLng destination, DirectionsCallback callback) {
        new Thread(() -> {
            try {
                URL url = getUrl(origin, destination);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    bufferedReader.close();

                    // Parse
                    List<Step> list = parseSteps(response.toString());
                    List<LatLng> polylineRoute = decodePolyline(extractPolyline(response.toString()));


                    callback.onSuccess(list, polylineRoute);

                } else {
                    Log.e("Directions", "Error: Could not get directions");
                    callback.onFail("Error: Could not get directions");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError(e);
            }
        }).start();
    }

    /**
     * Extracts the polyline string from the JSON response.
     *
     * @param jsonResponse The JSON response from the Directions API.
     * @return The encoded polyline string.
     * @throws JSONException If there is an error parsing the JSON.
     */
    private String extractPolyline(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray routes = jsonObject.getJSONArray("routes");

        if (routes.length() > 0) {
            JSONObject route = routes.getJSONObject(0);
            JSONObject poly = route.getJSONObject("overview_polyline");
            return poly.getString("points");
        }

        return null;
    }

    /**
     * Decodes an encoded polyline into a list of LatLng points.
     *
     * @param encoded The encoded polyline string.
     * @return A list of LatLng points representing the polyline.
     * TODO ADD REFERENCE TO THE ORIGINAL!!!!!!!
     */
    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    /**
     * Parses the steps from the JSON response into a list of Step objects.
     *
     * @param jsonResponse The JSON response from the Directions API.
     * @return A list of Step objects representing the route steps.
     * @throws JSONException If there is an error parsing the JSON.
     */
    private List<Step> parseSteps(String jsonResponse) throws JSONException {
        List<Step> stepList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray routes = jsonObject.getJSONArray("routes");

        if (routes.length() > 0) {
            JSONObject route = routes.getJSONObject(0);
            JSONArray legs = route.getJSONArray("legs");

            for (int i = 0; i < legs.length(); i++) {
                JSONObject leg = legs.getJSONObject(i);
                JSONArray steps = leg.getJSONArray("steps");

                for (int j = 0; j < steps.length(); j++) {

                    // TODO extract

                    JSONObject stepsJSONObject = steps.getJSONObject(j);
                    Step step = new Step();

                    JSONObject endLocation = stepsJSONObject.getJSONObject("end_location");
                    step.endLocation = new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng"));

                    if(stepsJSONObject.has("maneuver")) {
                        step.maneuver = stepsJSONObject.getString("maneuver");
                    }

                    stepList.add(step);
                }
            }
        }

        return stepList;
    }


    /**
     * Constructs the URL for the Directions API request.
     *
     * @param origin The starting point of the route.
     * @param destination The end point of the route.
     * @return A URL object for the API request.
     * @throws MalformedURLException If the constructed URL is not valid.
     */
    private URL getUrl(LatLng origin, LatLng destination) throws MalformedURLException {
        //TODO if nulls

        StringBuilder urlStringBuilder = new StringBuilder(API_URL);
        urlStringBuilder.append("origin=");
        urlStringBuilder.append(origin.latitude).append(",").append(origin.longitude);
        urlStringBuilder.append("&destination=");
        urlStringBuilder.append(destination.latitude).append(",").append(destination.longitude);
        urlStringBuilder.append("&key=").append(_APIkey);

        return new URL(urlStringBuilder.toString());
    }






}
