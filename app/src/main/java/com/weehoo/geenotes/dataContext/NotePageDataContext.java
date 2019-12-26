package com.weehoo.geenotes.dataContext;

import android.graphics.Bitmap;

import com.weehoo.geenotes.note.NotePage;
import com.weehoo.geenotes.storage.IStorage;

public class NotePageDataContext {

     /**
     * Writes the note page data to storage.
     * Overwrites existing page data.
     * @param storage Storage implementation.
     * @param notePage Note page.
      * @param noteData Note data.
     * */
    public static void setNotePage(IStorage storage, NotePage notePage, Bitmap noteData) {
        storage.setFileBitmap(notePage.getID(), noteData);
    }

    /**
     * Gets note page data from storage.
     * @param storage Storage implementation.
     * @param notePage Note page.
     * @return Note page data as a bitmap.
     */
    public static Bitmap getNotePage(IStorage storage, NotePage notePage) {
        return storage.getFileBitmap(notePage.getID());
    }
}
