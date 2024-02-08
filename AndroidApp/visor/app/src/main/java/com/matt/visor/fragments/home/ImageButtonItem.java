// TODO remove after testing of HUD image sender

package com.matt.visor.fragments.home;

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
