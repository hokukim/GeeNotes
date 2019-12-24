package com.weehoo.geenotes.note;

/**
 * Note page can only be directly accessed by note book.
 */
public class NotePage {

    private String mID;

    /**
     * Constructs a new NotePage object with empty data.
     */
    public NotePage() {
        mID = "1"; // TODO: Secure random ID generation.
    }

    /**
     * Constructs a new NotePage object using the specified ID.
     */
    public NotePage(String id) {
        mID = id;
    }
}
