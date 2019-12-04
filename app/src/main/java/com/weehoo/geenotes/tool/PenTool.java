package com.weehoo.geenotes.tool;

import android.graphics.PointF;
import android.view.MotionEvent;
import com.weehoo.geenotes.canvas.CanvasView;

public class PenTool implements ITool {

    private CanvasView mCanvasView;
    private PointF mStartPoint;

    /**
     * Called when the tool is selected as the primary drawing tool.
     *
     * @param canvasView
     */
    @Override
    public void onSelect(CanvasView canvasView) {
        mCanvasView = canvasView;
        mStartPoint = null;
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
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mStartPoint = new PointF(event.getX(), event.getY());
                mCanvasView.primaryCanvas.drawPoint(mStartPoint.x, mStartPoint.y, mCanvasView.primaryPaint);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mStartPoint == null) {
                    mStartPoint = new PointF(event.getX(), event.getY());
                    mCanvasView.primaryCanvas.drawPoint(mStartPoint.x, mStartPoint.y, mCanvasView.primaryPaint);
                    return true;
                }

                // Draw lines between batched historical points.
                for (int j = 0; j < event.getHistorySize() - 1; j++) {
                    mCanvasView.primaryCanvas.drawLine(mStartPoint.x, mStartPoint.y,
                            event.getHistoricalX(j + 1), event.getHistoricalY(j + 1),
                            mCanvasView.primaryPaint);

                    // Set start of next segment.
                    mStartPoint.x = event.getHistoricalX(j + 1);
                    mStartPoint.y = event.getHistoricalY(j + 1);
                }
                return true;
            }
            case MotionEvent.ACTION_UP: {
                mStartPoint = null;
                return false;
            }
        }

        return false;
    }

    /**
     * Called when the tool is deselected as the primary drawing tool.
     */
    @Override
    public void onDeselect() {
        // Do nothing.
    }
}
