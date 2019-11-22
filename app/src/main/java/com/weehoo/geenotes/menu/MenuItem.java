package com.weehoo.geenotes.menu;

import android.graphics.Bitmap;

public class MenuItem {
    private String mId;
    private Bitmap mBitmap;

    public MenuItem(String id, Bitmap bitmap) {
        mId = id;
        mBitmap = bitmap;
    }

    public String getId() {
        return mId;
    }
}
