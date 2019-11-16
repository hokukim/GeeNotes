package com.weehoo.geenotes;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private CanvasView mCanvasView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCanvasView = new CanvasView(this);
        setContentView(mCanvasView);
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
    private boolean b = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean invalidate = false;

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            for (int i = 0; i < event.getPointerCount(); i++) {
                switch (event.getToolType(i)) {
                   // case MotionEvent.TOOL_TYPE_FINGER:
                    case MotionEvent.TOOL_TYPE_STYLUS: {
                        // Add historical (batched) points.
                        for (int j = 0; j < event.getHistorySize() - 1; j++) {
                            mCanvasView.drawLine(event.getHistoricalX(j), event.getHistoricalY(j),
                                    event.getHistoricalX(j + 1), event.getHistoricalY(j + 1));
                        }

                        invalidate = true;

                        break;
                    }
                }
            }
        }

        if (invalidate) {
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
