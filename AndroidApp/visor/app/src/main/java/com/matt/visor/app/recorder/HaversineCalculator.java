/**
 * This class is part of the V.I.S.O.R app.
 * This method for calculating distance between two points on the earth was takes from:
 * https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
 *
 * @version 1.0
 * @since 21/02/2024
 */

//

package com.matt.visor.app.recorder;

public class HaversineCalculator {

    /**
     * Calculates the Haversine distance between two points on the earth.
     *
     * @param lat1 Latitude of the first point.
     * @param lon1 Longitude of the first point.
     * @param lat2 Latitude of the second point.
     * @param lon2 Longitude of the second point.
     * @return The distance in meters.
     */
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371e3; // Radius of the earth in METERS!!!

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // distance in km
    }

}
