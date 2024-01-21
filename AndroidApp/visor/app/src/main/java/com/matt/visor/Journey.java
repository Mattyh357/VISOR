package com.matt.visor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.matt.visor.Utils;

import java.io.File;

public class Journey {

    public int id; //TODO DO SOMETHING WITH IT :)
    //TODO from / to map

    public long timeStarted = -1;
    public long timeFinished = -1;
    public long totalTime = 0;


    public double totalDistance = 0;
    public double totalElevationClimbed = 0;
    public double totalElevationDescended = 0;
    public double totalAvgSpeed = -1;

    public Bitmap image;

    private File _gpxFile;


    public static final String XML_TimeStarted = "timeStarted";
    public static final String XML_TimeFinished = "timeFinished";
    public static final String XML_TimeTotalTime = "totalTime";
    public static final String XML_Distance = "totalDistance";
    public static final String XML_Climbed = "totalElevationClimbed";
    public static final String XML_Descended = "totalElevationDescended";
    public static final String XML_AvgSpeed = "totalAvgSpeed";



//TODO constructor?




    public void setGpxFilePath(File gpxFile) {
        _gpxFile = gpxFile;
    }

    public void setImagePath(File fileImg) {
        if(fileImg.exists())
            image = BitmapFactory.decodeFile(fileImg.getAbsolutePath());
    }


    public Bitmap getImage() {
        return image;
    }

    public int getDefaultImage() {
        return R.drawable.icon_history_24;
    }


    public String getTimeStarted() {
        return Utils.formatUnixToDateAndTime(timeStarted);
    }


    public String getTimeFinished() {
        return Utils.formatUnixToDateAndTime(timeFinished);
    }

    public String getDuration() {
        long dur = totalTime / 1000;
        long hours = dur / 3600;
        long minutes = (dur % 3600) / 60;
        long seconds = dur % 60;


//        return Long.toString(totalTime);
        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }


    public String getDistance() {
        double x = Math.round(totalDistance * 100.0) / 100.0; //TODO NOT HERE BUT IN THE META CALCUALTOR

        return Double.toString(x) + " Km";
    }

    public String getAvgSpeed() {
        return Double.toString(totalAvgSpeed);
    }

    public String getElevationClimbed() {
        return Double.toString(totalElevationClimbed);
    }

    public String getElevationDescended() {
        return Double.toString(totalElevationClimbed);
    }

}
