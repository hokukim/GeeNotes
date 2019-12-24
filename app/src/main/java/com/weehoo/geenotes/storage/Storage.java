package com.weehoo.geenotes.storage;

import android.content.Context;

import com.weehoo.geenotes.GeeNotesApplication;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Storage implements IStorage {
    /**
     * Read a file from storage and retrieve its contents as a string.
     * @param fileName File name.
     * @return File contents as a string.
     */
    @Override
    public String getFileString(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            FileInputStream fileInputStream = GeeNotesApplication.getContext().openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);

            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    /**
     * Writes a string to the file.
     * The entire file's contents are overwritten.
     * Creates a new file if it does not exist.
     * @param fileName
     * @param fileString
     */
    @Override
    public void setFileString(String fileName, String fileString) {
        try {
            FileOutputStream fileOutputStream = GeeNotesApplication.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(fileString.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
