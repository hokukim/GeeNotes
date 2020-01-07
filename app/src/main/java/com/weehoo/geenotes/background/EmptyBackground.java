package com.weehoo.geenotes.background;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.weehoo.geenotes.GeeNotesApplication;
import com.weehoo.geenotes.R;
import com.weehoo.geenotes.canvas.CanvasView;

public class EmptyBackground implements IBackground {

    private final String mText;

    /**
     * Constructs a Grid background object.
     */
    public EmptyBackground() {
        mText = GeeNotesApplication.getContext().getResources().getString(R.string.background_text_empty);
    }

    /**
     * Called when the background is selected as the background drawing.
     *
     * @param canvasView
     */
    @Override
    public void onSelect(CanvasView canvasView) {
        // Do nothing.
        // Empty background is empty.
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
        return R.drawable.ic_page_menu_background_empty_active;
    }

    /**
     * Get the background's inactive icon.
     *
     * @return This background's inactive icon res.
     */
    @Override
    public int getIconResInactive() {
        return R.drawable.ic_page_menu_background_empty_inactive;
    }
}
