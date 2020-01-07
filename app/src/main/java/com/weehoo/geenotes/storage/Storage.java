package com.weehoo.geenotes.storage;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.weehoo.geenotes.GeeNotesApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

        try (FileInputStream fileInputStream = GeeNotesApplication.getContext().openFileInput(fileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)) {

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
     * @param fileName File name.
     * @param fileString String to write to file.
     */
    @Override
    public void setFileString(String fileName, String fileString) {
        try (FileOutputStream fileOutputStream = GeeNotesApplication.getContext().openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fileOutputStream.write(fileString.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read a file from storage and retrieve its contents as a bitmap.
     *
     * @param fileName File name.
     * @return File contents as a bitmap.
     */
    @Override
    public Bitmap getFileBitmap(String fileName) {
        Bitmap bitmap = null;

        try (FileInputStream fileInputStream = GeeNotesApplication.getContext().openFileInput(fileName)) {

            bitmap = BitmapFactory.decodeStream(fileInputStream);

            } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * Write a bitmap to the file.
     * The entire file's contents are overwritten.
     * Creates a new file if it does not exist.
     * @param fileName File name.
     * @param fileBitmap File contents as a bitmap.
     */
    @Override
    public void setFileBitmap(String fileName, Bitmap fileBitmap) {
        Thread thread = new Thread(new SetFileBitmapRunnable(fileName, fileBitmap));
        thread.start();
    }

    /**
     * Delete a file.
     * @param fileName Name of file to delete.
     */
    @Override
    public void deleteFile(String fileName) {
        GeeNotesApplication.getContext().deleteFile(fileName);
    }

    /**
     * Runnable class for concurrent bitmap storage.
     */
    private class SetFileBitmapRunnable implements Runnable {
        private final String mFileName;
        private final Bitmap mFileBitmap;

        public SetFileBitmapRunnable(String fileName, Bitmap fileBitmap) {
            mFileName = fileName;
            mFileBitmap = fileBitmap;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 FileOutputStream fileOutputStream = GeeNotesApplication.getContext().openFileOutput(mFileName, Context.MODE_PRIVATE)) {

                mFileBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                fileOutputStream.write(byteArrayOutputStream.toByteArray());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
