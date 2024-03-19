/**
 * This class is part of the V.I.S.O.R app.
 * Manages navigation for a given route (list of steps retrieved from Google Direction API).
 * Updates the current step based on provided location, and return distance to the next step as well
 * as the string predetermined ID of the maneuver.
 * The list of directions for used in Google API maneuver taken from:
 * LINK: https://github.com/google/material-design-icons/issues/341
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.app;

import com.google.android.gms.maps.model.LatLng;
import com.matt.visor.GoogleMap.Step;
import com.matt.visor.app.recorder.HaversineCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Navigator {

    private static final Map<String, Integer> MANEUVER_ID = new HashMap<>();
    private static final String DESTINATION_STR = "destination";

    static {
        MANEUVER_ID.put("straight", 0);
        MANEUVER_ID.put("merge", 1);
        MANEUVER_ID.put("ferry-train", 2);
        MANEUVER_ID.put("ferry", 3);
        MANEUVER_ID.put("turn-slight-left", 4);
        MANEUVER_ID.put("turn-left", 5);
        MANEUVER_ID.put("turn-sharp-left", 6);
        MANEUVER_ID.put("ramp-left", 7);
        MANEUVER_ID.put("fork-left", 8);
        MANEUVER_ID.put("uturn-left", 9);
        MANEUVER_ID.put("roundabout-left", 10);
        MANEUVER_ID.put("turn-slight-right", 11);
        MANEUVER_ID.put("turn-right", 12);
        MANEUVER_ID.put("turn-sharp-right", 13);
        MANEUVER_ID.put("ramp-right", 14);
        MANEUVER_ID.put("fork-right", 15);
        MANEUVER_ID.put("uturn-right", 16);
        MANEUVER_ID.put("roundabout-right", 17);
        MANEUVER_ID.put(DESTINATION_STR, 18);
    }




    // TODO test what number is adequate
    private static final double THRESHOLD = 50; // Seems to work just find
    private final List<Step> _steps;
    private int _currentStepIndex = 0;
    private double _distanceToNext = 0;

    /**
     * Initializes navigation with a predefined set of steps.
     *
     * @param steps The list of navigation steps to follow.
     */
    public Navigator(List<Step> steps) {
        // TODO hardcoded step
        steps = getHardcodedSteps();
        _steps = getHardcodedSteps();
    }

    /**
     * Updates the navigator with the current location.
     * This method calculates the distance to the next step. If the distance is less then the THRESHOLD,
     * it means that the step was reached and it moved on to the next one.
     *
     * @param location The current geographical location.
     * @return True if navigation should continue, false if the destination is reached or an error occurs.
     */
    public boolean update(LatLng location) {
        // Destination reached
        if (_currentStepIndex >= _steps.size())
            return false;

        //Calculate distance to next stop
        _distanceToNext = HaversineCalculator.haversineDistance(
                location.latitude
                , location.longitude
                , _steps.get(_currentStepIndex).endLocation.latitude
                , _steps.get(_currentStepIndex).endLocation.longitude
        );


        System.out.println("Step number: " + _currentStepIndex + " - delta distance: " + _distanceToNext);

        if(_distanceToNext > THRESHOLD)
            return false;

        // Got close to the waypoint -> change it
        _currentStepIndex++;

        //RE-Calculate as it changed
        if (_currentStepIndex >= _steps.size()) {
            _distanceToNext = 0;
        }
        else {
            _distanceToNext = HaversineCalculator.haversineDistance(
                    location.latitude
                    , location.longitude
                    , _steps.get(_currentStepIndex).endLocation.latitude
                    , _steps.get(_currentStepIndex).endLocation.longitude
            );
        }

        return true;

    }


    /**
     * Retrieves the maneuver string for the current step.
     *
     * @return The maneuver as a string, or "destination" if the end is reached.
     */
    public String getManeuverString() {

        if (_currentStepIndex < _steps.size()) {
            return _steps.get(_currentStepIndex).maneuver;
        }

        // Last step reached
        return DESTINATION_STR;
    }

    /**
     * Gets the maneuver ID corresponding to the current maneuver string.
     *
     * @return The maneuver ID, or -1 if not found.
     */
    public int getManeuverID() {
        String maneuver = getManeuverString();

        return MANEUVER_ID.getOrDefault(maneuver, -1);
    }

    /**
     * Gets the distance to the next step in Kilometers.
     *
     * @return The distance to the next step, in kilometers.
     */
    public double getDistanceToNext() {
        return _distanceToNext / 1000;
    }


    // TODO remove
    public static List<Step> getHardcodedSteps() {

        List<Step> list = new ArrayList<>();

        Step step1 = new Step();
        step1.endLocation = new LatLng(56.47035529999999, -3.0120029);
        step1.maneuver = null;
        list.add(step1);

        Step step2 = new Step();
        step2.endLocation = new LatLng(56.4706489, -3.0108241);
        step2.maneuver = "turn-left";
        list.add(step2);

        Step step3 = new Step();
        step3.endLocation = new LatLng(56.4716987, -3.0118959);
        step3.maneuver = "turn-left";
        list.add(step3);

        Step step4 = new Step();
        step4.endLocation = new LatLng(56.4638752, -2.9802262);
        step4.maneuver = "roundabout-left";
        list.add(step4);

        Step step5 = new Step();
        step5.endLocation = new LatLng(56.46443, -2.9733292);
        step5.maneuver = "roundabout-left";
        list.add(step5);

        Step step6 = new Step();
        step6.endLocation = new LatLng(56.46416680000001, -2.9719381);
        step6.maneuver = "roundabout-left";
        list.add(step6);

        Step step7 = new Step();
        step7.endLocation = new LatLng(56.4637045, -2.9714995);
        step7.maneuver = "turn-right";
        list.add(step7);

        Step step8 = new Step();
        step8.endLocation = new LatLng(56.4632277, -2.973336);
        step8.maneuver = "turn-right";
        list.add(step8);


        return list;
    }
}
