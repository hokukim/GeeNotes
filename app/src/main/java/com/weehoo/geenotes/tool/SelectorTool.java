package com.weehoo.geenotes.tool;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.weehoo.geenotes.canvas.CanvasView;

public class SelectorTool implements ITool {

    // Start and end points of selection rectangle.
    private PointF mStartPoint;
    private PointF mEndPoint;

    private Paint mSelectorRectPaint;

    public SelectorTool() {
        mStartPoint = null;
        mEndPoint = null;

        // Set selector rect paint, thin dashed.
        mSelectorRectPaint = new Paint();
        mSelectorRectPaint.setColor(Color.BLACK);
        mSelectorRectPaint.setAlpha(150);
        mSelectorRectPaint.setStyle(Paint.Style.STROKE);
        mSelectorRectPaint.setStrokeWidth(2);
        mSelectorRectPaint.setStrokeCap(Paint.Cap.SQUARE);
        mSelectorRectPaint.setStrokeJoin(Paint.Join.BEVEL);
        mSelectorRectPaint.setAntiAlias(true);
        mSelectorRectPaint.setDither(true);
        mSelectorRectPaint.setPathEffect(new DashPathEffect(new float [] {10, 10}, 0));
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
                }

                // Draw selector UI.
                this.drawSelector(canvasView);

                // Reset points so a new selection can be drawn.
                mStartPoint = null;
                mEndPoint = null;
            } break;
            case MotionEvent.ACTION_MOVE: {
                mEndPoint = new PointF(event.getX(0), event.getY(0));

                // Clear overlay, removing previously selector.
                canvasView.ClearOverlay();

                // Draw selector UI.
                this.drawSelector(canvasView, false);
            } break;
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
        canvasView.ClearOverlay();
    }

    /**
     * Draw selection box with menu.
     */
    private void drawSelector(CanvasView canvasView) {
        this.drawSelector(canvasView, true);
    }

    /**
     * Draw selection box with optional menu.
     */
    private void drawSelector(CanvasView canvasView, boolean drawMenu) {
        // Draw selection rectangle.
        RectF selectionRect = new RectF(Math.min(mStartPoint.x, mEndPoint.x), Math.min(mStartPoint.y, mEndPoint.y),
                                      Math.max(mStartPoint.x, mEndPoint.x), Math.max(mStartPoint.y, mEndPoint.y));

        canvasView.overlayCanvas.drawRect(selectionRect, mSelectorRectPaint);

        if (drawMenu) {
            // Draw menu.
            // https://developer.android.com/guide/topics/graphics/drawables
            canvasView.overlayCanvas.drawRect(selectionRect.right - 32, selectionRect.top - 32,
                    selectionRect.right, selectionRect.top,
                    mSelectorRectPaint);

//        ImageButton button = new ImageButton(canvasView.getRootView().getContext());
//        canvasView.overlayCanvas.drawPicture();
        }
    }
}
