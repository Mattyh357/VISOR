/**
 * This class is part of the V.I.S.O.R app.
 * The HelperGPX class is responsible formatting a list of waypoint into an appropriate format for
 * sites like Strava.
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.app.recorder;

import com.matt.visor.Utils;

import java.util.List;

public class HelperGPX {

    private String _name;
    private String _activity;


    // TODO to be removed
    public HelperGPX() {
        //TODO REMOVE THIS CRAP!
        // check strava for what needs to be there
    }

    /**
     * Initializes HelperGPX with specified name and activity.
     *
     * @param name     The name of the GPX track.
     * @param activity The type of activity.
     */
    public HelperGPX(String name, String activity) {
        _name = name;
        _activity = activity;
    }


    /**
     * Generates GPX format string from a list of waypoints.
     *
     * @param list The list of waypoints to include in the GPX.
     * @return GPX format string.
     */
    public String getGPX(List<Waypoint> list) {
        // Get time of last item for metadata
        long time = list.get(list.size() - 1).getTime();

        StringBuilder sb = new StringBuilder();

        sb.append(getDeclaration());
        sb.append(getMetadata(Utils.UnixToZulu(time)));
        sb.append(getHead(_name, _activity));
        sb.append(getBody(list));
        sb.append(getTail());


        return sb.toString();
    }

    /**
     * Builds the GPX body section from a list of waypoints.
     *
     * @param list The list of waypoints.
     * @return GPX body as a string.
     */
    private String getBody(List<Waypoint> list) {
        StringBuilder sb = new StringBuilder();

        for (Waypoint data : list) {
            sb.append(waypointData2gpx(data));
        }

        return sb.toString();
    }

    /**
     * Converts a single waypoint data to GPX format.
     *
     * @param waypoint The waypoint to convert.
     * @return GPX format string for the waypoint.
     */
    private String waypointData2gpx(Waypoint waypoint) {
        if (waypoint.getLatitude() == 0 && waypoint.getLongitude() == 0)
            return "";

        StringBuilder sb = new StringBuilder();

        sb.append("   <trkpt lat=\"").append(waypoint.getLatitude()).append("\" lon=\"").append(waypoint.getLongitude()).append("\">");
        sb.append("\n");
        sb.append("    <ele>").append(waypoint.getAltitude()).append("</ele>");
        sb.append("\n");
        sb.append("    <time>").append(Utils.UnixToZulu(waypoint.getTime())).append("</time>");
        sb.append("\n");

        // TODO fix this

//        if(waypoint.hasExtension()){
//            sb.append("    <extensions>");
//            sb.append("\n");
//            sb.append("     <gpxtpx:TrackPointExtension>");
//            sb.append("\n");
//
//            if(waypoint.heartRate >= 0)
//                sb.append("      <gpxtpx:hr>").append(waypoint.heartRate).append("</gpxtpx:hr>").append("\n");
//
//            if(waypoint.cadence >= 0)
//                sb.append("      <gpxtpx:cad>").append(waypoint.cadence).append("</gpxtpx:cad>").append("\n");
//
//            if(waypoint.power >= 0)
//                sb.append("      <gpxtpx:power>").append(waypoint.power).append("</gpxtpx:power>").append("\n");
//
//            sb.append("     </gpxtpx:TrackPointExtension>");
//            sb.append("\n");
//            sb.append("    </extensions>");
//            sb.append("\n");
//        }

        sb.append("   </trkpt>");
        sb.append("\n");


        return sb.toString();
    }

    /**
     * Generates the GPX declaration header.
     *
     * @return GPX declaration as a string.
     */
    private String getDeclaration() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<gpx");

        //head stuff

        sb.append(">");
        sb.append("\n");

        return sb.toString();
    }

    /**
     * Generates the metadata section for GPX.
     *
     * @param time The time for the metadata section.
     * @return GPX metadata section as a string.
     */
    private String getMetadata(String time) {
        StringBuilder sb = new StringBuilder();
        sb.append(" <metadata>");
        sb.append("\n");

        sb.append("  <time>").append(time).append("</time>");
        sb.append("\n");

        sb.append(" </metadata>");
        sb.append("\n");

        return sb.toString();
    }

    /**
     * Generates the head section for GPX with name and type.
     *
     * @param name The name of the GPX track.
     * @param type The type of activity.
     * @return GPX head section as a string.
     */
    private String getHead(String name, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(" <trk>");
        sb.append("\n");

        sb.append("  <name>").append(name).append("</name>");
        sb.append("\n");
        sb.append("  <type>").append(type).append("</type>");
        sb.append("\n");

        sb.append("  <trkseg>");
        sb.append("\n");

        return sb.toString();
    }

    /**
     * Generates the tail section for GPX.
     *
     * @return GPX tail section as a string.
     */
    private String getTail() {
        StringBuilder sb = new StringBuilder();

        sb.append("  </trkseg>");
        sb.append("\n");

        sb.append(" </trk>");
        sb.append("\n");

        sb.append("</gpx>");
        sb.append("\n");

        return sb.toString();
    }

}
