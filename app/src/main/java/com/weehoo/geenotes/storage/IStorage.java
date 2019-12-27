package com.weehoo.geenotes.storage;

import android.graphics.Bitmap;

public interface IStorage {
    /**
     * Read a file from storage and retrieve its contents as a string.
     * @param fileName File name.
     * @return File contents as a string.
     */
    String getFileString(String fileName);

    /**
     * Writes a string to the file.
     * The entire file's contents are overwritten.
     * Creates a new file if it does not exist.
     * @param fileName File name.
     * @param fileString File contents as a string.
     */
    void setFileString(String fileName, String fileString);

    /**
     * Read a file from storage and retrieve its contents as a bitmap.
     * @param fileName File name.
     * @return File contents as a bitmap.
     */
    Bitmap getFileBitmap(String fileName);

    /**
     * Write a bitmap to the file.
     * The entire file's contents are overwritten.
     * Creates a new file if it does not exist.
     * @param fileName File name.
     * @param fileBitmap File contents as a bitmap.
     */
    void setFileBitmap(String fileName, Bitmap fileBitmap);

    /**
     * Delete a file.
     * @param fileName Name of file to delete.
     */
    void deleteFile(String fileName);
}
