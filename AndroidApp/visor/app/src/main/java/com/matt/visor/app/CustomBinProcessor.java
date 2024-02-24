/**
 * This class is part of the V.I.S.O.R app.
 * The CustomBinProcessor is responsible for reading CustomBinaryFile and processing it into Bitmap
 * images which can then be used in the app.
 *
 * @version 1.0
 * @since 24/02/2024
 */

package com.matt.visor.app;
import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;


public class CustomBinProcessor {
    int scaleFactor = 6;
    private List<byte[]> _list = new ArrayList<>();
    private int _width = 0;
    private int _height = 0;

    /**
     * Constructs a processor for binary files to create bitmaps, verifying file existence.
     *
     * @param file The binary file to be processed.
     */
    public CustomBinProcessor(File file) {
        if(!file.exists())
            System.out.println("PROBLEM - file doesn't exist");

        processBinFile(file.getAbsolutePath());
    }

    /**
     * Converts a list of byte arrays to a list of Bitmaps.
     *
     * @return List of Bitmaps generated from byte arrays.
     */
    public List<Bitmap> getListOfBitmaps() {
        List<Bitmap> list = new ArrayList<>();

        for (byte[] b : _list) {
            list.add(getBitmap(b));
        }

        return list;
    }

    /**
     * Generates a Bitmap from byte array data, including scaling.
     *
     * @param imageData The byte array containing bitmap data.
     * @return A scaled Bitmap created from the byte array.
     */
    private Bitmap getBitmap(byte[] imageData) {

        Bitmap originalBitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);

        for (int y = 0; y < _height; y++) {
            for (int x = 0; x < _width; x++) {
                int index = (y * _width + x) / 8;
                int bitPosition = x % 8;
                byte currentByte = imageData[index];
                int pixelValue = ((currentByte >> (7 - bitPosition)) & 1) == 1 ? Color.BLACK : Color.WHITE;
                originalBitmap.setPixel(x, y, pixelValue);
            }
        }

        // Scale the bitmap
        return Bitmap.createScaledBitmap(originalBitmap, _width * scaleFactor, _height * scaleFactor, false);
    }

    /**
     * Reads a binary file specified by its filepath to extract metadata and raw image data.
     * This includes reading the number of images, width, height, and constructing a byte array for each image.
     * Assumes the file format includes the image count, width, and height in little-endian order at the beginning.
     * Handles I/O exceptions and prints error messages if reading fails or data is incomplete.
     *
     * @param filepath The path to the binary file containing image data.
     */
    private void processBinFile(String filepath) {
        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(filepath))) {

            // Read and convert integers assuming little-endian format
            byte[] intBuffer = new byte[4];

            dataInputStream.readFully(intBuffer);
            int numberOfImages = (int)(ByteBuffer.wrap(intBuffer).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xffffffffL);
            dataInputStream.readFully(intBuffer);
            _width = (int)(ByteBuffer.wrap(intBuffer).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xffffffffL);
            dataInputStream.readFully(intBuffer);
            _height = (int)(ByteBuffer.wrap(intBuffer).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xffffffffL);

            long imageSize = _width * _height / 8;

            for (int i = 0; i < numberOfImages; ++i) {
                byte[] imageData = new byte[(int) imageSize];

                int bytesRead = dataInputStream.read(imageData, 0, imageData.length);
                if (bytesRead != imageSize) {
                    System.err.println("Error reading image data");
                    break;
                }
                // Debug
                System.out.print("Image " + (i + 1) + " data: ");
                for (int j = 0; j < imageSize; ++j) {
                    System.out.printf("%02x ", imageData[j]);
                }
                System.out.println();

                _list.add(imageData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}