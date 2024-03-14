/**
 * This class is part of the V.I.S.O.R app.
 * Internal Storage helper
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.app.recorder;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class HelperInternalStorage {


    /**
     * Saves text to a specified file and directory within the app's private storage.
     *
     * @param context   The application context.
     * @param directory The directory within internal storage to save the file.
     * @param filename  The name of the file.
     * @param ext       The file extension.
     * @param text      The text to save.
     * @return True if the text was successfully saved, false otherwise.
     */
    public static boolean saveTextToFile(Context context, String directory, String filename, String ext, String text) {
        try {
            File dir = new File(context.getFilesDir(), directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, filename + "." + ext);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(text.getBytes());
            fos.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Loads text from a file in the app's private storage.
     *
     * @param context  The application context.
     * @param filename The name of the file to load the text from.
     * @return The loaded text, or an error message if the file could not be opened.
     */
    public static String loadTextFromFile(Context context, String filename) {
        //check file first
        File file = context.getFileStreamPath(filename);
        if(!file.exists())
            return "";

        try {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while (((line = br.readLine())) != null) {
                sb.append(line);
            }
            fis.close();
            isr.close();
            br.close();

            return sb.toString();
        } catch (Exception e) {
            return "Problem opening file...";
        }
    }

}
