package com.weehoo.geenotes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.weehoo.geenotes.adapters.NoteBookAdapter;
import com.weehoo.geenotes.dataContext.NoteBookDataContext;
import com.weehoo.geenotes.dataContext.NotePageDataContext;
import com.weehoo.geenotes.menus.contextMenu.NoteBooksListContextMenu;
import com.weehoo.geenotes.note.NoteBook;
import com.weehoo.geenotes.storage.IStorage;
import com.weehoo.geenotes.storage.Storage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mNoteBooksListView;
    private NoteBooksListContextMenu mNoteBooksListContextMenu;
    private ArrayList<NoteBook> mNoteBooks;
    private IStorage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Display up/back button in place of home button.

        // Get notebooks.
        mStorage = new Storage();
        mNoteBooks = NoteBookDataContext.getNoteBooks(mStorage);

        // Display notebooks in list view.
        mNoteBooksListView = findViewById(R.id.notebooks_list_view);
        mNoteBooksListView.setAdapter(new NoteBookAdapter(this, new ArrayList<>(mNoteBooks)));

        // Set notebook item event listeners.
        mNoteBooksListView.setOnItemClickListener(new NoteBookItemClickListener(this));

        // Register notebook list view for context menu.
        registerForContextMenu(mNoteBooksListView);
        mNoteBooksListContextMenu = new NoteBooksListContextMenu(this, getMenuInflater());
    }

    /**
     * + Note button click event handler.
     * Starts Note activity for creating a new note.
     * @param view The view for which the event is being handled.
     */
    public void newNoteActivity(View view) {
        // Start note activity for a new notebook.
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            finish();
        }
        else if (itemId == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a context menu for the {@code view} is about to be shown.
     * Unlike {@link #onCreateOptionsMenu(Menu)}, this will be called every
     * time the context menu is about to be shown and should be populated for
     * the view (or item inside the view for {@link AdapterView} subclasses,
     * this can be found in the {@code menuInfo})).
     * <p>
     * Use {@link #onContextItemSelected(MenuItem)} to know when an
     * item has been selected.
     * <p>
     * It is not safe to hold onto the context menu after this method returns.
     *
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        mNoteBooksListContextMenu.onCreateContextMenu(menu, v, menuInfo);
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
     * @param item The context menu item that was selected.
     * @return boolean Return false to allow normal context menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        mNoteBooksListContextMenu.onContextItemSelected(mNoteBooks, mNoteBooksListView, mStorage, item);

        return super.onContextItemSelected(item);
    }

    /**
     * Notebook item click listener.
     */
    private class NoteBookItemClickListener implements AdapterView.OnItemClickListener {
        Context mContext = null;

        public NoteBookItemClickListener(Context context)
        {
            mContext = context;
        }

        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent   The AdapterView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView noteBookIDView = view.findViewById(R.id.notebook_id);
            String noteBookId = (String) noteBookIDView.getText();

            // Start note activity for an existing notebook.
            Intent intent = new Intent(mContext, NoteActivity.class);
            intent.putExtra(NoteActivity.NOTEBOOK_ID_EXTRA_KEY, noteBookId);
            startActivity(intent);
        }
    }
}
