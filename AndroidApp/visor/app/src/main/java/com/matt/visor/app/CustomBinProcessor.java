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
    private int _numberOfImages = 0;
    private int _width = 0;
    private int _height = 0;

    public CustomBinProcessor(File file) {
        if(!file.exists())
            System.out.println("PROBLEM - file doesn't exist");

        processBinFile(file.getAbsolutePath());
    }


    public List<Bitmap> getListOfBitmaps() {
        List<Bitmap> list = new ArrayList<>();

        for (byte[] b : _list) {
            list.add(getBitmap(b));
        }


        return list;
    }

    public Bitmap getBitmap(byte[] imageData) {

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

    // TODO old - remove
    public Bitmap getBitmap(int imgNumber) {

        Bitmap originalBitmap = Bitmap.createBitmap(_width, _height, Bitmap.Config.ARGB_8888);

        byte[] imageData = _list.get(imgNumber); // TODO oob check

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


    private void processBinFile(String filepath) {

        try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(filepath))) {

            // Read and convert integers assuming little-endian format
            byte[] intBuffer = new byte[4];

            dataInputStream.readFully(intBuffer);
            _numberOfImages = (int)(ByteBuffer.wrap(intBuffer).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xffffffffL);
            dataInputStream.readFully(intBuffer);
            _width = (int)(ByteBuffer.wrap(intBuffer).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xffffffffL);
            dataInputStream.readFully(intBuffer);
            _height = (int)(ByteBuffer.wrap(intBuffer).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xffffffffL);

            long imageSize = _width * _height / 8;

            for (int i = 0; i < _numberOfImages; ++i) {
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