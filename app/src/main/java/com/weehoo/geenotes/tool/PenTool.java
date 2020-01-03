package com.weehoo.geenotes.tool;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.weehoo.geenotes.R;
import com.weehoo.geenotes.canvas.CanvasView;

import java.util.HashMap;

public class PenTool implements ITool {

    protected Paint mPaint;
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
        mPaint = mCanvasView.primaryPaint;
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
                mCanvasView.primaryCanvas.drawPoint(mStartPoint.x, mStartPoint.y, mPaint);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mStartPoint == null) {
                    // For some reason it's possible that the ACTION_DOWN event didn't fire, so set the first down event here.
                    mStartPoint = new PointF(event.getX(), event.getY());
                    mCanvasView.primaryCanvas.drawPoint(mStartPoint.x, mStartPoint.y, mPaint);
                    return true;
                }

                // Draw lines between batched historical points.
                for (int j = 1; j < event.getHistorySize() - 1; j++) {
                    new PenDrawLineRunnable(mCanvasView.primaryCanvas, mStartPoint.x, mStartPoint.y,
                            event.getHistoricalX(j + 1), event.getHistoricalY(j + 1),
                            mPaint).run();

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
    /**
     * Called to get tool's active icon.
     * @return This tool's active icon res.
     */
    @Override
    public int getIconResActive() {
        return R.drawable.ic_tool_menu_pen_active;
    }

    /**
     * Called to get tool's inactive icon.
     * @return This tool's inactive icon res.
     */
    @Override
    public int getIconResInactive() {
        return R.drawable.ic_tool_menu_pen_inactive;
    }


    /**
     * Runnable class draws a pen to a canvas.
     */
    private class PenDrawLineRunnable implements Runnable {
        private Canvas mCanvas;
        private float mStartX;
        private float mEndX;
        private float mStartY;
        private float mEndY;
        private Paint mPaint;

        public PenDrawLineRunnable(Canvas canvas, float startX, float startY, float endX, float endY, Paint paint) {
            mCanvas = canvas;
            mStartX = startX;
            mStartY = startY;
            mEndX = endX;
            mEndY = endY;
            mPaint = paint;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            mCanvasView.primaryCanvas.drawLine(mStartX, mStartY,
                    mEndX, mEndY,
                    mPaint);
        }
    }
}
