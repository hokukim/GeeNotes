package com.weehoo.geenotes;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.weehoo.geenotes.canvas.CanvasView;
import com.weehoo.geenotes.dataContext.NoteBookDataContext;
import com.weehoo.geenotes.dataContext.NotePageDataContext;
import com.weehoo.geenotes.note.NoteBook;
import com.weehoo.geenotes.storage.IStorage;
import com.weehoo.geenotes.storage.Storage;
import com.weehoo.geenotes.tool.EraserTool;
import com.weehoo.geenotes.tool.ITool;
import com.weehoo.geenotes.tool.PenTool;
import com.weehoo.geenotes.tool.SelectionTool;

import org.w3c.dom.Text;

import java.util.ArrayList;

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
    private int mNoteBookIndex;
    private int mNotePageIndex;
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
        mToolMenuItem = null;

        // Load notebooks after canvas view has been created and sized.
        mCanvasView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCanvasView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mStorage = new Storage();
                loadNoteBook();
            }
        });
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
               // case MotionEvent.TOOL_TYPE_FINGER:
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
        getMenuInflater().inflate(R.menu.menu_note, menu);

        // Add tool menu items and group divider.
        for (int i = 0; i < mTools.size(); i++) {
            ITool tool = mTools.get(i);
            int iconRes = i == 0 ? tool.getIconResActive() : tool.getIconResInactive();

            MenuItem item = menu.add(R.id.note_menu_group_tools, i, MENU_TOOLS_GROUP_ORDER, "");
            item.setIcon(iconRes).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            if (mToolMenuItem == null) {
                mToolMenuItem = item;
            }
        }

        menu.add(R.id.note_menu_group_tools, 0, MENU_TOOLS_GROUP_ORDER + 1, "|")
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
            // Save current page.
            saveNoteBookData();

            // Go back.
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
        else if (groupId == R.id.note_menu_group_page) {
            if (itemId == R.id.note_menu_add_page) {
                // Save current page.
                saveNoteBookData();

                // Load new page.
                mNotePageIndex++;
                mNoteBooks.get(mNoteBookIndex).addPage(mNotePageIndex);
                mCanvasView.clearPrimary();
                saveNoteBookData();
                loadCanvasViewNotePage();
                updateStatusBar();
            }
            else if (itemId == R.id.note_menu_previous_page) {
                // Save current page.
                saveNoteBookData();
                mCanvasView.clearPrimary();

                // Retreat to previous page index.
                mNotePageIndex--;

                if (mNotePageIndex < 0) {
                    // Insert (at front) and save a new page.
                    mNotePageIndex = 0;
                    mNoteBooks.get(mNoteBookIndex).addPage(mNotePageIndex);
                    saveNoteBookData();
                }

                // Load the previous page.
                loadCanvasViewNotePage();
                updateStatusBar();
            }
            else if (itemId == R.id.note_menu_next_page) {
                // Save current page.
                saveNoteBookData();
                mCanvasView.clearPrimary();

                // Advance to next page index.
                mNotePageIndex++;

                if (mNotePageIndex >= mNoteBooks.get(mNoteBookIndex).getPageCount()) {
                    // Insert (at end) and save a new page.
                    mNoteBooks.get(mNoteBookIndex).addPage();
                    saveNoteBookData();
                }

                // Load the next page.
                loadCanvasViewNotePage();
                updateStatusBar();
            }
            else if (itemId == R.id.note_menu_delete_page) {
                NoteBook noteBook = mNoteBooks.get(mNoteBookIndex);
                getDeleteNotePageConfirmationDialog().show();
            }
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
        mNoteBookIndex = 0;
        mNotePageIndex = 0;
        mNoteBooks = NoteBookDataContext.getNoteBooks(mStorage);
        String id = getIntent().getStringExtra(NoteActivity.NOTEBOOK_ID_EXTRA_KEY);

        if (id == null || id.isEmpty()) {
            // Load a new notebook.
            NoteBook noteBook = new NoteBook();
            noteBook.addPage();
            mNoteBooks.add(noteBook);
            mNoteBookIndex = mNoteBooks.size() - 1;
            saveNoteBookData();
        }
        else {
            // Load an existing notebook.
            for (int i = 0; i < mNoteBooks.size(); i++) {
                NoteBook noteBook = mNoteBooks.get(i);

                if (noteBook.getID().equalsIgnoreCase(id)) {
                    mNoteBookIndex = i;

                    // Load first page.
                    break;
                }
            }
        }

        updateStatusBar();
        loadCanvasViewNotePage();
    }

    /**
     * Saves current notebook and page to storage.
     */
    private void saveNoteBookData() {
        // Save updated notebooks and page to storage.
        NoteBookDataContext.setNoteBooks(mStorage, mNoteBooks);
        NotePageDataContext.setNotePage(mStorage, mNoteBooks.get(mNoteBookIndex).getPage(mNotePageIndex), mCanvasView.copyPrimaryBitmap());
    }

    /**
     * Loads the current page to the primary canvas view.
     */
    private void loadCanvasViewNotePage() {
        NoteBook noteBook = mNoteBooks.get(mNoteBookIndex);
        Bitmap pageBitmap = NotePageDataContext.getNotePage(mStorage, noteBook.getPage(mNotePageIndex));

        // Load new note page.
        mCanvasView.primaryCanvas.drawBitmap(pageBitmap, 0, 0, mCanvasView.primaryPaint);
    }

    private void updateStatusBar() {
        // NoteBook name.
        TextView noteBookNameView = (TextView) findViewById(R.id.status_notebook_name);
        noteBookNameView.setText(mNoteBooks.get(mNoteBookIndex).name);

        // Page description.
        StringBuilder pageDescriptionStringBuilder = new StringBuilder("Page ");
        pageDescriptionStringBuilder.append(mNotePageIndex + 1);
        pageDescriptionStringBuilder.append(" of ");
        pageDescriptionStringBuilder.append(mNoteBooks.get(mNoteBookIndex).getPageCount());

        TextView pageDescriptionView = findViewById(R.id.status_notepage_description);
        pageDescriptionView.setText(pageDescriptionStringBuilder.toString());
    }

    /**
     * Constructs an AlertAction dialog to confirm or cancel notebook deletion.
     * @return AlertDialog object.
     */
    private AlertDialog getDeleteNotePageConfirmationDialog() {
        final NoteBook noteBook = mNoteBooks.get(mNoteBookIndex);

        // Construct alert dialog with positive and negative button click listeners.
        return new AlertDialog.Builder(this)
                .setTitle(R.string.notepage_delete_confirm_title)
                .setMessage(R.string.notepage_delete_confirm_message)
                .setIcon(R.drawable.ic_selector_menu_delete)
                .setPositiveButton(R.string.notepage_delete_confirm_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete the current page.
                        NotePageDataContext.deleteNotePage(mStorage, noteBook.getPage(mNotePageIndex));
                        noteBook.deletePage(mNotePageIndex);

                        if (noteBook.getPageCount() == 0) {
                            // Add a new page.
                            mNotePageIndex = 0;
                            noteBook.addPage();
                            mCanvasView.clearPrimary();
                            saveNoteBookData();
                        }
                        else {
                            mCanvasView.clearPrimary();

                            if (mNotePageIndex >= noteBook.getPageCount()) {
                                // End page was deleted.
                                mNotePageIndex = noteBook.getPageCount() - 1;
                            }
                        }

                        // Update page.
                        loadCanvasViewNotePage();
                        updateStatusBar();

                        // Save notebook.
                        saveNoteBookData();
                    }
                })
                .setNegativeButton(R.string.notepage_delete_confirm_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
