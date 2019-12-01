package com.weehoo.geenotes.dimensions;

import android.content.Context;

import com.weehoo.geenotes.GeeNotesApplication;

public class StatusBar {
    public static int getStatusBarHeight() {
        int height = 0;
        Context context = GeeNotesApplication.getContext();
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (identifier > 0) {
            height = context.getResources().getDimensionPixelSize(identifier);
        }

        return height;
    }
}
