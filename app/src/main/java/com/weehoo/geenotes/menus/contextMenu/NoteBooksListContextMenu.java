package com.weehoo.geenotes.menus.contextMenu;

import android.content.Context;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.weehoo.geenotes.R;
import com.weehoo.geenotes.adapters.NoteBookAdapter;
import com.weehoo.geenotes.dataContext.NoteBookDataContext;
import com.weehoo.geenotes.dataContext.NotePageDataContext;
import com.weehoo.geenotes.note.NoteBook;
import com.weehoo.geenotes.storage.IStorage;

import java.util.ArrayList;

/**
 * Handles NoteBooks list context menu events.
 */
public class NoteBooksListContextMenu {

    private Context mContext;
    private MenuInflater mMenuInflater;

    public NoteBooksListContextMenu(Context context, MenuInflater menuInflater) {
        mContext = context;
        mMenuInflater = menuInflater;
    }

    /**
     * Called when a context menu for the {@code view} is about to be shown.
     * Unlike onCreateOptionsMenu(Menu), this will be called every
     * time the context menu is about to be shown and should be populated for
     * the view (or item inside the view for {@link AdapterView} subclasses,
     * this can be found in the {@code menuInfo})).
     * <p>
     * Use onContextItemSelected(MenuItem) to know when an
     * item has been selected.
     * <p>
     * It is not safe to hold onto the context menu after this method returns.
     *
     * @param menu Menu to be shown.
     * @param v View in which to show menu.
     * @param menuInfo Menu info.
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        mMenuInflater.inflate(R.menu.context_notebooks_list_main, menu);
    }

    /**
     * This hook is called whenever an item in a context menu is selected. The
     * default implementation simply returns false to have the normal processing
     * happen (calling the item's Runnable or sending a message to its Handler
     * as appropriate). You can use this method for any items for which you
     * would like to do processing without those other facilities.
     * <p>
     * Use {@link MenuItem#getMenuInfo()} to get extra information set by the
     * View that added this menu item.
     * <p>
     * Derived classes should call through to the base class for it to perform
     * the default menu handling.
     *
     * @param noteBooks NoteBooks list.
     * @param noteBooksListView NoteBooks list view.
     * @param storage Storage implementation.
     * @param item The context menu item that was selected.
     * @return boolean Return false to allow normal context menu processing to
     * proceed, true to consume it here.
     */
    public boolean onContextItemSelected(ArrayList<NoteBook> noteBooks, ListView noteBooksListView, IStorage storage, MenuItem item) {
        // Get index of notebook list item.
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int noteBookIndex = info.position;

        // Handle context menu item selected.
        int itemId = item.getItemId();

        if (itemId == R.id.notebook_delete) {
            // Delete notebook pages.
            NoteBook noteBook = noteBooks.get(noteBookIndex);

            for (int i = 0; i < noteBook.getPageCount(); i++) {
                NotePageDataContext.deleteNotePage(storage, noteBook.getPage(i));
            }

            // Delete notebook.
            noteBooks.remove(noteBookIndex);
            NoteBookDataContext.setNoteBooks(storage, noteBooks);

            // Reload notebooks list view.
            noteBooksListView.setAdapter(new NoteBookAdapter(mContext, new ArrayList<>(noteBooks)));
        }
        else if (itemId == R.id.notebook_rename) {
            // Set edit view to rename a notebook.
            NoteBookAdapter noteBookAdapter = (NoteBookAdapter) noteBooksListView.getAdapter();
            noteBookAdapter.setItemStatus(noteBookIndex, NoteBookAdapter.ItemStatus.EDIT);
            noteBookAdapter.notifyDataSetChanged();

            final EditText editText = noteBooksListView.findViewById(R.id.notebook_rename);

            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean b = editText.requestFocus();

                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                }
            }, 100);
        }

        return true;
    }
}
