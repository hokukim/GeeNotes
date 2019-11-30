package com.weehoo.geenotes.tool;

import android.view.MotionEvent;
import com.weehoo.geenotes.canvas.CanvasView;

public class PenTool implements ITool {

    private CanvasView mCanvasView;

    public PenTool(CanvasView canvasView) {
        mCanvasView = canvasView;
    }

    /**
     * Called when a touch screen event needs to be handled by the input object.
     * Input object should draw to the canvas view.
     * When event is successfully handled, canvas view will invalidate and redraw itself.
     * @param event  The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // Draw lines between batched historical points.
            for (int j = 0; j < event.getHistorySize() - 1; j++) {
                mCanvasView.primaryCanvas.drawLine(event.getHistoricalX(j), event.getHistoricalY(j),
                                    event.getHistoricalX(j + 1), event.getHistoricalY(j + 1),
                                    mCanvasView.primaryPaint);
            }
        }

        return true;
    }

    /**
     * Called when the tool is deselected as the primary drawing tool.
     */
    @Override
    public void onDeselect() {
        // Do nothing.
    }

    /**
     * Adjusts input offsets due to status bar etc.
     *
     * @param x Input offset x.
     * @param y Input offset y.
     */
    @Override
    public void setInputOffsets(float x, float y) {

    }
}
