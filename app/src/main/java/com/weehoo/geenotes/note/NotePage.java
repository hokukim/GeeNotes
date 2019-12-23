package com.weehoo.geenotes.note;

import android.graphics.Bitmap;

/**
 * Note page can only be directly accessed by note book.
 */
public class NotePage {

    private String mID;

    /**
     * Constructs a new NotePage object with empty data.
     * Bitmap is configured by default to ARGB_8888.
     */
    public NotePage() {
        mID = "1"; // TODO: Secure random ID generation.
    }
}
