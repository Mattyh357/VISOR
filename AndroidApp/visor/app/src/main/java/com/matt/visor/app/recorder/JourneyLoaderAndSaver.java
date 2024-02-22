/**
 * This class is part of the V.I.S.O.R app.
 * The JourneyLoaderAndSaver is responsible for saving and loading journey into the application's
 * internal storage based on its ID, which ends up being the folder name.
 *
 * @version 1.0
 * @since 21/02/2024
 */


package com.matt.visor.app.recorder;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JourneyLoaderAndSaver {

    public static final String JOURNEY_FOLDER = "journeys";

    /**
     * Retrieves all journeys stored in the application's internal storage.
     *
     * @param context The application context.
     * @return A list of Journey objects.
     */
    public static List<Journey> getAllJourneys(Context context) {
        List<Journey> list = new ArrayList<>();

        for(String journeyID : getAllSubfolder(JOURNEY_FOLDER, context)) {
            list.add(getJourneyById(journeyID, context));
        }

        return list;
    }

    /**
     * Retrieves a single journey by its ID from internal storage.
     *
     * @param journeyID The ID of the journey to retrieve.
     * @param context   The application context.
     * @return A Journey object if found, or null.
     */

    public static Journey getJourneyById(String journeyID, Context context) {

        File dir = new File(context.getFilesDir(), JOURNEY_FOLDER + "/" + journeyID);
        if (dir.exists() && dir.isDirectory()) {
            System.out.println(dir);
        }

        //check all files
        File file_xml = new File(context.getFilesDir(), JOURNEY_FOLDER + "/" + journeyID + "/" + journeyID + ".xml");
        File file_gpx = new File(context.getFilesDir(), JOURNEY_FOLDER + "/" + journeyID + "/" + journeyID + ".gpx");
        File file_img = new File(context.getFilesDir(), JOURNEY_FOLDER + "/" + journeyID + "/" + journeyID + ".png");

        if(!file_xml.exists()){
            System.out.println("PROBLEM: " + file_xml);
        }

        if(!file_gpx.exists()){
            System.out.println("PROBLEM: " + file_gpx);
        }

        if(!file_img.exists()) {
            System.out.println("PROBLEM: " + file_img);
        }
        else
            System.out.println("Loaded Image :): " + file_img);

        //new journey
        Journey journey = new Journey(getParsedMapFromFile(file_xml), journeyID);
        journey.setGpxFilePath(file_gpx);
        journey.setImagePath(file_img);

        return journey;
    }

    /**
     * Retrieves all sub-folders from a specified root directory within the app's internal storage.
     *
     * @param root    The root directory to search within.
     * @param context The application context.
     * @return A list of subfolder names found within the specified root directory.
     */

    private static List<String> getAllSubfolder(String root, Context context) {
        List<String> list = new ArrayList<>();
        File rootDir = new File(context.getFilesDir(), root);

        // If root directory exists and is a directory
        if (rootDir.exists() && rootDir.isDirectory()) {
            list.addAll(Arrays.asList(rootDir.list()));
        }

        return list;
    }

    /**
     * Parses an XML file into a Map of strings.
     *
     * @param file The XML file to parse.
     * @return A Map containing the parsed key-value pairs from the XML file.
     */
    private static Map<String, String> getParsedMapFromFile(File file) {
        String content = readFileContent(file); // TODO check for null
        Map<String, String> map = HelperXML.fromXml(content);
        return map;
    }

    /**
     * Reads and returns the content of a file as a string.
     *
     * @param file The file to read from.
     * @return The content of the file as a string, or null if the file doesn't exist or an error occurs.
     */
    private static String readFileContent(File file) {

        if (!file.exists() || !file.isFile()) {
            return null;
        }

        StringBuilder content = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return content.toString();
    }

    /**
     * Generates the next unique identifier based on the number of folders in the JOURNEY_FOLDER directory.
     *
     * @param context The application context.
     * @return The next unique identifier for a new journey.
     */

    private static int getNextId(Context context) {
        File root = new File(context.getFilesDir(), JOURNEY_FOLDER);
        int folderCount = root.list() != null ? root.list().length : 0;
        return folderCount + 1;
    }

    /**
     * Asynchronously saves a journey's metadata, GPX data, and map image to internal storage.
     *
     * @param context  The application context.
     * @param map      The GoogleMap object for capturing a snapshot.
     * @param journey  The journey object containing metadata and waypoints.
     * @param callback The callback to notify upon completion of the save operation.
     */

    public static void saveJourney(Context context, GoogleMap map, Journey journey, Callback callback) {
        new Thread(() -> {
            System.out.println("Processing started");

            //get name
            int id = getNextId(context); //journey_
            String filename = "journey_" + id;
            String path = "journeys/journey_" + id;

            //Metadata
            boolean met_save_status = HelperInternalStorage.saveTextToFile(context, path, filename, "xml", journey.metadataXmlString());

            // GPX
            HelperGPX gpx = new HelperGPX();
            String gpxString = gpx.getGPX(journey.getAllWaypoints());
            boolean gpx_save_status = HelperInternalStorage.saveTextToFile(context, path, filename, "gpx", gpxString);

            //Image
            saveThumbnailFromMap(context, path, filename, map);

            // Introduce a sleep delay for dramatic effect :)
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            System.out.println("Processing DONE");

            // Notify once processing is done
            if (callback != null)
                callback.onSaveComplete();

        }).start();
    }

    /**
     * Captures and saves a snapshot of the current state of the GoogleMap into an image file.
     *
     * @param context   The application context.
     * @param directory The directory to save the image file.
     * @param filename  The name of the image file (without extension).
     * @param mapCrap   The GoogleMap object to capture the snapshot from.
     */
    private static void saveThumbnailFromMap(Context context, String directory, String filename, GoogleMap mapCrap) {

        System.out.println("SAVING IMAGE");

        mapCrap.snapshot(callback -> {
            try {
                //saving
                File dir = new File(context.getFilesDir(), directory);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(dir, filename + ".png");
                FileOutputStream fos = new FileOutputStream(file);

                callback.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush(); // fos.write(text.getBytes());
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        System.out.println("Image saved... I THINK? - " + directory + "/" + filename + ".png");
    }

    /**
     * Interface to notify when the journey save operation is complete.
     */
    public interface Callback {
        void onSaveComplete();
    }

}