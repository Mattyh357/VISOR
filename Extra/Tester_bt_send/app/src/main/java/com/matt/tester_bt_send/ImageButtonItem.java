package com.matt.tester_bt_send;

import android.graphics.Bitmap;

public class ImageButtonItem {

    private final Bitmap _image;
    private final String _title;


    public ImageButtonItem(String _title, Bitmap _image) {
        this._title = _title;
        this._image = _image;
    }

    public Bitmap getImageBitmap() {
        return _image;
    }

    public String getTitle() {
        return _title;
    }
}
