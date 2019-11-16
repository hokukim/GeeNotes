package com.weehoo.geenotes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;

public class CanvasView extends View {

    // Store an internal canvas with bitmap.
    // Draw onto internal bitmap through an internal canvas,
    // Then draw the bitmap to the UI canvas view.
    private Bitmap mBitmap;
    private Canvas mCanvas;

    private Paint mPaint;

    public CanvasView(Context context) {
        super(context);

        Point size = new Point();
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(6);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    public void drawLine(float startX, float startY, float endX, float endY) {
        // Add line to internal bitmap.
        mCanvas.drawLine(startX + 6, startY - 81, endX + 6, endY - 81, mPaint);
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w    Current width of this view.
     * @param h    Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }
}
