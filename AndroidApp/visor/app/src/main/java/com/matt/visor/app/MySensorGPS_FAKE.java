/**
 * This class is part of the V.I.S.O.R app.
 * Represents a GPS sensor, handling location updates and status.
 * Extends MySensorGPS to provide the same functionality as regular GPS sensor would.
 *
 * This class is a FAKE gps sensor that is only to be used for demo function.
 * It contains hardcoded list of waypoints and steps that this class will iterate over and present
 * them in the exactly same way regular sensor would.
 *
 * @version 1.0
 * @since 08/02/2024
 */

package com.matt.visor.app;

import android.app.Activity;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.matt.visor.GoogleMap.Step;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class MySensorGPS_FAKE extends MySensorGPS {

    private LinkedList<Location> _listOfLocations;

    private Location _lastLocation;

    /**
     * Initializes the GPS sensor with default values.
     */
    public MySensorGPS_FAKE() {
        _listOfLocations = new LinkedList<>();
        pushAllWaypoints();

        Handler mainHandler = new Handler(Looper.getMainLooper());

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                mainHandler.post(() -> {
                    _lastLocation = getNewLocation();

                    if(_sensorValueListener != null)
                        _sensorValueListener.onSensorValueChange();
                });
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();


    }

    /**
     * Generates random speed and polls the hardcoded list of location to get next stop.
     *
     * @return The last known location.
     */
    private Location getNewLocation() {
        Random random = new Random();
        float randomSpeed = 10 + (25 - 10) * random.nextFloat();

        Location newLocation = _listOfLocations.pollFirst();

        if(newLocation == null)
            return _lastLocation;

        newLocation.setTime(System.currentTimeMillis());
        newLocation.setSpeed(randomSpeed);

        return newLocation;
    }


    /**
     * Returns the most recent location received.
     *
     * @return The last known location.
     */
    public Location getLocation() {
        return _lastLocation;
    }


    /**
     * Activates the GPS sensor and starts receiving location updates.
     *
     * @param activity The activity context used to access the FusedLocationProviderClient.
     */
    @Override
    public void activateSensor(@NonNull Activity activity) {
    }


    /**
     * Hardcoded list of waypoints for predetermined journey used in demo
     */
    private void pushAllWaypoints() {

        pushWaypoint(56.4703956, -3.012);
        pushWaypoint(56.4703454, -3.012);
        pushWaypoint(56.4702979, -3.0118397);
        pushWaypoint(56.4702682, -3.011429);
        pushWaypoint(56.4702935, -3.011084);
        pushWaypoint(56.4704703, -3.0107741);
        pushWaypoint(56.4704703, -3.0107741);
        pushWaypoint(56.4706853, -3.0107581);
        pushWaypoint(56.4708774, -3.0109985);
        pushWaypoint(56.4710816, -3.0112834);
        pushWaypoint(56.4712857, -3.0115153);
        pushWaypoint(56.4716343, -3.0118039);
        pushWaypoint(56.4719806, -3.0121507);
        pushWaypoint(56.4721509, -3.011574);
        pushWaypoint(56.4718083, -3.0112096);
        pushWaypoint(56.4718083, -3.0112096);
        pushWaypoint(56.4714662, -3.011061);
        pushWaypoint(56.4711288, -3.0109733);
        pushWaypoint(56.4708448, -3.0107764);
        pushWaypoint(56.470603, -3.0104447);
        pushWaypoint(56.4704545, -3.0100218);
        pushWaypoint(56.4703788, -3.0095698);
        pushWaypoint(56.4703437, -3.0088174);
        pushWaypoint(56.4703514, -3.0081745);
        pushWaypoint(56.4703338, -3.0074878);
        pushWaypoint(56.4702207, -3.0068047);
        pushWaypoint(56.4700977, -3.0061803);
        pushWaypoint(56.4699303, -3.0054417);
        pushWaypoint(56.4699303, -3.0054417);
        pushWaypoint(56.4698013, -3.0049311);
        pushWaypoint(56.4696278, -3.0043576);
        pushWaypoint(56.4693724, -3.003563);
        pushWaypoint(56.4693724, -3.003563);
        pushWaypoint(56.4689105, -3.0029324);
        pushWaypoint(56.4685873, -3.0027398);
        pushWaypoint(56.4683904, -3.0026078);
        pushWaypoint(56.468115, -3.0019166);
        pushWaypoint(56.4678838, -3.0011453);
        pushWaypoint(56.4677782, -3.0005124);
        pushWaypoint(56.4677782, -3.0005124);
        pushWaypoint(56.4677428, -2.9997655);
        pushWaypoint(56.4677585, -2.99918);
        pushWaypoint(56.4677609, -2.9983115);
        pushWaypoint(56.4675984, -2.9978187);
        pushWaypoint(56.4670946, -2.9975368);
        pushWaypoint(56.4667329, -2.9975747);
        pushWaypoint(56.4663948, -2.9976167);
        pushWaypoint(56.4659907, -2.9973305);
        pushWaypoint(56.4659907, -2.9973305);
        pushWaypoint(56.4658958, -2.9968034);
        pushWaypoint(56.4659081, -2.9961642);
        pushWaypoint(56.4658073, -2.9953448);
        pushWaypoint(56.4656256, -2.9946216);
        pushWaypoint(56.4656256, -2.9946216);
        pushWaypoint(56.4653115, -2.9939439);
        pushWaypoint(56.4651011, -2.9935639);
        pushWaypoint(56.4648779, -2.9932425);
        pushWaypoint(56.4645679, -2.9928584);
        pushWaypoint(56.4643633, -2.9925811);
        pushWaypoint(56.4640677, -2.9919511);
        pushWaypoint(56.463824, -2.9913344);
        pushWaypoint(56.4636093, -2.9906498);
        pushWaypoint(56.4634367, -2.9898198);
        pushWaypoint(56.4634367, -2.9898198);
        pushWaypoint(56.46339, -2.9892524);
        pushWaypoint(56.46341, -2.9884558);
        pushWaypoint(56.463543, -2.9875711);
        pushWaypoint(56.463543, -2.9875711);
        pushWaypoint(56.4636781, -2.9867312);
        pushWaypoint(56.4637729, -2.9858336);
        pushWaypoint(56.4637729, -2.9858336);
        pushWaypoint(56.4638231, -2.9851294);
        pushWaypoint(56.4638431, -2.9845128);
        pushWaypoint(56.4638271, -2.9835523);
        pushWaypoint(56.4638094, -2.9828231);
        pushWaypoint(56.463792, -2.9820907);
        pushWaypoint(56.4637771, -2.9812617);
        pushWaypoint(56.4637771, -2.9812617);
        pushWaypoint(56.4637615, -2.9803523);
        pushWaypoint(56.4638983, -2.9797599);
        pushWaypoint(56.4639366, -2.979072);
        pushWaypoint(56.4638762, -2.9781476);
        pushWaypoint(56.4637465, -2.9771555);
        pushWaypoint(56.4636629, -2.976374);
        pushWaypoint(56.4637425, -2.9753797);
        pushWaypoint(56.4637425, -2.9753797);
        pushWaypoint(56.4638989, -2.9746344);
        pushWaypoint(56.4641742, -2.9738764);
        pushWaypoint(56.4644511, -2.9731854);
        pushWaypoint(56.4645311, -2.972619);
        pushWaypoint(56.4643855, -2.9723178);
        pushWaypoint(56.4642522, -2.9719062);
        pushWaypoint(56.4639772, -2.971507);
        pushWaypoint(56.4639772, -2.971507);
        pushWaypoint(56.4637735, -2.9713665);
        pushWaypoint(56.4636532, -2.9715107);
        pushWaypoint(56.4635419, -2.971846);
        pushWaypoint(56.4635419, -2.971846);
        pushWaypoint(56.4634808, -2.9721849);
        pushWaypoint(56.4634191, -2.9725001);
        pushWaypoint(56.4633596, -2.9727775);
        pushWaypoint(56.4633011, -2.9730354);
        pushWaypoint(56.463225, -2.9733479);
        pushWaypoint(56.4631662, -2.9735648);
        pushWaypoint(56.4631662, -2.9735648);
        pushWaypoint(56.4630961, -2.9738297);
        pushWaypoint(56.4631247, -2.9740735);
        pushWaypoint(56.4631247, -2.9740735);
        pushWaypoint(56.4632137, -2.9742511);
        pushWaypoint(56.4632402, -2.9742473);
        pushWaypoint(56.4632402, -2.9742473);
        pushWaypoint(56.463261, -2.9742367);
        pushWaypoint(56.463261, -2.9742367);
        pushWaypoint(56.4632716, -2.9742087);
        pushWaypoint(56.463268, -2.9741955);
        pushWaypoint(56.4632684, -2.9741894);
        pushWaypoint(56.4633009, -2.9742049);
        pushWaypoint(56.4632702, -2.9741843);
        pushWaypoint(56.4632702, -2.9741843);
        pushWaypoint(56.4632652, -2.9741794);
        pushWaypoint(56.4632628, -2.9741774);
        pushWaypoint(56.4632644, -2.9741797);
        pushWaypoint(56.4632644, -2.9741797);
        pushWaypoint(56.4632711, -2.9741815);
        pushWaypoint(56.4632663, -2.9741797);
        pushWaypoint(56.4632663, -2.9741797);
        pushWaypoint(56.4632679, -2.9741798);
        pushWaypoint(56.4632679, -2.9741798);
        pushWaypoint(56.463264, -2.9741777);
        pushWaypoint(56.4632609, -2.974177);
        pushWaypoint(56.4632609, -2.974177);
        pushWaypoint(56.4632647, -2.9741782);
        pushWaypoint(56.4632615, -2.9741773);
        pushWaypoint(56.4632615, -2.9741773);
        pushWaypoint(56.4632649, -2.9741783);
        pushWaypoint(56.4632649, -2.9741783);
        pushWaypoint(56.4632633, -2.9741774);
        pushWaypoint(56.4632633, -2.9741774);
        pushWaypoint(56.4632613, -2.9741765);
        pushWaypoint(56.4632596, -2.9741764);
        pushWaypoint(56.4632596, -2.9741764);
        pushWaypoint(56.4632641, -2.974178);
        pushWaypoint(56.4632641, -2.974178);
        pushWaypoint(56.4632623, -2.974177);
        pushWaypoint(56.4632601, -2.9741766);
        pushWaypoint(56.4632601, -2.9741766);
        pushWaypoint(56.4632645, -2.9741781);
        pushWaypoint(56.4632615, -2.9741772);
        pushWaypoint(56.4632629, -2.9741782);
        pushWaypoint(56.463265, -2.9741793);
        pushWaypoint(56.463265, -2.9741793);
        pushWaypoint(56.4632681, -2.9741799);
        pushWaypoint(56.4632635, -2.9741782);
        pushWaypoint(56.4632641, -2.9741788);
        pushWaypoint(56.4632655, -2.9741796);
        pushWaypoint(56.4632655, -2.9741796);
        pushWaypoint(56.4632684, -2.97418);
        pushWaypoint(56.4632636, -2.9741783);
        pushWaypoint(56.4632641, -2.9741788);
        pushWaypoint(56.4632641, -2.9741788);
        pushWaypoint(56.4632677, -2.9741797);
        pushWaypoint(56.4632677, -2.9741797);
        pushWaypoint(56.463271, -2.9741817);
        pushWaypoint(56.4632671, -2.9741803);
        pushWaypoint(56.4632671, -2.9741803);
        pushWaypoint(56.4632694, -2.9741806);
        pushWaypoint(56.4632694, -2.9741806);
        pushWaypoint(56.4632717, -2.9741821);
        pushWaypoint(56.4632717, -2.9741821);
        pushWaypoint(56.4632689, -2.9741802);
        pushWaypoint(56.4632689, -2.9741802);
        pushWaypoint(56.4632648, -2.9741781);
        pushWaypoint(56.4632648, -2.9741781);
        pushWaypoint(56.4632618, -2.9741767);
        pushWaypoint(56.4632618, -2.9741767);
        pushWaypoint(56.4632611, -2.9741764);
        pushWaypoint(56.4632638, -2.9741795);
        pushWaypoint(56.4632638, -2.9741795);
        pushWaypoint(56.4632709, -2.9741816);
        pushWaypoint(56.4632662, -2.9741798);
        pushWaypoint(56.4632662, -2.9741798);
        pushWaypoint(56.463268, -2.9741799);
        pushWaypoint(56.463268, -2.9741799);
        pushWaypoint(56.463264, -2.9741778);
        pushWaypoint(56.463264, -2.9741778);
        pushWaypoint(56.4632622, -2.974177);
        pushWaypoint(56.463264, -2.9741796);
        pushWaypoint(56.463264, -2.9741796);
        pushWaypoint(56.4632711, -2.9741817);
        pushWaypoint(56.4632711, -2.9741817);
        pushWaypoint(56.4632676, -2.9741796);
        pushWaypoint(56.4632663, -2.9741808);
        pushWaypoint(56.4632696, -2.9741823);
        pushWaypoint(56.4632696, -2.9741823);
        pushWaypoint(56.4632726, -2.9741824);
        pushWaypoint(56.4632668, -2.9741801);
        pushWaypoint(56.4632668, -2.9741801);
        pushWaypoint(56.4632739, -2.9741834);
        pushWaypoint(56.4632739, -2.9741834);
        pushWaypoint(56.4632705, -2.974181);
        pushWaypoint(56.4632705, -2.974181);
        pushWaypoint(56.4632656, -2.9741785);
        pushWaypoint(56.4632615, -2.9741772);
        pushWaypoint(56.4632615, -2.9741772);
        pushWaypoint(56.4632645, -2.9741781);
        pushWaypoint(56.4632645, -2.9741781);
        pushWaypoint(56.463263, -2.9741772);
        pushWaypoint(56.4632602, -2.9741766);
        pushWaypoint(56.4632602, -2.9741766);
        pushWaypoint(56.4632641, -2.9741779);
        pushWaypoint(56.4632641, -2.9741779);
        pushWaypoint(56.463263, -2.9741772);
        pushWaypoint(56.4632602, -2.9741766);
        pushWaypoint(56.4632602, -2.9741766);
        pushWaypoint(56.4632641, -2.9741779);
        pushWaypoint(56.4632617, -2.9741774);
        pushWaypoint(56.4632669, -2.9741813);
        pushWaypoint(56.4632705, -2.9741828);
        pushWaypoint(56.4632711, -2.9741829);
        pushWaypoint(56.4632707, -2.9741826);
        pushWaypoint(56.4632703, -2.9741823);
        pushWaypoint(56.4632714, -2.9741837);
        pushWaypoint(56.4632734, -2.9741843);
        pushWaypoint(56.4632734, -2.9741843);
        pushWaypoint(56.4632749, -2.9741837);
        pushWaypoint(56.4632749, -2.9741837);
        pushWaypoint(56.4632684, -2.97418);
        pushWaypoint(56.4632684, -2.97418);
        pushWaypoint(56.4632634, -2.9741775);
        pushWaypoint(56.4632634, -2.9741775);
        pushWaypoint(56.4632617, -2.9741767);
        pushWaypoint(56.4632636, -2.9741794);
        pushWaypoint(56.4632636, -2.9741794);
        pushWaypoint(56.463271, -2.9741817);
        pushWaypoint(56.463271, -2.9741817);
        pushWaypoint(56.4632676, -2.9741796);
        pushWaypoint(56.4632661, -2.9741807);
        pushWaypoint(56.4632696, -2.9741823);
        pushWaypoint(56.4632704, -2.9741825);
        pushWaypoint(56.4632704, -2.9741825);
        pushWaypoint(56.4632724, -2.9741822);
        pushWaypoint(56.4632684, -2.9741819);
        pushWaypoint(56.4632684, -2.9741819);
        pushWaypoint(56.4632738, -2.9741831);
        pushWaypoint(56.4632738, -2.9741831);
        pushWaypoint(56.4632689, -2.9741802);
        pushWaypoint(56.4632665, -2.9741809);
        pushWaypoint(56.4632698, -2.9741824);
        pushWaypoint(56.4632705, -2.9741826);
        pushWaypoint(56.4632705, -2.9741826);
        pushWaypoint(56.4632724, -2.9741822);
        pushWaypoint(56.4632684, -2.9741819);
        pushWaypoint(56.463271, -2.9741831);
        pushWaypoint(56.4632713, -2.974183);
        pushWaypoint(56.4632708, -2.9741826);
        pushWaypoint(56.4632708, -2.9741826);
        pushWaypoint(56.4632716, -2.9741818);
        pushWaypoint(56.4632652, -2.9741792);
        pushWaypoint(56.4632652, -2.9741792);
        pushWaypoint(56.4632666, -2.9741792);
        pushWaypoint(56.4632666, -2.9741792);
        pushWaypoint(56.463264, -2.9741777);
        pushWaypoint(56.4632606, -2.9741768);
        pushWaypoint(56.4632623, -2.9741779);
        pushWaypoint(56.4632645, -2.9741791);
        pushWaypoint(56.4632645, -2.9741791);
        pushWaypoint(56.4632679, -2.9741798);
        pushWaypoint(56.4632634, -2.9741781);
        pushWaypoint(56.463264, -2.9741787);
        pushWaypoint(56.4632655, -2.9741796);
        pushWaypoint(56.4632655, -2.9741796);
        pushWaypoint(56.4632684, -2.97418);
        pushWaypoint(56.4632636, -2.9741783);
        pushWaypoint(56.4632641, -2.9741788);
        pushWaypoint(56.4632655, -2.9741796);
        pushWaypoint(56.4632655, -2.9741796);
        pushWaypoint(56.4632684, -2.97418);
        pushWaypoint(56.4632684, -2.97418);
        pushWaypoint(56.4632643, -2.9741779);
        pushWaypoint(56.4632611, -2.9741771);
        pushWaypoint(56.4632611, -2.9741771);
        pushWaypoint(56.4632649, -2.9741783);
        pushWaypoint(56.4632616, -2.9741773);
        pushWaypoint(56.4632616, -2.9741773);
        pushWaypoint(56.4632649, -2.9741783);
        pushWaypoint(56.4632621, -2.9741776);
        pushWaypoint(56.4632668, -2.9741813);
        pushWaypoint(56.4632668, -2.9741813);
        pushWaypoint(56.4632737, -2.9741831);
        pushWaypoint(56.4632737, -2.9741831);
        pushWaypoint(56.463269, -2.9741803);
        pushWaypoint(56.463269, -2.9741803);
        pushWaypoint(56.463271, -2.9741818);
        pushWaypoint(56.463271, -2.9741818);
        pushWaypoint(56.4632687, -2.9741801);
        pushWaypoint(56.4632641, -2.9741786);
        pushWaypoint(56.4632676, -2.9741817);
        pushWaypoint(56.4632676, -2.9741817);
        pushWaypoint(56.4632741, -2.9741833);
        pushWaypoint(56.4632741, -2.9741833);
        pushWaypoint(56.4632692, -2.9741804);
        pushWaypoint(56.4632692, -2.9741804);
        pushWaypoint(56.463271, -2.9741818);
        pushWaypoint(56.463271, -2.9741818);
        pushWaypoint(56.4632687, -2.9741801);
        pushWaypoint(56.4632687, -2.9741801);
        pushWaypoint(56.4632648, -2.9741781);
        pushWaypoint(56.4632648, -2.9741781);
        pushWaypoint(56.4632617, -2.9741767);
        pushWaypoint(56.4632617, -2.9741767);
        pushWaypoint(56.4632611, -2.9741764);
        pushWaypoint(56.4632611, -2.9741764);
        pushWaypoint(56.4632681, -2.9741804);
        pushWaypoint(56.4632681, -2.9741804);
        pushWaypoint(56.4632674, -2.9741795);
        pushWaypoint(56.4632674, -2.9741795);
        pushWaypoint(56.4632644, -2.9741779);
        pushWaypoint(56.4632609, -2.9741769);
        pushWaypoint(56.4632609, -2.9741769);

    }

    /**
     * Pushes waypoint made out of latitude and longitude to the list of locations
     *
     * @param latitude Latitude of the waypoint
     * @param longitude Longitude of the waypoint
     */
    private void pushWaypoint(double latitude, double longitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        _listOfLocations.addLast(location);
    }


    /**
     * List of Step for predetermined journey used in demo
     *
     * @return  List of steps
     */
    public static List<Step> getFakeSteps() {

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

    /**
     * Hardcoded list of polyline points for predetermined journey used in demo
     *
     * @return List of polyline points
     */
    public static List<LatLng> getFacePoly() {

        List<LatLng> list = new ArrayList<>();

        list.add(new LatLng(56.47057,-3.012));
        list.add(new LatLng(56.47036,-3.012));
        list.add(new LatLng(56.47034,-3.01137));
        list.add(new LatLng(56.47035,-3.01127));
        list.add(new LatLng(56.47041,-3.01099));
        list.add(new LatLng(56.47045,-3.01088));
        list.add(new LatLng(56.47052,-3.01083));
        list.add(new LatLng(56.47057,-3.01081));
        list.add(new LatLng(56.47065,-3.01082));
        list.add(new LatLng(56.47093,-3.0113));
        list.add(new LatLng(56.47108,-3.01148));
        list.add(new LatLng(56.47116,-3.01156));
        list.add(new LatLng(56.47152,-3.01176));
        list.add(new LatLng(56.4717,-3.0119));
        list.add(new LatLng(56.47172,-3.012));
        list.add(new LatLng(56.4718,-3.01215));
        list.add(new LatLng(56.47193,-3.0122));
        list.add(new LatLng(56.47199,-3.01218));
        list.add(new LatLng(56.47205,-3.01208));
        list.add(new LatLng(56.47209,-3.01183));
        list.add(new LatLng(56.47205,-3.0116));
        list.add(new LatLng(56.47201,-3.01154));
        list.add(new LatLng(56.47194,-3.01149));
        list.add(new LatLng(56.47187,-3.01148));
        list.add(new LatLng(56.47181,-3.0115));
        list.add(new LatLng(56.47176,-3.01156));
        list.add(new LatLng(56.47159,-3.01158));
        list.add(new LatLng(56.4715,-3.01155));
        list.add(new LatLng(56.47141,-3.0115));
        list.add(new LatLng(56.47133,-3.01144));
        list.add(new LatLng(56.47115,-3.01127));
        list.add(new LatLng(56.47094,-3.01102));
        list.add(new LatLng(56.47082,-3.01084));
        list.add(new LatLng(56.47074,-3.01067));
        list.add(new LatLng(56.47066,-3.01048));
        list.add(new LatLng(56.47058,-3.01017));
        list.add(new LatLng(56.47052,-3.00979));
        list.add(new LatLng(56.47046,-3.00911));
        list.add(new LatLng(56.47044,-3.00783));
        list.add(new LatLng(56.47039,-3.00749));
        list.add(new LatLng(56.47028,-3.00703));
        list.add(new LatLng(56.47021,-3.00687));
        list.add(new LatLng(56.47014,-3.00652));
        list.add(new LatLng(56.46999,-3.00582));
        list.add(new LatLng(56.46981,-3.00509));
        list.add(new LatLng(56.46942,-3.00373));
        list.add(new LatLng(56.46932,-3.00349));
        list.add(new LatLng(56.46921,-3.00333));
        list.add(new LatLng(56.46912,-3.00326));
        list.add(new LatLng(56.46887,-3.00313));
        list.add(new LatLng(56.46869,-3.00304));
        list.add(new LatLng(56.4686,-3.00297));
        list.add(new LatLng(56.46841,-3.00275));
        list.add(new LatLng(56.46826,-3.00232));
        list.add(new LatLng(56.46804,-3.0015));
        list.add(new LatLng(56.46793,-3.00105));
        list.add(new LatLng(56.46787,-3.00079));
        list.add(new LatLng(56.46784,-3.00047));
        list.add(new LatLng(56.46783,-2.99991));
        list.add(new LatLng(56.46783,-2.99854));
        list.add(new LatLng(56.4678,-2.99827));
        list.add(new LatLng(56.46776,-2.99818));
        list.add(new LatLng(56.46767,-2.99804));
        list.add(new LatLng(56.46755,-2.99798));
        list.add(new LatLng(56.46736,-2.99794));
        list.add(new LatLng(56.46708,-2.99793));
        list.add(new LatLng(56.46658,-2.99796));
        list.add(new LatLng(56.46644,-2.99791));
        list.add(new LatLng(56.46635,-2.9978));
        list.add(new LatLng(56.46616,-2.99736));
        list.add(new LatLng(56.46608,-2.99699));
        list.add(new LatLng(56.46605,-2.99587));
        list.add(new LatLng(56.46601,-2.99563));
        list.add(new LatLng(56.46587,-2.99528));
        list.add(new LatLng(56.46575,-2.99503));
        list.add(new LatLng(56.46551,-2.9945));
        list.add(new LatLng(56.46508,-2.99365));
        list.add(new LatLng(56.46469,-2.99315));
        list.add(new LatLng(56.46452,-2.99291));
        list.add(new LatLng(56.46437,-2.99266));
        list.add(new LatLng(56.46405,-2.99189));
        list.add(new LatLng(56.46402,-2.99179));
        list.add(new LatLng(56.46375,-2.99103));
        list.add(new LatLng(56.46367,-2.99073));
        list.add(new LatLng(56.46359,-2.99028));
        list.add(new LatLng(56.46352,-2.98988));
        list.add(new LatLng(56.46345,-2.98903));
        list.add(new LatLng(56.46346,-2.98865));
        list.add(new LatLng(56.46351,-2.98825));
        list.add(new LatLng(56.46358,-2.98788));
        list.add(new LatLng(56.46367,-2.98732));
        list.add(new LatLng(56.46376,-2.98636));
        list.add(new LatLng(56.4638,-2.98578));
        list.add(new LatLng(56.46383,-2.98478));
        list.add(new LatLng(56.46382,-2.98402));
        list.add(new LatLng(56.46377,-2.98148));
        list.add(new LatLng(56.46376,-2.98077));
        list.add(new LatLng(56.46388,-2.98023));
        list.add(new LatLng(56.46392,-2.98016));
        list.add(new LatLng(56.46395,-2.98008));
        list.add(new LatLng(56.46398,-2.97989));
        list.add(new LatLng(56.46396,-2.97936));
        list.add(new LatLng(56.46393,-2.97889));
        list.add(new LatLng(56.46386,-2.97843));
        list.add(new LatLng(56.46365,-2.97702));
        list.add(new LatLng(56.46363,-2.97657));
        list.add(new LatLng(56.46366,-2.97618));
        list.add(new LatLng(56.46371,-2.97591));
        list.add(new LatLng(56.46397,-2.97503));
        list.add(new LatLng(56.46425,-2.97415));
        list.add(new LatLng(56.46431,-2.97388));
        list.add(new LatLng(56.46437,-2.97351));
        list.add(new LatLng(56.46443,-2.97333));
        list.add(new LatLng(56.46449,-2.97332));
        list.add(new LatLng(56.46454,-2.97328));
        list.add(new LatLng(56.46459,-2.97313));
        list.add(new LatLng(56.4646,-2.97301));
        list.add(new LatLng(56.46457,-2.97291));
        list.add(new LatLng(56.46453,-2.97283));
        list.add(new LatLng(56.46445,-2.97277));
        list.add(new LatLng(56.46438,-2.97279));
        list.add(new LatLng(56.46423,-2.97257));
        list.add(new LatLng(56.46418,-2.97247));
        list.add(new LatLng(56.46413,-2.97226));
        list.add(new LatLng(56.46413,-2.97215));
        list.add(new LatLng(56.46417,-2.97194));
        list.add(new LatLng(56.46401,-2.9718));
        list.add(new LatLng(56.46391,-2.97173));
        list.add(new LatLng(56.4637,-2.9715));
        list.add(new LatLng(56.46362,-2.97182));
        list.add(new LatLng(56.46329,-2.97313));
        list.add(new LatLng(56.46323,-2.97334));

        return list;
    }


}




