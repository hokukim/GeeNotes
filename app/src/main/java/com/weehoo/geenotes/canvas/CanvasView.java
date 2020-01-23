package com.weehoo.geenotes.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.weehoo.geenotes.R;
import com.weehoo.geenotes.dimensions.StatusBar;

public class CanvasView extends View {
    private final Bitmap.Config bitmapConfig;

    public Canvas backgroundCanvas;
    public Canvas primaryCanvas;
    public Canvas overlayCanvas;

    public Paint backgroundPaint;
    public Paint primaryPaint;
    public Paint overlayPaint;

    // Store an internal canvas with bitmaps.
    //  Draw onto internal bitmaps through an internal canvas,
    //  Then draw the bitmaps to the UI canvas view.
    private Bitmap mBackgroundBitmap;
    private Bitmap mPrimaryBitmap;
    private Bitmap mOverlayBitmap;

    private PointF mInputOffsets;

    /**
     * Constructor.
     * @param context App context.
     * @param attrs Attribute set.
     */
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInputOffsets = new PointF(0, 0);
        mInputOffsets.x -= TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                (float) getResources().getDimension(R.dimen.view_padding),
                getResources().getDisplayMetrics())/2;

        // Initialize paints.
        bitmapConfig = Bitmap.Config.ARGB_8888;
        this.initializePaints();
    }

    /**
     * Clears the overlay bitmap.
     */
    public void clearOverlay() {
        mOverlayBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    /**
     * Clears the primary bitmap.
     */
    public void clearPrimary() {
        mPrimaryBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    /**
     * Clears the background bitmap.
     */
    public void clearBackground() {
        mBackgroundBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    /**
     * Gets a copy of the primary drawing bitmap.
     * @return Bitmap copy.
     */
    public Bitmap copyPrimaryBitmap() {
        return Bitmap.createBitmap(mPrimaryBitmap);
    }

    /**
     * Gets a copy of the primary drawing bitmap within the specified rect.
     * @param rect Rectangle.
     * @return Bitmap copy.
     */
    public Bitmap copyPrimaryBitmap(Rect rect) {
        return Bitmap.createBitmap(mPrimaryBitmap, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
    }

    /**
     * Get input offsets due to status bar, toolbars, etc.
     */
    public PointF getInputOffsets() {
        return mInputOffsets;
    }

    /**
     * Draw on canvas.
     *
     * @param canvas The view canvas on which to draw.
     */
    @Override
    protected void onDraw(Canvas canvas) {

        // Draw in this order: background, primary, overlay.
        canvas.drawBitmap(mBackgroundBitmap, 0, 0, backgroundPaint);
        canvas.drawBitmap(mPrimaryBitmap, 0, 0, primaryPaint);
        canvas.drawBitmap(mOverlayBitmap, 0, 0, overlayPaint);
        super.onDraw(canvas);
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

        // Set adjusted height, possibly due to toolbars.
        int[] loc = new int[2];
        getLocationOnScreen(loc);
        //h -= (loc[1] - StatusBar.getStatusBarHeight());
        mInputOffsets.y = loc[1] ;

        // Set background canvas with bitmap.
        mBackgroundBitmap = Bitmap.createBitmap(w, h, bitmapConfig);
        backgroundCanvas = new Canvas(mBackgroundBitmap);

        // Set primary canvas with bitmap.
        mPrimaryBitmap = Bitmap.createBitmap(w, h, bitmapConfig);
        primaryCanvas = new Canvas(mPrimaryBitmap);

        // Set overlay canvas with bitmap.
        mOverlayBitmap = Bitmap.createBitmap(w, h, bitmapConfig);
        overlayCanvas = new Canvas(mOverlayBitmap);
    }

    /**
     * Initialize paints.
     */
    private void initializePaints() {
        // Background paint.
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(1);
        backgroundPaint.setStrokeCap(Paint.Cap.SQUARE);
        backgroundPaint.setStrokeJoin(Paint.Join.BEVEL);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setDither(true);

        // Primary paint.
        primaryPaint = new Paint();
        primaryPaint.setColor(Color.BLACK);
        primaryPaint.setStyle(Paint.Style.STROKE);
        primaryPaint.setStrokeWidth(4);
        primaryPaint.setStrokeCap(Paint.Cap.ROUND);
        primaryPaint.setStrokeJoin(Paint.Join.ROUND);
        primaryPaint.setAntiAlias(true);
        primaryPaint.setDither(true);

        // Overlay paint.
        overlayPaint = new Paint();
        overlayPaint.setColor(Color.BLACK);
        overlayPaint.setStyle(Paint.Style.STROKE);
        overlayPaint.setStrokeWidth(2);
        overlayPaint.setStrokeCap(Paint.Cap.SQUARE);
        overlayPaint.setStrokeJoin(Paint.Join.BEVEL);
        overlayPaint.setAntiAlias(true);
        overlayPaint.setDither(true);
    }
}
