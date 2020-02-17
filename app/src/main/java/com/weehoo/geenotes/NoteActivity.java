package com.weehoo.geenotes;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.weehoo.geenotes.background.EmptyBackground;
import com.weehoo.geenotes.background.GridBackground;
import com.weehoo.geenotes.background.IBackground;
import com.weehoo.geenotes.canvas.CanvasView;
import com.weehoo.geenotes.dataContext.NoteBookDataContext;
import com.weehoo.geenotes.dataContext.NotePageDataContext;
import com.weehoo.geenotes.menus.subMenus.NotePageBackgroundsSubMenu;
import com.weehoo.geenotes.note.NoteBook;
import com.weehoo.geenotes.note.NotePage;
import com.weehoo.geenotes.social.Sharing;
import com.weehoo.geenotes.storage.IStorage;
import com.weehoo.geenotes.storage.Storage;
import com.weehoo.geenotes.timers.RateLimiter;
import com.weehoo.geenotes.tool.EraserTool;
import com.weehoo.geenotes.tool.ITool;
import com.weehoo.geenotes.tool.PenTool;
import com.weehoo.geenotes.tool.SelectionTool;

import java.io.File;
import java.util.ArrayList;

public class NoteActivity extends AppCompatActivity {

    public static final String NOTEBOOK_ID_EXTRA_KEY = "notebook_id_extra";

    private CanvasView mCanvasView;

    // Tools.
    private ArrayList<ITool> mTools;
    private SparseArray<ITool> mToolsMap;
    private ITool mTool;
    private MenuItem mToolMenuItem;

    // Backgrounds.
    private NotePageBackgroundsSubMenu mNotePageBackgroundsSubMenu;
    private IBackground mBackground;

    private  ArrayList<NoteBook> mNoteBooks;
    private int mNoteBookIndex;
    private int mNotePageIndex;

    private IStorage mStorage;
    private RateLimiter mNotePageDataSaver;

    public NoteActivity() {
        mNotePageDataSaver = new RateLimiter(new Runnable() {
            @Override
            public void run() {
                saveNotePageData(true);
            }
        });
    }

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

        // Register backgrounds.
        this.registerBackgrounds();

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

            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Auto save page data.
                mNotePageDataSaver.run();
            }
        }

        return true;
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     *
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);

        // Add tool menu items and group divider.
        int menuToolsGroupOrder = getResources().getInteger(R.integer.note_menu_tools_group_order_min);

        for (int i = 0; i < mTools.size(); i++) {
            ITool tool = mTools.get(i);
            int iconRes = i == 0 ? tool.getIconResActive() : tool.getIconResInactive();

            MenuItem item = menu.add(R.id.note_menu_group_tools, i, menuToolsGroupOrder, "");
            item.setIcon(iconRes).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            if (mToolMenuItem == null) {
                mToolMenuItem = item;
            }
        }

        menu.add(R.id.note_menu_group_tools, 0, menuToolsGroupOrder + 1, "|")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        // Add backgrounds submenu items.
        Menu backgroundsSubMenu = menu.findItem(R.id.note_menu_background).getSubMenu();
        mNotePageBackgroundsSubMenu.onCreateOptionsMenu(backgroundsSubMenu, R.id.note_menu_group_backgrounds, getResources().getInteger(R.integer.note_menu_backgrounds_submenu_group_order_min));

        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int menuToolsGroupOrder = getResources().getInteger(R.integer.note_menu_tools_group_order_min);
        int menuBackgroundsGroupOrder = getResources().getInteger(R.integer.note_menu_backgrounds_submenu_group_order_min);
        int groupId = item.getGroupId();
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            // Go back.
            return super.onOptionsItemSelected(item);
        }
        else if (groupId == R.id.note_menu_group_tools && item.getOrder() == menuToolsGroupOrder) {
            // Deselect previous tool.
            mToolMenuItem.setIcon(mTool.getIconResInactive());
            mTool.onDeselect();

            // Select new tool.
            mTool = mToolsMap.get(itemId);
            mToolMenuItem = item;
            mToolMenuItem.setIcon(mTool.getIconResActive());
            mTool.onSelect(mCanvasView);
        }
        else if (groupId == R.id.note_menu_group_backgrounds && item.getOrder() == menuBackgroundsGroupOrder) {
            // Clear the previous background.
            mCanvasView.clearBackground();

            // Select the new background.
            mBackground = mNotePageBackgroundsSubMenu.onOptionsItemSelected(item);
            mBackground.onSelect(mCanvasView);
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
            } else if (itemId == R.id.note_menu_previous_page) {
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
            } else if (itemId == R.id.note_menu_next_page) {
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
            } else if (itemId == R.id.note_menu_delete_page) {
                NoteBook noteBook = mNoteBooks.get(mNoteBookIndex);
                getDeleteNotePageConfirmationDialog().show();
            }
            else if (itemId == R.id.note_menu_share_page) {
                // Get note page file URI.
                NotePage notePage = mNoteBooks.get(mNoteBookIndex).getPage(mNotePageIndex);
                File file = NotePageDataContext.getNotePageFile(mStorage, notePage);
                Uri fileUri = FileProvider.getUriForFile(this, getPackageName(), file);

                Sharing.sendFile(this, fileUri, getResources().getString(R.string.action_send_page));
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

    private void registerBackgrounds() {
        ArrayList<IBackground> backgrounds = new ArrayList<>();

        // *** Add background here. ***
        backgrounds.add(new GridBackground());
        backgrounds.add(new EmptyBackground());
        // ****************************

        // Create backgrounds submenu.
        mNotePageBackgroundsSubMenu = new NotePageBackgroundsSubMenu(backgrounds);

        // Set default background.
        mBackground = mNotePageBackgroundsSubMenu.getBackground();
    }

    /**
     * Loads the current notebook and page.
     */
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
     * Saves current notebook and page and data.
     */
    private void saveNoteBookData() {
        // Save updated notebooks and page data.
        NoteBookDataContext.setNoteBooks(mStorage, mNoteBooks);
        this.saveNotePageData();
    }

    /**
     * Saves current page data synchronously.
     */
    private void saveNotePageData() {
        this.saveNotePageData(false);
    }

    /**
     * Saves current page data with the specified concurrency.
     * @param runAsync True to run asynchronously, false to run synchronously
     */
    private void saveNotePageData(boolean runAsync) {
        if (runAsync) {
            // Save current page data asynchronously.
            new Thread(new SaveNotePageDataRunnable()).start();
        }
        else {
            // Save current page data synchronously.
            new SaveNotePageDataRunnable().run();
        }
    }

    /**
     * Loads the current page to the primary canvas view.
     */
    private void loadCanvasViewNotePage() {
        NoteBook noteBook = mNoteBooks.get(mNoteBookIndex);
        Bitmap pageBitmap = NotePageDataContext.getNotePage(mStorage, noteBook.getPage(mNotePageIndex));

        // Load new note page.
        mCanvasView.primaryCanvas.drawBitmap(pageBitmap, 0, 0, mCanvasView.primaryPaint);

        // Draw background.
        IBackground bg = new GridBackground();
        bg.onSelect(mCanvasView);
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

    /**
     * Runnable class to save note page data asynchronously.
     */
    private class SaveNotePageDataRunnable implements Runnable {
        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            NotePageDataContext.setNotePage(mStorage, mNoteBooks.get(mNoteBookIndex).getPage(mNotePageIndex), mCanvasView.copyPrimaryBitmap());
        }
    }
}
