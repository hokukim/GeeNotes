package com.weehoo.geenotes.menu;

import android.graphics.Bitmap;

/**
 * A menu item that can be added to a Menu.
 */
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

    public Bitmap getmBitmap() {
        return mBitmap;
    }
}
