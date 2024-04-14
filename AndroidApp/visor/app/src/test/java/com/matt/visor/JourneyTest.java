package com.matt.visor;


import static org.junit.Assert.assertEquals;

import com.matt.visor.app.recorder.HaversineCalculator;
import com.matt.visor.app.recorder.Journey;
import com.matt.visor.app.recorder.Waypoint;

import org.junit.Test;

public class JourneyTest {

    double earthIsNotFlat = HaversineCalculator.haversineDistance(1.0, 1.0, 1.0, 1.01);


    @Test
    public void TimeStarted() {

        Journey journey1 = data_go_east_and_back();
        Journey journey2 = data_go_in_square();
        double delta = 0.01;

        assertEquals("Time started J1: ", 946728000, journey1.getTimeStarted(), delta);
        assertEquals("Time started J2: ", 946728000, journey2.getTimeStarted(), delta);
    }

    @Test
    public void TimeFinished() {
        Journey journey1 = data_go_east_and_back();
        Journey journey2 = data_go_in_square();
        double delta = 0.01;

        assertEquals("Time finished J1: ", 946746000, journey1.getTimeFinished(), delta);
        assertEquals("Time finished J2: ", 946749600, journey2.getTimeFinished(), delta);
    }

    @Test
    public void TotalTime() {
        Journey journey1 = data_go_east_and_back();
        Journey journey2 = data_go_in_square();
        double delta = 0.01;


        double expected1 = 18000; // 5hours
        double expected2 = 21600; // 6hours

        assertEquals("Total ride time J1: ", expected1, journey1.getTimeTotal(), delta);
        assertEquals("Total ride time J2: ", expected2, journey2.getTimeTotal(), delta);
    }

    @Test
    public void TotalDistance() {
        Journey journey1 = data_go_east_and_back();
        Journey journey2 = data_go_in_square();
        double delta = 0.01;

        double expectedJ1 = (20 * earthIsNotFlat) / 1000;
        double expectedJ2 = (6 * earthIsNotFlat) / 1000;

        assertEquals("Total distance J1: ", expectedJ1, journey1.getTotalDistance(), delta);
        assertEquals("Total distance J2: ", expectedJ2, journey2.getTotalDistance(), delta);
    }

    @Test
    public void TotalAvgSpeed() {
        Journey journey1 = data_go_east_and_back();
        Journey journey2 = data_go_in_square();
        double delta = 0.01;

        // total distance and total time is tested elsewhere - so it's correct

        double distance1 = journey1.getTotalDistance();
        double travelTime1 = journey1.getTimeTotal() / 3600; // Sec -> hour
        double expected1 = (distance1 / travelTime1);

        double distance2 = journey2.getTotalDistance();
        double travelTime2 = journey2.getTimeTotal() / 3600; //Sec -> hour
        double expected2 = (distance2 / travelTime2);

        assertEquals("Total avg speed J1: ", expected1, journey1.getAverageSpeed(), delta);
        assertEquals("Total avg speed J2: ", expected2, journey2.getAverageSpeed(), delta);
    }

    @Test
    public void TotalElevationClimbed() {
        Journey journey1 = data_go_east_and_back();
        Journey journey2 = data_go_in_square();
        double delta = 1;

        double expectedJ1 = 100;
        double expectedJ2 = 40;

        assertEquals("Total elevation gained J1: ", expectedJ1, journey1.getTotalElevationClimbed(), delta);
        assertEquals("Total elevation gained J2: ", expectedJ2, journey2.getTotalElevationClimbed(), delta);
    }

    @Test
    public void TotalElevationDescended() {
        Journey journey1 = data_go_east_and_back();
        Journey journey2 = data_go_in_square();
        double delta = 1;

        double expectedJ1 = -100;
        double expectedJ2 = -40;

        assertEquals("Total elevation descended J1: ", expectedJ1, journey1.getGet_totalElevationDescended(), delta);
        assertEquals("Total elevation descended J2: ", expectedJ2, journey2.getGet_totalElevationDescended(), delta);
    }

    @Test
    public void TestMetadata() {

        // All the journey.getStuff() are tested elsewhere so we know it correct

        Journey journey1 = data_go_east_and_back();
        Journey journey2 = data_go_in_square();

        String expectedJ1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<root>\n" +
                "  <item>\n" +
                "    <timeStarted>" + journey1.getTimeStarted() + "</timeStarted>\n" +
                "    <timeFinished>" + journey1.getTimeFinished() + "</timeFinished>\n" +
                "    <totalTime>" + journey1.getTimeTotal() + "</totalTime>\n" +

                "    <totalDistance>" + journey1.getTotalDistance() + "</totalDistance>\n" +
                "    <totalElevationClimbed>" + journey1.getTotalElevationClimbed() + "</totalElevationClimbed>\n" +
                "    <totalElevationDescended>" + journey1.getGet_totalElevationDescended() + "</totalElevationDescended>\n" +
                "    <totalAvgSpeed>" + journey1.getAverageSpeed() + "</totalAvgSpeed>\n" +
                "  </item>\n" +
                "</root>\n";

        String expectedJ2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<root>\n" +
                "  <item>\n" +
                "    <timeStarted>" + journey2.getTimeStarted() + "</timeStarted>\n" +
                "    <timeFinished>" + journey2.getTimeFinished() + "</timeFinished>\n" +
                "    <totalTime>" + journey2.getTimeTotal() + "</totalTime>\n" +

                "    <totalDistance>" + journey2.getTotalDistance() + "</totalDistance>\n" +
                "    <totalElevationClimbed>" + journey2.getTotalElevationClimbed() + "</totalElevationClimbed>\n" +
                "    <totalElevationDescended>" + journey2.getGet_totalElevationDescended() + "</totalElevationDescended>\n" +
                "    <totalAvgSpeed>" + journey2.getAverageSpeed() + "</totalAvgSpeed>\n" +
                "  </item>\n" +
                "</root>\n";


        assertEquals("Total elevation descended J1: ", expectedJ1, journey1.metadataXmlString());
        assertEquals("Total elevation descended J1: ", expectedJ2, journey2.metadataXmlString());


    }


