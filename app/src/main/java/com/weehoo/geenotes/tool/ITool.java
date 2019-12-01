package com.weehoo.geenotes.tool;

import android.view.MotionEvent;
import com.weehoo.geenotes.canvas.CanvasView;

public interface ITool {
    /**
     * Called when a touch screen event needs to be handled by the input object.
     * Input object should draw to the bitmap.
     * @param event The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     */
    boolean onTouchEvent(MotionEvent event);

    /**
     * Called when the tool is selected as the primary drawing tool.
     */
    void onSelect(CanvasView canvasView);

    /**
     * Called when the tool is deselected as the primary drawing tool.
     */
    void onDeselect();
}
