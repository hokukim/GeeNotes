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
    private RectF mSelectionRect;

    private Paint mSelectionRectPaint;

    public SelectorTool() {
        mStartPoint = null;
        mEndPoint = null;
        mSelectionRect = null;

        // Set selector rect paint, thin dashed.
        mSelectionRectPaint = new Paint();
        mSelectionRectPaint.setColor(Color.BLACK);
        mSelectionRectPaint.setAlpha(150);
        mSelectionRectPaint.setStyle(Paint.Style.STROKE);
        mSelectionRectPaint.setStrokeWidth(2);
        mSelectionRectPaint.setStrokeCap(Paint.Cap.SQUARE);
        mSelectionRectPaint.setStrokeJoin(Paint.Join.BEVEL);
        mSelectionRectPaint.setAntiAlias(true);
        mSelectionRectPaint.setDither(true);
        mSelectionRectPaint.setPathEffect(new DashPathEffect(new float [] {10, 10}, 0));
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
                this.drawSelectionRect(canvasView);

                // Reset points so a new selection can be drawn.
                mStartPoint = null;
                mEndPoint = null;
            } break;
            case MotionEvent.ACTION_MOVE: {
                mEndPoint = new PointF(event.getX(0), event.getY(0));

                // Clear overlay, removing previously selector.
                canvasView.ClearOverlay();

                // Draw selector UI.
                this.drawSelectionRect(canvasView, false);
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
    private void drawSelectionRect(CanvasView canvasView) {
        this.drawSelectionRect(canvasView, true);
    }

    /**
     * Draw selection box with optional menu.
     */
    private void drawSelectionRect(CanvasView canvasView, boolean drawMenu) {
        // Selection rectangle.
        mSelectionRect = new RectF(Math.min(mStartPoint.x, mEndPoint.x), Math.min(mStartPoint.y, mEndPoint.y),
                                      Math.max(mStartPoint.x, mEndPoint.x), Math.max(mStartPoint.y, mEndPoint.y));

        canvasView.overlayCanvas.drawRect(mSelectionRect, mSelectionRectPaint);

        // Optional menu.
        if (drawMenu) {
            // https://developer.android.com/guide/topics/graphics/drawables
            canvasView.overlayCanvas.drawRect(mSelectionRect.right - 32, mSelectionRect.top - 32,
                    mSelectionRect.right, mSelectionRect.top,
                    mSelectionRectPaint);

//        ImageButton button = new ImageButton(canvasView.getRootView().getContext());
//        canvasView.overlayCanvas.drawPicture();
        }
    }
}
