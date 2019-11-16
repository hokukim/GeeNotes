package com.weehoo.geenotes.input;

import android.view.MotionEvent;
import com.weehoo.geenotes.CanvasView;

public class PenInput implements IInput {
    /**
     * Called when a touch screen event needs to be handled by the input object.
     * Input object should draw to the canvas view.
     * When event is successfully handled, canvas view will invalidate and redraw itself.
     * @param event  The touch screen event being processed.
     * @param canvasView  The canvas view being drawn to.
     * @return Return true if you have consumed the event, false if you haven't.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event, CanvasView canvasView) {
        // Add historical (batched) points.
        for (int j = 0; j < event.getHistorySize() - 1; j++) {
            // Draw lines between points.
            canvasView.drawLine(event.getHistoricalX(j), event.getHistoricalY(j),
                    event.getHistoricalX(j + 1), event.getHistoricalY(j + 1));
        }

        return true;
    }
}
