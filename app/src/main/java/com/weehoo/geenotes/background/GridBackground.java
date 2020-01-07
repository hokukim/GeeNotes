package com.weehoo.geenotes.background;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.weehoo.geenotes.GeeNotesApplication;
import com.weehoo.geenotes.R;
import com.weehoo.geenotes.canvas.CanvasView;

public class GridBackground implements IBackground {

    private final String mText;

    /**
     * Constructs a Grid background object.
     */
    public GridBackground() {
        mText = GeeNotesApplication.getContext().getResources().getString(R.string.background_text_grid);
    }

    /**
     * Called when the background is selected as the background drawing.
     * @param canvasView Canvas view which contains canvases on which to draw.
     */
    @Override
    public void onSelect(CanvasView canvasView) {
        // Create background bitmap drawable from resources.
        Resources resources = GeeNotesApplication.getContext().getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.bg_square);

        for (int x = 0; x < canvasView.getWidth(); x += bitmap.getWidth()) {
            for (int y = 0; y < canvasView.getHeight(); y += bitmap.getHeight()) {
                // Draw to background canvas.
                Thread thread = new Thread(new GridBackgroundDrawRunnable(canvasView.backgroundCanvas, bitmap, x, y, canvasView.backgroundPaint));
                thread.start();
            }
        }
    }

    /**
     * Gets this background's text.
     *
     * @return The background's text.
     */
    @Override
    public String getText() {
        return mText;
    }

    /**
     * Get the background's active icon.
     *
     * @return This background's active icon res.
     */
    @Override
    public int getIconResActive() {
        return R.drawable.ic_page_menu_background_grid_active;
    }

    /**
     * Get the background's inactive icon.
     *
     * @return This background's inactive icon res.
     */
    @Override
    public int getIconResInactive() {
        return R.drawable.ic_page_menu_background_grid_inactive;
    }

    /**
     * Runnable class draws a grid background to a canvas.
     */
    private class GridBackgroundDrawRunnable implements Runnable {
        private Canvas mCanvas;
        private Bitmap mBitmap;
        private float mX;
        private float mY;
        private Paint mPaint;

        public GridBackgroundDrawRunnable(Canvas canvas, Bitmap bitmap, float x, float y, Paint paint) {
            mCanvas = canvas;
            mBitmap = bitmap;
            mX = x;
            mY = y;
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
            // Draw to canvas.
            mCanvas.drawBitmap(mBitmap, mX, mY, mPaint);
        }
    }
}
