package com.weehoo.geenotes.note;

import android.graphics.Bitmap;
import java.util.ArrayList;

public class NoteBook {

    public String name;

    private String mID;
    private ArrayList<NotePage> mPages;

    /**
     * Construct a new NoteBook object with a single empty page.
     * Pages dimensions will be of the specified width and height.
     */
    public NoteBook() {
        this.name = "Notebook";

        mID = "id";
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
    public NotePage addNewPage() {
        NotePage page = new NotePage();
        mPages.add(page);

        return page;
    }

    /**
     * Adds a new page to the book at the specified index.
     * @param pageIndex 0-base index at which a new page is to be added.
     * @return The new page.
     */
    public NotePage addNewPage(int pageIndex) {
        NotePage page = new NotePage();
        mPages.add(pageIndex, page);

        return page;
    }

    /**
     * Gets a note page from this book.
     * @param pageIndex 0-based index of the page to get.
     * @return The page at the specified index, or a new page.
     */
    public NotePage getPage(int pageIndex) {
        if (mPages.size() > pageIndex) {
            return this.addNewPage();
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
}
