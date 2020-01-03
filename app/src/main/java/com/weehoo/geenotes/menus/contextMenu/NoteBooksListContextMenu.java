package com.weehoo.geenotes.menus.contextMenu;

import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

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
    public boolean onContextItemSelected(final ListView noteBooksListView, final ArrayList<NoteBook> noteBooks, final IStorage storage, MenuItem item) {
        // Get index of notebook list item.
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int noteBookIndex = info.position;

        // Handle context menu item selected.
        int itemId = item.getItemId();

        if (itemId == R.id.notebook_delete) {
            getDeleteNotebookConfirmationDialog(noteBooksListView, noteBooks, noteBookIndex, storage).show();
        } else if (itemId == R.id.notebook_rename) {
            // Set edit view to rename a notebook.
            NoteBookAdapter noteBookAdapter = (NoteBookAdapter) noteBooksListView.getAdapter();
            noteBookAdapter.setItemStatus(noteBookIndex, NoteBookAdapter.ItemStatus.EDIT);
            noteBookAdapter.notifyDataSetChanged();

            final EditText editText = info.targetView.findViewById(R.id.notebook_rename);

            // Delay setting focus and showing IME.
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (editText.requestFocus()) {
                        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }, 100);

            editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                /**
                 * Called when an action is being performed.
                 *
                 * @param v        The view that was clicked.
                 * @param actionId Identifier of the action.  This will be either the
                 *                 identifier you supplied, or {@link EditorInfo#IME_NULL
                 *                 EditorInfo.IME_NULL} if being called due to the enter key
                 *                 being pressed.
                 * @param event    If triggered by an enter key, this is the event;
                 *                 otherwise, this is null.
                 * @return Return true if you have consumed the action, else false.
                 */
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(noteBooksListView.getWindowToken(), 0);

                        // Set the notebook name.
                        NoteBook noteBook = noteBooks.get(noteBookIndex);
                        noteBook.name = v.getText().toString();
                        noteBooks.set(noteBookIndex, noteBook);

                        // Save updated notebook to storage.
                        NoteBookDataContext.setNoteBooks(storage, noteBooks);

                        // Refresh notebooks list view.
                        noteBooksListView.setAdapter(new NoteBookAdapter(mContext, new ArrayList<>(noteBooks)));
                        return true;
                    }

                    return false;
                }
            });
        }

        return true;
    }

    /**
     * Constructs an AlertAction dialog to confirm or cancel notebook deletion.
     * @param noteBooksListView NoteBooks list view.
     * @param noteBooks All notebooks.
     * @param noteBookIndex Index of notebook to delete.
     * @param storage Storage implementation from which to delete the notebook and its pages.
     * @return AlertDialog object.
     */
    private AlertDialog getDeleteNotebookConfirmationDialog(final ListView noteBooksListView, final ArrayList<NoteBook> noteBooks, final int noteBookIndex, final IStorage storage) {
        final NoteBook noteBook = noteBooks.get(noteBookIndex);

        // Construct alert dialog with positive and negative button click listeners.
        AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.notebook_delete_confirm_title)
                .setMessage(String.format(mContext.getResources().getString(R.string.notebook_delete_confirm_message_spec), noteBook.name))
                .setIcon(R.drawable.ic_selector_menu_delete)
                .setPositiveButton(R.string.notebook_delete_confirm_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete notebook pages.

                        for (int i = 0; i < noteBook.getPageCount(); i++) {
                            NotePageDataContext.deleteNotePage(storage, noteBook.getPage(i));
                        }

                        // Delete notebook.
                        noteBooks.remove(noteBookIndex);
                        NoteBookDataContext.setNoteBooks(storage, noteBooks);

                        // Reload notebooks list view.
                        noteBooksListView.setAdapter(new NoteBookAdapter(mContext, new ArrayList<>(noteBooks)));
                    }
                })
                .setNegativeButton(R.string.notebook_delete_confirm_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        return alertDialog;
    }
}
