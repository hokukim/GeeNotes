package com.weehoo.geenotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.weehoo.geenotes.dataContext.NoteBookDataContext;
import com.weehoo.geenotes.note.NoteBook;
import com.weehoo.geenotes.storage.IStorage;
import com.weehoo.geenotes.storage.Storage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<NoteBook> mNoteBooks;
    private IStorage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        mStorage = new Storage();
        mNoteBooks = NoteBookDataContext.getNoteBooksOrdered(mStorage);
    }

    /**
     * + Note button click event handler.
     * Starts Note activity for creating a new note.
     * @param view The view for which the event is being handled.
     */
    public void newNoteActivity(View view) {
        // Create a new notebook.
        NoteBook noteBook = new NoteBook();

        // Save notebook to storage.
        mNoteBooks.add(noteBook);

        // Start note activity.
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(NoteActivity.NOTEBOOK_ID_EXTRA_KEY, noteBook.getID());
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
