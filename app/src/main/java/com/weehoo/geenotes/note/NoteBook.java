package com.weehoo.geenotes.note;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class NoteBook {

    public String name;

    private String mID;
    private ArrayList<NotePage> mPages;

    private static final String NAME_KEY = "name";
    private static final String ID_KEY = "id";
    private static final String PAGES_KEY = "pages";

    /**
     * Construct a new NoteBook object with a single empty page.
     * Pages dimensions will be of the specified width and height.
     */
    public NoteBook() {
        this.name = "Notebook";

        mID = UUID.randomUUID().toString();
        mPages = new ArrayList<>();
        mPages.add(new NotePage());
    }

    /**
     * Constructs a NoteBook object using the specified ID.
     * @param id NoteBook ID.
     */
    private NoteBook(String id) {
        mID = id;
        mPages = new ArrayList<>();
        mPages.add(new NotePage());
    }

    /**
     * Gets the ID of this book.
     * @return Book ID.
     */
    public String getID() {
        return mID;
    }

    /**
     * Gets the number of pages in the book.
     * @return The number of pages in the book.
     */
    public int getPageCount() {
        return mPages.size();
    }

    /**
     * Adds a new page to the end of the book.
     * @return The new page.
     */
    private NotePage addPage() {
        NotePage page = new NotePage();
        mPages.add(page);

        return page;
    }

    /**
     * Adds a new page to the book at the specified index.
     * @param pageIndex 0-based index at which a new page is to be added.
     * @return The new page.
     */
    public NotePage addPage(int pageIndex) {
        NotePage notePage = new NotePage();
        mPages.add(pageIndex, notePage);

        return notePage;
    }

    /**
     * Adds a new page to the end of the book.
     * @return The new page.
     */
    private NotePage addPage(NotePage notePage) {
        mPages.add(notePage);

        return notePage;
    }

    /**
     * Gets a note page from this book.
     * @param pageIndex 0-based index of the page to get.
     * @return The page at the specified index, or a new page.
     */
    public NotePage getPage(int pageIndex) {
        if (pageIndex >= mPages.size()){
            return this.addPage();
        }

        return mPages.get(pageIndex);
    }

    /**
     * Remove the page at the specified index.
     * @param pageIndex 0-based index of the page to delete.
     */
    public void deletePage(int pageIndex) {
        mPages.remove(pageIndex);
    }

    /**
     * Converts the note book to a JSON object.
     * @return JSON object.
     */
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NoteBook.ID_KEY, mID);
            jsonObject.put(NoteBook.NAME_KEY, name);

            // Pages.
            JSONArray pagesJSON = new JSONArray();

            for(NotePage page : mPages) {
                pagesJSON.put(page.toJSONObject());
            }

            jsonObject.put(NoteBook.PAGES_KEY, pagesJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * Converts a JSON object to a note book.
     * @param jsonObject JSON object to convert.
     * @return Note book.
     */
    public static NoteBook fromJSONObject(JSONObject jsonObject) {
        NoteBook noteBook = null;

        try {
            noteBook = new NoteBook(jsonObject.getString(NoteBook.ID_KEY));
            noteBook.name = jsonObject.getString(NoteBook.NAME_KEY);

            JSONArray pagesJSON = jsonObject.getJSONArray(NoteBook.PAGES_KEY);

            for (int i = 0; i < pagesJSON.length(); i++) {
                NotePage notePage = new NotePage(pagesJSON.getString(i));
                noteBook.addPage(notePage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return noteBook;
    }
}