    /*
     * this Journey should:
     * distance: 20KM on flat earth :D real is something like 22.23KM....
     * time: 5h
     * avg speed: 4.447 something something
     * ele+: 100
     * ele-: 100
     */
    public Journey data_go_east_and_back() {

        Waypoint waypoint1 = new Waypoint();
        waypoint1.setLatitude(1.0);
        waypoint1.setLongitude(1.01);
        waypoint1.setAltitude(10);
        waypoint1.setTime(946728000); // 1/1/2000 - 12:00

        Waypoint waypoint2 = new Waypoint();
        waypoint2.setLatitude(1.0);
        waypoint2.setLongitude(1.02);           // 1 km east (total 1km)
        waypoint2.setAltitude(20);              // 10 up
        waypoint2.setTime(946731600);   // 1/1/2000 - 13:00

        Waypoint waypoint3 = new Waypoint();
        waypoint3.setLatitude(1.0);
        waypoint3.setLongitude(1.03);            // 1 km east (total 2km)
        waypoint3.setAltitude(30);              // 10 up
        waypoint3.setTime(946735200);   // 1/1/2000 - 14:00

        Waypoint waypoint4 = new Waypoint();
        waypoint4.setLatitude(1.0);
        waypoint4.setLongitude(1.02);           // 1 km west (total 3km)
        waypoint4.setAltitude(20);              //10 down
        waypoint4.setTime(946738800);   // 1/1/2000 - 15:00

        Waypoint waypoint5 = new Waypoint();
        waypoint5.setLatitude(1.0);
        waypoint5.setLongitude(1.10);           // 8 km east (total 11km)
        waypoint5.setAltitude(100);             //80 up
        waypoint5.setTime(946742400);   // 1/1/2000 - 16:00

        Waypoint waypoint6 = new Waypoint();
        waypoint6.setLatitude(1.0);
        waypoint6.setLongitude(1.01);           // 9 km west (total 20km)
        waypoint6.setAltitude(10);              //90 down
        waypoint6.setTime(946746000);   // 1/1/2000 - 17:00

        Journey journey = new Journey();

        journey.addWaypoint(waypoint1);
        journey.addWaypoint(waypoint2);
        journey.addWaypoint(waypoint3);
        journey.addWaypoint(waypoint4);
        journey.addWaypoint(waypoint5);
        journey.addWaypoint(waypoint6);

        return journey;
    }


    /*
     * this Journey should:
     * distance: 6KM on flat earth :D
     * time: 6h
     * avg speed: I dunno.... about.... 6/6... carry the one.... a million!
     * ele+: 40
     * ele-: 40
     */
    public Journey data_go_in_square() {

        Waypoint waypoint1 = new Waypoint();
        waypoint1.setLatitude(1.0);
        waypoint1.setLongitude(1.0);
        waypoint1.setAltitude(10);
        waypoint1.setTime(946728000); // 1/1/2000 - 12:00

        Waypoint waypoint2 = new Waypoint();
        waypoint2.setLatitude(1.0);             //
        waypoint2.setLongitude(1.01);           // 1 km east
        waypoint2.setAltitude(20);              // 10 up
        waypoint2.setTime(946731600);   // 1/1/2000 - 13:00

        Waypoint waypoint3 = new Waypoint();
        waypoint3.setLatitude(1.01);            // 1 km north
        waypoint3.setLongitude(1.01);           //
        waypoint3.setAltitude(30);              // 10 up
        waypoint3.setTime(946735200);   // 1/1/2000 - 14:00

        Waypoint waypoint4 = new Waypoint();
        waypoint4.setLatitude(1.01);            //
        waypoint4.setLongitude(1.0);            // 1 km west
        waypoint4.setAltitude(10);              // 20 down
        waypoint4.setTime(946738800);   // 1/1/2000 - 15:00

        Waypoint waypoint5 = new Waypoint();
        waypoint5.setLatitude(1.01);
        waypoint5.setLongitude(0.99);           // 1 km west
        waypoint5.setAltitude(30);              // 20 up
        waypoint5.setTime(946742400);   // 1/1/2000 - 16:00

        Waypoint waypoint6 = new Waypoint();
        waypoint6.setLatitude(1.0);             // 1 km south
        waypoint6.setLongitude(0.99);           //
        waypoint6.setAltitude(20);              // 10 down
        waypoint6.setTime(946746000);   // 1/1/2000 - 17:00

        Waypoint waypoint7 = new Waypoint();
        waypoint7.setLatitude(1.0);
        waypoint7.setLongitude(1.0);           // 1 km east
        waypoint7.setAltitude(10);              // 10 down
        waypoint7.setTime(946749600);   // 1/1/2000 - 18:00

        Journey journey = new Journey();

        journey.addWaypoint(waypoint1);
        journey.addWaypoint(waypoint2);
        journey.addWaypoint(waypoint3);
        journey.addWaypoint(waypoint4);
        journey.addWaypoint(waypoint5);
        journey.addWaypoint(waypoint6);
        journey.addWaypoint(waypoint7);

        return journey;
    }

}


