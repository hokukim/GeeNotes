package com.weehoo.geenotes.input;

import android.view.MotionEvent;
import com.weehoo.geenotes.CanvasView;

public interface IInput {
    /**
     * Called when a touch screen event needs to be handled by the input object.
     * Input object should draw to the bitmap.
     * @param event The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     */
    boolean onTouchEvent(MotionEvent event, CanvasView canvasView);
}
