/**
 * This class is part of the V.I.S.O.R app.
 * The Journey class serves as a data struct to hold data regarding one entire journey.
 *
 * @version 1.0
 * @since 08/02/2024
 */

package com.matt.visor.app.recorder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.matt.visor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Journey {

    private String _id;
    private List<Waypoint> _waypoints;

    // Times
    private long _timeStarted;
    private long _timeFinished;
    private long _timeTotal;


    // Other measurements
    private double _totalDistance;
    private double _totalElevationClimbed;
    private double _totalElevationDescended;
    private double _averageSpeed;

    // Images
    private Bitmap image;
    public static final int DEFAULT_IMAGE = R.drawable.icon_history_24;

    // GPX file
    private File _gpxFile;

    // XML string constants identifying how are things saved
    public static final String XML_TimeStarted = "timeStarted";
    public static final String XML_TimeFinished = "timeFinished";
    public static final String XML_TimeTotalTime = "totalTime";
    public static final String XML_Distance = "totalDistance";
    public static final String XML_Climbed = "totalElevationClimbed";
    public static final String XML_Descended = "totalElevationDescended";
    public static final String XML_AvgSpeed = "totalAvgSpeed";


    /**
     * Default constructor initializing journey with default values and empty waypoints list.
     */
    public Journey() {
        _waypoints = new ArrayList<>();

        _timeStarted = 0;
        _timeFinished = 0;

        _totalDistance = 0;
        _totalElevationClimbed = 0;
        _totalElevationDescended = 0;
        _averageSpeed = 0;
    }

    /**
     * Constructs a journey from a map containing journey data and an identifier.
     *
     * @param map The map with journey data.
     * @param id The unique identifier for the journey.
     */
    public Journey(Map<String, String> map, String id) {
        try {
            _id = id;

            System.out.println("ID: " + id);
            _timeStarted = Long.parseLong(map.get(XML_TimeStarted));
            _timeFinished = Long.parseLong(map.get(XML_TimeFinished));
            _timeTotal = Long.parseLong(map.get(XML_TimeTotalTime));

            _totalDistance = Double.parseDouble(map.get(XML_Distance));
            _totalElevationClimbed = Double.parseDouble(map.get(XML_Climbed));
            _totalElevationDescended = Double.parseDouble(map.get(XML_Descended));
            _averageSpeed = Double.parseDouble(map.get(XML_AvgSpeed));
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the file path for the GPX file associated with this journey.
     *
     * @param gpxFile The GPX file.
     */
    public void setGpxFilePath(File gpxFile) {
        _gpxFile = gpxFile;
    }

    /**
     * Sets the image path for the journey's associated image, loading it if the file exists.
     *
     * @param fileImg The image file.
     */
    public void setImagePath(File fileImg) {
        if(fileImg.exists())
            image = BitmapFactory.decodeFile(fileImg.getAbsolutePath());
    }


    /**
     * Adds a new waypoint for the list of waypoint. If this is not the first waypoint, calculates
     * all necessary values based on previous waypoints.
     * Calculated:
     * - Total Time
     * - Total Distance
     * - Elevation Climbed and Descended
     * - Average Speed
     *
     * @param waypoint Current waypoint
     */
    public void addWaypoint(Waypoint waypoint) {

        // If first one - set times but don't calculate anything
        if(_waypoints.size() == 0) {
            _timeStarted = waypoint.getTime();
            _timeFinished = waypoint.getTime();
            _waypoints.add(waypoint);
            return;
        }

        // Times
        _timeFinished = waypoint.getTime();
        _timeTotal = _timeFinished - _timeStarted;

        // Distance
        _totalDistance += calculateDistanceDelta(waypoint);

        // Elevation
        double elevationDelta = calculateElevationDelta(waypoint);
        if(elevationDelta > 0)
            _totalElevationClimbed += elevationDelta;
        else if (elevationDelta < 0)
            _totalElevationDescended += elevationDelta;

        // Average speed
        _averageSpeed = calculateAvgSpeed();

        // add to the list
        _waypoints.add(waypoint);
    }

    /**
     * Calculates the difference in distance (Using Haversine formula) from the last waypoint to the
     * current one
     *
     * @param waypoint Current waypoint
     * @return Difference in distance in Km
     */
    private double calculateDistanceDelta(Waypoint waypoint) {
        double deltaDistance = HaversineCalculator.haversineDistance(
                getLastWaypoint().getLatitude()
                , getLastWaypoint().getLongitude()
                , waypoint.getLatitude()
                , waypoint.getLongitude()
        );

        return (deltaDistance / 1000); // Convert metres -> kilometres
    }

    /**
     * Calculates the difference in altitude from last waypoint to the current one
     *
     * @param waypoint Current waypoint
     * @return Difference in elevation
     */
    private double calculateElevationDelta(Waypoint waypoint) {
        return waypoint.getAltitude() - getLastWaypoint().getAltitude();
    }

    /**
     * Calculates average speed based on total distance over total time in hour
     *
     * @return Average speed in Km/h
     */
    private double calculateAvgSpeed() {
        return (getTotalDistance() / (getTimeTotal() / 3600F));
    }

    /**
     * Gets the last waypoint in the journey or a new waypoint if none exist.
     *
     * @return The last waypoint or a new default waypoint.
     */
    public Waypoint getLastWaypoint() {
        if(_waypoints.size() == 0){
            return new Waypoint();
        }

        return _waypoints.get(_waypoints.size() -1);
    }

    /**
     * Returns the unique identifier for the journey.
     *
     * @return The journey ID.
     */
    public String getJourneyID() {
        return _id;
    }

    /**
     * Gets the start time of the journey in seconds since epoch.
     *
     * @return The start time.
     */
    public long getTimeStarted() {
        return _timeStarted;
    }

    /**
     * Gets the finish time of the journey in seconds since epoch.
     *
     * @return The finish time.
     */
    public long getTimeFinished() {
        return _timeFinished;
    }

    /**
     * Gets the total time of the journey in seconds.
     *
     * @return The total time.
     */
    public long getTimeTotal() {
        return _timeTotal;
    }

    /**
     * Gets the total distance traveled in the journey in kilometers.
     *
     * @return The total distance.
     */
    public double getTotalDistance() {
        return _totalDistance;
    }

    /**
     * Gets the total elevation climbed in the journey in kilometers.
     *
     * @return The total elevation climbed.
     */
    public double getTotalElevationClimbed() {
        return _totalElevationClimbed;
    }

    /**
     * Gets the total elevation descended in the journey in kilometers.
     *
     * @return The total elevation descended.
     */
    public double getGet_totalElevationDescended() {
        return _totalElevationDescended;
    }

    /**
     * Gets the average speed during the journey in kilometers per hour.
     *
     * @return The average speed.
     */
    public double getAverageSpeed() {
        return _averageSpeed;
    }

    /**
     * Gets a list of all waypoints in the journey.
     *
     * @return The list of waypoints.
     */
    public List<Waypoint> getAllWaypoints() {
        return _waypoints;
    }

    /**
     * Gets the image associated with the journey.
     *
     * @return The journey's image.
     */
    public Bitmap getImage() {
        return image;
    }

    /**
     * Returns the journey's metadata as an XML string.
     *
     * @return XML string representation of the journey's metadata.
     */
    public String metadataXmlString() {
        Map<String, String> data = new LinkedHashMap<>();

        data.put(XML_TimeStarted, Long.toString(_timeStarted));
        data.put(XML_TimeFinished, Long.toString(_timeFinished));
        data.put(XML_TimeTotalTime, Long.toString(_timeTotal));

        data.put(XML_Distance, Double.toString(_totalDistance));
        data.put(XML_Climbed, Double.toString(_totalElevationClimbed));
        data.put(XML_Descended, Double.toString(_totalElevationDescended));
        data.put(XML_AvgSpeed, Double.toString(_averageSpeed));

        return HelperXML.oneItemToXml(data);
    }


    /**
     * Returns the journey's GPS file.
     *
     * @return GPX file of the route.
     */
    public File gptGpxFile() {
        return _gpxFile;
    }


}
