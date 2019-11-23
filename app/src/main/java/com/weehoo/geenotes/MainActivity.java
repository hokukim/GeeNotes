package com.weehoo.geenotes;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.weehoo.geenotes.canvas.CanvasView;
import com.weehoo.geenotes.tool.ITool;
import com.weehoo.geenotes.tool.PenTool;
import com.weehoo.geenotes.tool.SelectorTool;

public class MainActivity extends AppCompatActivity {

    private CanvasView mCanvasView;
    private ITool mTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCanvasView = new CanvasView(this);
        setContentView(mCanvasView);

        mTool = new SelectorTool(this);
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
        boolean drawingChanged = false;

        for (int i = 0; i < event.getPointerCount(); i++) {
            switch (event.getToolType(i)) {
                case MotionEvent.TOOL_TYPE_FINGER:
                case MotionEvent.TOOL_TYPE_STYLUS: {
                    // Send input event to input object.)
                    drawingChanged = mTool.onTouchEvent(event, mCanvasView);
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
