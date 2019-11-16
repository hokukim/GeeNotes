package com.weehoo.geenotes.tool;

import android.view.MotionEvent;
import com.weehoo.geenotes.canvas.CanvasView;

public class PenTool implements ITool {
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
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // Draw lines between batched historical points.
            for (int j = 0; j < event.getHistorySize() - 1; j++) {
                canvasView.primaryCanvas.drawLine(event.getHistoricalX(j), event.getHistoricalY(j),
                                    event.getHistoricalX(j + 1), event.getHistoricalY(j + 1),
                                    canvasView.primaryPaint);
            }
        }

        return true;
    }

    /**
     * Called when the tool is deselected as the primary drawing tool.
     *
     * @param canvasView The canvas view being drawn to.
     */
    @Override
    public void onDeselect(CanvasView canvasView) {
        // Do nothing.
    }
}
