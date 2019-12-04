package com.weehoo.geenotes;

import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.weehoo.geenotes.canvas.CanvasView;
import com.weehoo.geenotes.dimensions.StatusBar;
import com.weehoo.geenotes.tool.ITool;
import com.weehoo.geenotes.tool.PenTool;
import com.weehoo.geenotes.tool.SelectionTool;

public class NoteActivity extends AppCompatActivity {

    private CanvasView mCanvasView;
    private ITool mTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar_note);
        setSupportActionBar(toolbar);

        // Action bar settings.
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // Do not show app title.
        actionBar.setDisplayHomeAsUpEnabled(true); // Display up button in place of home button.

        // Set canvas view and default tool.
        mCanvasView = findViewById(R.id.canvas_view);
        mTool = new PenTool();
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
                    // Send input event to input object.)
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_pen: {
                mTool.onDeselect();
                mTool = new PenTool();
                mTool.onSelect(mCanvasView);
            } break;
            case R.id.action_select: {
                mTool.onDeselect();
                mTool = new SelectionTool();
                mTool.onSelect(mCanvasView);
            } break;
            case android.R.id.home: {
                mTool.onDeselect();
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
