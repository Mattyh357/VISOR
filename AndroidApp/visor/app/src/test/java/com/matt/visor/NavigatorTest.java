package com.matt.visor;

import static com.matt.visor.app.Navigator.MANEUVER_ID;
import static org.junit.Assert.assertEquals;

import com.google.android.gms.maps.model.LatLng;
import com.matt.visor.GoogleMap.Step;
import com.matt.visor.app.Navigator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class NavigatorTest {

    @Test
    public void testAll() {

        Navigator navigator = new Navigator(getHardcodedSteps());
        double delta = 0.1D;

        double test = navigator.getDistanceToNext();
        int icon = navigator.getManeuverID();

        System.out.println("Distance to next: " + test);
        System.out.println("ID: " + icon);

        // Before 1st update - so icon null
        assertEquals("Update 1 - icon: ", null, navigator.getManeuverString());

        // 1st update - still haven't reach the first step, so still null
        navigator.update(new LatLng(0, 0));
        assertEquals("Update 1 - icon: ", null, navigator.getManeuverString());


        // 2nd update - Close to 1st waypoint, so should switch to the "straight'
        navigator.update(new LatLng(1, 1));
        assertEquals("Update 2 - icon: ", "straight", navigator.getManeuverString());

        // 3rd update - far away from the waypoint.. so still "straight"
        navigator.update(new LatLng(1.001, 1.001));
        assertEquals("Update 3 - icon: ", "straight", navigator.getManeuverString());

        // 4th update - super close to the next step, so should switch to "turn-left"
        navigator.update(new LatLng(1.999999, 1.999999));
        assertEquals("Update 4 - icon: ", "turn-left", navigator.getManeuverString());

        // 5th update - at the next step, so should switch to "turn-right"
        navigator.update(new LatLng(3, 3));
        assertEquals("Update 5 - icon: ", "turn-right", navigator.getManeuverString());

        // 6th update - at the last step - so at the destination?
        navigator.update(new LatLng(4, 4));
        assertEquals("Update 5 - icon: ", "destination", navigator.getManeuverString());

    }

    @Test
    public void testCodes() {

        // 0 = straight
        assertEquals(" 0 = straight: ", 0, (int)MANEUVER_ID.get("straight"));

        // 1 = merge
        assertEquals(" 1 = merge: ", 1, (int)MANEUVER_ID.get("merge"));

        // 2 = ferry-train
        assertEquals(" 2 = ferry-train: ", 2, (int)MANEUVER_ID.get("ferry-train"));

        // 3 = ferry
        assertEquals(" 3 = ferry: ", 3, (int)MANEUVER_ID.get("ferry"));

        // 4 = turn-slight-left
        assertEquals(" 4 = turn-slight-left: ", 4, (int)MANEUVER_ID.get("turn-slight-left"));

        // 5 = turn-left
        assertEquals(" 5 = turn-left: ", 5, (int)MANEUVER_ID.get("turn-left"));

        // 6 = turn-sharp-left
        assertEquals(" 6 = turn-sharp-left: ", 6, (int)MANEUVER_ID.get("turn-sharp-left"));

        // 7 = ramp-left
        assertEquals(" 7 = ramp-left: ", 7, (int)MANEUVER_ID.get("ramp-left"));

        // 8 = fork-left
        assertEquals(" 8 = fork-left: ", 8, (int)MANEUVER_ID.get("fork-left"));

        // 9 = uturn-left
        assertEquals(" 9 = uturn-left: ", 9, (int)MANEUVER_ID.get("uturn-left"));

        // 10 = roundabout-left
        assertEquals(" 10 = roundabout-left: ", 10, (int)MANEUVER_ID.get("roundabout-left"));

        // 11 = turn-slight-right
        assertEquals(" 11 = turn-slight-right: ", 11, (int)MANEUVER_ID.get("turn-slight-right"));

        // 12 = turn-right
        assertEquals(" 12 = turn-right: ", 12, (int)MANEUVER_ID.get("turn-right"));

        // 13 = turn-sharp-right
        assertEquals(" 13 = turn-sharp-right: ", 13, (int)MANEUVER_ID.get("turn-sharp-right"));

        // 14 = ramp-right
        assertEquals(" 14 = ramp-right: ", 14, (int)MANEUVER_ID.get("ramp-right"));

        // 15 = fork-right
        assertEquals(" 15 = fork-right: ", 15, (int)MANEUVER_ID.get("fork-right"));

        // 16 = uturn-right
        assertEquals(" 16 = uturn-right: ", 16, (int)MANEUVER_ID.get("uturn-right"));

        // 17 = roundabout-right
        assertEquals(" 17 = roundabout-right: ", 17, (int)MANEUVER_ID.get("roundabout-right"));

        // 18 = destination
        assertEquals(" 18 = destination: ", 18, (int)MANEUVER_ID.get("destination"));

    }



    public static List<Step> getHardcodedSteps() {

        List<Step> list = new ArrayList<>();

        Step step1 = new Step();
        step1.endLocation = new LatLng(1, 1);
        step1.maneuver = null;
        list.add(step1);

        Step step2 = new Step();
        step2.endLocation = new LatLng(2, 2);
        step2.maneuver = "straight";
        list.add(step2);

        Step step3 = new Step();
        step3.endLocation = new LatLng(3, 3);
        step3.maneuver = "turn-left";
        list.add(step3);

        Step step4 = new Step();
        step4.endLocation = new LatLng(4, 4);
        step4.maneuver = "turn-right";
        list.add(step4);

        return list;
    }


}
