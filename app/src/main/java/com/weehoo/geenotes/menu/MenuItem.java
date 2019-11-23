package com.weehoo.geenotes.menu;

import android.graphics.Bitmap;

/**
 * A menu item that can be added to a Menu.
 */
public class MenuItem {
    private MenuItemType mType;
    private Bitmap mBitmap;

    public MenuItem(MenuItemType type, Bitmap bitmap) {
        mType = type;
        mBitmap = bitmap;
    }

    public MenuItemType getType() {
        return mType;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
