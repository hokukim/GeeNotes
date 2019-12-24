package com.weehoo.geenotes.dataContext;

import android.provider.ContactsContract;

import com.weehoo.geenotes.GeeNotesApplication;
import com.weehoo.geenotes.note.NoteBook;
import com.weehoo.geenotes.note.NotePage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NoteBookDataContext {

    private final String MANIFEST_FILE_NAME = "GeeNotesManifest.json";
    private final String MANIFEST_KEY_NOTEBOOKS = "notebooks";
    private final String MANIFEST_KEY_NOTEBOOK_ID = "id";
    private final String MANIFEST_KEY_NOTEBOOK_NAME = "name";
    private final String MANIFEST_KEY_NOTEBOOK_PAGES = "pages";
    private final String MANIFEST_KEY_NOTEBOOK_PAGE_ID = "id";

    private static NoteBookDataContext mInstance = null;

    private NoteBookDataContext() {
    }

    public static NoteBookDataContext getInstance() {
        if (mInstance == null) {
            mInstance = new NoteBookDataContext();
        }

        return mInstance;
    }

    public void addNoteBook(NoteBook noteBook) {

    }

    public ArrayList<NoteBook> getAllNoteBooks() {
        ArrayList<NoteBook> allNoteBooks = new ArrayList<>();

        // Read entire manifest file.
        StringBuilder stringBuilder = new StringBuilder();

        try {
            FileInputStream fileInputStream = GeeNotesApplication.getContext().openFileInput(MANIFEST_FILE_NAME);
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

        // Read file data as JSON.
        try {
            JSONObject manifestJSON = new JSONObject(stringBuilder.toString());
            JSONArray noteBooksJSON = manifestJSON.getJSONArray(MANIFEST_KEY_NOTEBOOKS);

            // Read all notebook metadata.
            for (int i = 0; i < noteBooksJSON.length(); i++) {
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

                allNoteBooks.add(noteBook);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allNoteBooks;
    }

    public void updateNoteBook(NoteBook noteBook) {

    }

    public void deleteNoteBook(NoteBook noteBook) {

    }
}
