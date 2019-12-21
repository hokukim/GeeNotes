package com.weehoo.geenotes;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import com.weehoo.geenotes.canvas.CanvasView;
import com.weehoo.geenotes.dimensions.StatusBar;
import com.weehoo.geenotes.tool.EraserTool;
import com.weehoo.geenotes.tool.ITool;
import com.weehoo.geenotes.tool.PenTool;
import com.weehoo.geenotes.tool.SelectionTool;
import java.util.ArrayList;
import java.util.HashMap;

public class NoteActivity extends AppCompatActivity {

    private CanvasView mCanvasView;
    private ArrayList<ITool> mTools;
    private HashMap<Integer, ITool> mToolsMap;
    private ITool mTool;
    private MenuItem mToolMenuItem;

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
        actionBar.setDisplayHomeAsUpEnabled(true); // Display up button in place of home button.

        // Register tools.
        mTools = new ArrayList<>();
        mToolsMap = new HashMap<>();
        this.RegisterTools();

        // Set default tool.
        mTool = mTools.get(0);
        mTool.onSelect(mCanvasView);

        int yOffset = StatusBar.getStatusBarHeight();
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
                //case MotionEvent.TOOL_TYPE_FINGER:
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

        // Add tool menu items.
        for (int i = 0; i < mTools.size(); i++) {
            ITool tool = mTools.get(i);
            int iconRes = i == 0 ? tool.getIconResActive() : tool.getIconResInactive();
            menu.add(0, i, i, "")
                .setIcon(iconRes)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        mToolMenuItem = menu.getItem(0);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            return super.onOptionsItemSelected(item);
        }

        // Deselect previous tool.
        mToolMenuItem.setIcon(mTool.getIconResInactive());
        mTool.onDeselect();

        // Select new tool.
        mTool = mToolsMap.get(id);
        mToolMenuItem = item;
        mToolMenuItem.setIcon(mTool.getIconResActive());
        mTool.onSelect(mCanvasView);

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
    private void RegisterTools() {
        // *** Add tool here. ***
        mTools.add(new PenTool());
        mTools.add(new EraserTool());
        mTools.add(new SelectionTool());
        // **********************

        for (int i = 0; i < mTools.size(); i++) {
            mToolsMap.put(i, mTools.get(i));
        }
    }
}
