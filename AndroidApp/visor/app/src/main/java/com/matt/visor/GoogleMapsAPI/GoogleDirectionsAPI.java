package com.matt.visor.GoogleMapsAPI;

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

    private static final String APIURL= "https://maps.googleapis.com/maps/api/directions/json?";
    private String _APIkey;
    private String _travelMode; // TODO travelmode


    public GoogleDirectionsAPI(String APIkey) {
        _APIkey = APIkey;
    }


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

                    Log.i("Directions", response.toString());

                    // TODO PARSE
                    List<Waypoint> list = extractWaypointsFromDirections(response.toString());
                    List<LatLng> polylineRoute = decodePolyline(extractPolyline(response.toString()));


                    callback.onSuccess(list, polylineRoute);

                } else {
                    //TODO better fail thing
                    Log.e("Directions", "Error: Could not get directions");
                    callback.onFail("Error: Could not get directions");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError(e);
            }
        }).start();
    }


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











    private List<Waypoint> extractWaypointsFromDirections(String jsonResponse) throws JSONException {
        List<Waypoint> waypoints = new ArrayList<>();



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

                    JSONObject step = steps.getJSONObject(j);
                    Waypoint waypoint = new Waypoint();

                    waypoint.instruction = android.text.Html.fromHtml(step.getString("html_instructions")).toString(); // Convert from HTML
                    waypoint.distanceText = step.getJSONObject("distance").getString("text");
                    waypoint.distanceValue = step.getJSONObject("distance").getInt("value");
                    waypoint.durationText = step.getJSONObject("duration").getString("text");
                    waypoint.durationValue = step.getJSONObject("duration").getInt("value");

                    JSONObject startLocation = step.getJSONObject("start_location");
                    waypoint.startLocation = new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng"));

                    JSONObject endLocation = step.getJSONObject("end_location");
                    waypoint.endLocation = new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng"));

                    if(step.has("maneuver")) {
                        waypoint.maneuver = step.getString("maneuver");
                    }

                    waypoints.add(waypoint);
                }
            }
        }


        return waypoints;
    }



    private URL getUrl(LatLng origin, LatLng destination) throws MalformedURLException {
        //TODO if nulls

        StringBuilder urlStringBuilder = new StringBuilder(APIURL);
        urlStringBuilder.append("origin=");
        urlStringBuilder.append(origin.latitude).append(",").append(origin.longitude);
        urlStringBuilder.append("&destination=");
        urlStringBuilder.append(destination.latitude).append(",").append(destination.longitude);
        urlStringBuilder.append("&key=").append(_APIkey);

        return new URL(urlStringBuilder.toString());
    }






}
