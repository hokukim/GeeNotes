package com.weehoo.geenotes.background;

import com.weehoo.geenotes.canvas.CanvasView;

public interface IBackground {
    /**
     * Called when the background is selected as the background drawing.
     */
    void onSelect(CanvasView canvasView);

    /**
     * Gets this background's text.
     * @return The background's text.
     */
    String getText();

    /**
     * Get the background's active icon.
     * @return This background's active icon res.
     */
    int getIconResActive();

    /**
     * Get the background's inactive icon.
     * @return This background's inactive icon res.
     */
    int getIconResInactive();
}
