package com.weehoo.geenotes.dataContext;

import com.weehoo.geenotes.note.NoteBook;
import com.weehoo.geenotes.storage.IStorage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoteBookDataContext {

    private static final String MANIFEST_FILE_NAME = "GeeNotesManifest.json";
    private static final String MANIFEST_KEY_NOTEBOOKS = "notebooks";
    private static final String MANIFEST_KEY_NOTEBOOK_ID = "id";
    private static final String MANIFEST_KEY_NOTEBOOK_NAME = "name";
    private static final String MANIFEST_KEY_NOTEBOOK_PAGES = "pages";
    private static final String MANIFEST_KEY_NOTEBOOK_PAGE_ID = "id";

    /**
     * Writes the notebooks list to storage.
     * Overwrites existing manifest.
     * @param storage Storage implementation.
     * @param noteBooks Notebooks.
     */
    public static void setNoteBooks(IStorage storage, ArrayList<NoteBook> noteBooks) {

        try {
            // Convert notebooks list to JSON.
            JSONObject noteBooksJSON = new JSONObject();
            noteBooksJSON.put(MANIFEST_KEY_NOTEBOOKS, noteBooks);

            // Write to manifest.
            storage.setFileString(MANIFEST_FILE_NAME, noteBooksJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get notebooks from storage.
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
            JSONArray noteBooksJSON = manifestJSON.getJSONArray(MANIFEST_KEY_NOTEBOOKS);

            // Read all notebook metadata.
            for (int i = 0; i < noteBooksJSON.length(); i++) {
                NoteBook noteBook = (NoteBook) noteBooksJSON.get(i);

                /*
                JSONObject noteBookJSON = noteBooksJSON.getJSONObject(i);
                NoteBook noteBook = new NoteBook(noteBookJSON.getString(MANIFEST_KEY_NOTEBOOK_ID));
                noteBook.name = noteBookJSON.getString(MANIFEST_KEY_NOTEBOOK_NAME);

                // Read all page metadata.
                JSONArray pagesJSON = noteBookJSON.getJSONArray(MANIFEST_KEY_NOTEBOOK_PAGES);

                for (int j = 0; j < pagesJSON.length(); j++) {
                    JSONObject notePageJSON = pagesJSON.getJSONObject(j);
                    NotePage notePage = new NotePage(notePageJSON.getString(MANIFEST_KEY_NOTEBOOK_PAGE_ID));

                    // Add page to notebook.
                    noteBook.addPage(notePage);
                }

                 */

                allNoteBooks.add(noteBook);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allNoteBooks;
    }
}
