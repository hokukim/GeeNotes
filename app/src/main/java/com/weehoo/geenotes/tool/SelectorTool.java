package com.weehoo.geenotes.tool;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import com.weehoo.geenotes.canvas.CanvasView;

public class SelectorTool implements ITool {

    // Start and end points of selection rectangle.
    private PointF mStartPoint;
    private PointF mEndPoint;

    public SelectorTool() {
        mStartPoint = null;
        mEndPoint = null;
    }

    /**
     * Called when a touch screen event needs to be handled by the input object.
     * Input object should draw to the bitmap.
     *
     * @param event      The touch screen event being processed.
     * @param canvasView    The canvas view being drawn to.
     * @return Return true if you have consumed the event, false if you haven't.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event, CanvasView canvasView) {
        // On event down.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (mStartPoint == null) {
                    // Clear overlay.
                    canvasView.ClearOverlay();

                    // Start new selection rectangle.
                    mStartPoint = new PointF(event.getX(0), event.getY(0));
                }
            } break;

            case MotionEvent.ACTION_UP: {
                if (mEndPoint == null) {
                    // End new selection rectangle.
                    mEndPoint = new PointF(event.getX(0), event.getY(0));

                    canvasView.overlayCanvas.drawRect(mStartPoint.x, mStartPoint.y,
                                                mEndPoint.x, mEndPoint.y,
                                                canvasView.overlayPaint);

                    // Reset points so a new selection can be drawn.
                    mStartPoint = null;
                    mEndPoint = null;
                }

            }
        }

        return true;
    }

    /**
     * Called when the tool is deselected as the primary drawing tool.
     *
     * This implementation:
     * Clear selector rectangle and menu.
     *
     * @param canvasView The canvas view being drawn to.
     */
    @Override
    public void onDeselect(CanvasView canvasView) {
        // Clear selector rectangle.
    }
}
