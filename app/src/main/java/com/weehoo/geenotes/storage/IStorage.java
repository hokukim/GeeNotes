package com.weehoo.geenotes.storage;

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
     * @param fileName
     * @param fileString
     */
    void setFileString(String fileName, String fileString);
}
