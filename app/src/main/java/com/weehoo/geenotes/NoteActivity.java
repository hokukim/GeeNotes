package com.weehoo.geenotes;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import com.weehoo.geenotes.canvas.CanvasView;
import com.weehoo.geenotes.dataContext.NoteBookDataContext;
import com.weehoo.geenotes.note.NoteBook;
import com.weehoo.geenotes.storage.IStorage;
import com.weehoo.geenotes.storage.Storage;
import com.weehoo.geenotes.tool.EraserTool;
import com.weehoo.geenotes.tool.ITool;
import com.weehoo.geenotes.tool.PenTool;
import com.weehoo.geenotes.tool.SelectionTool;

import java.util.ArrayList;
import java.util.HashMap;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTEBOOK_ID_EXTRA_KEY = "notebook_id_extra";
    private final int MENU_TOOLS_GROUP_ORDER = 0;
    private final int MENU_PAGE_GROUP_ORDER = 100;

    private CanvasView mCanvasView;
    private ArrayList<ITool> mTools;
    private SparseArray<ITool> mToolsMap;
    private ITool mTool;
    private MenuItem mToolMenuItem;

    private  ArrayList<NoteBook> mNoteBooks;
    private NoteBook mNoteBook;
    private IStorage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mCanvasView = findViewById(R.id.canvas_view);

        Toolbar toolbar = findViewById(R.id.toolbar_note);
        setSupportActionBar(toolbar);

        // Action bar settings.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // Do not show app title.
        actionBar.setDisplayHomeAsUpEnabled(true); // Display up/back button in place of home button.

        // Register tools.
        mTools = new ArrayList<>();
        mToolsMap = new SparseArray<>();
        this.registerTools();

        // Set default tool.
        mTool = mTools.get(0);
        mTool.onSelect(mCanvasView);

        // Load notebooks.
        mStorage = new Storage();
        this.loadNoteBook();
    }

    /**
     * Called when a touch screen event was not handled by any of the views
     * under it.  This is most useful to process touch events that happen
     * outside of your window bounds, where there is no view to receive it.
     *
     * @param event The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     * The default implementation always returns false.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Adjust event input by canvas input offsets.
        PointF inputOffsets = mCanvasView.getInputOffsets();
        event.offsetLocation(inputOffsets.x, -inputOffsets.y);

        boolean drawingChanged = false;

        for (int i = 0; i < event.getPointerCount(); i++) {
            switch (event.getToolType(i)) {
                case MotionEvent.TOOL_TYPE_FINGER:
                case MotionEvent.TOOL_TYPE_STYLUS: {
                    // Send input event to input object.
                    drawingChanged = mTool.onTouchEvent(event);
                } break;
            }
        }

        if (drawingChanged) {
            // Drawing has changed.
            // Invalidate view so it can be redrawn.
            mCanvasView.invalidate();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_main, menu);

        // Add tool menu items and group divider.
        for (int i = 0; i < mTools.size(); i++) {
            ITool tool = mTools.get(i);
            int iconRes = i == 0 ? tool.getIconResActive() : tool.getIconResInactive();
            menu.add(R.id.note_menu_group_tools, i, MENU_TOOLS_GROUP_ORDER, "")
                .setIcon(iconRes)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        menu.add(R.id.note_menu_group_tools, 0, MENU_TOOLS_GROUP_ORDER + 1, "|")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        mToolMenuItem = menu.getItem(0);

        // Add page menu items and group divider.
        menu.add(R.id.note_menu_group_tools, 0, MENU_PAGE_GROUP_ORDER + 1, "|")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int groupId = item.getGroupId();
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }
        else if (groupId == R.id.note_menu_group_tools && item.getOrder() == MENU_TOOLS_GROUP_ORDER) {
            // Deselect previous tool.
            mToolMenuItem.setIcon(mTool.getIconResInactive());
            mTool.onDeselect();

            // Select new tool.
            mTool = mToolsMap.get(itemId);
            mToolMenuItem = item;
            mToolMenuItem.setIcon(mTool.getIconResActive());
            mTool.onSelect(mCanvasView);
        }

        return true;
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link Activity#onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     */
    @Override
    public void onBackPressed() {
        // Ignore default back button press.
        // This activity has a back button in the action menu bar instead.
    }

    /**
     * Register tools.
     */
    private void registerTools() {
        // *** Add tool here. ***
        mTools.add(new PenTool());
        mTools.add(new EraserTool());
        mTools.add(new SelectionTool());
        // **********************

        for (int i = 0; i < mTools.size(); i++) {
            mToolsMap.put(i, mTools.get(i));
        }
    }

    private void loadNoteBook() {
        mNoteBooks = NoteBookDataContext.getNoteBooks(mStorage);
        String id = getIntent().getStringExtra(NoteActivity.NOTEBOOK_ID_EXTRA_KEY);

        if (id == null || id.isEmpty()) {
            // Load a new notebook.
            mNoteBook = new NoteBook();
            mNoteBook.addPage();
            mNoteBooks.add(mNoteBook);

            // Save new notebook to storage.
            NoteBookDataContext.setNoteBooks(mStorage, mNoteBooks);
        }
        else {
            // Load an existing notebook.
            for (NoteBook noteBook : mNoteBooks) {
                if (noteBook.getID().equalsIgnoreCase(id)) {
                    mNoteBook = noteBook;
                    break;
                }
            }
        }

        // Load pages.
    }
}
