package com.weehoo.geenotes.dataContext;

import com.weehoo.geenotes.note.NoteBook;
import com.weehoo.geenotes.storage.IStorage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NoteBookDataContext {

    private static final String MANIFEST_FILE_NAME = "GeeNotesManifest.json";
    private static final String NOTEBOOKS_KEY = "notebooks";

    /**
     * Writes the notebooks list to storage.
     * Overwrites existing manifest.
     * @param storage Storage implementation.
     * @param noteBooks Notebooks.
     */
    public static void setNoteBooks(IStorage storage, ArrayList<NoteBook> noteBooks) {
        try {
            // Convert notebooks list to JSON.
            JSONArray noteBooksJSON = new JSONArray();

            for (NoteBook noteBook : noteBooks) {
                noteBooksJSON.put(noteBook.toJSONObject());
            }

            // Write to manifest.
            JSONObject manifestJSON = new JSONObject();
            manifestJSON.put(NOTEBOOKS_KEY, noteBooksJSON);

            storage.setFileString(MANIFEST_FILE_NAME, manifestJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get notebooks from storage, ordered according to the order in which they were added.
     * @param storage Storage implementation.
     * @return List of notebooks.
     */
    public static ArrayList<NoteBook> getNoteBooks(IStorage storage) {
        ArrayList<NoteBook> allNoteBooks = new ArrayList<>();

        // Read entire manifest file.
        String data = storage.getFileString(MANIFEST_FILE_NAME);

        // Read file data as JSON.
        try {
            JSONObject manifestJSON = new JSONObject(data);
            JSONArray noteBooksJSON = manifestJSON.getJSONArray(NOTEBOOKS_KEY);

            // Read all notebook metadata.
            for (int i = 0; i < noteBooksJSON.length(); i++) {
                NoteBook noteBook = NoteBook.fromJSONObject(noteBooksJSON.getJSONObject(i));
                allNoteBooks.add(noteBook);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allNoteBooks;
    }
}
