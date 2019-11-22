package com.weehoo.geenotes.menu;

import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import com.weehoo.geenotes.canvas.CanvasView;
import java.util.ArrayList;

public class Menu {
    private RectF mRect;

    // List of menu items.
    private ArrayList<MenuItem> mLeftItems;
    private ArrayList<MenuItem> mRightItems;

    public Menu() {
        mLeftItems = new ArrayList<MenuItem>();
        mRightItems = new ArrayList<MenuItem>();
    }

    /**
     *
     * @param menuItem
     * @param menuAlign
     */
    public void addItem(MenuItem menuItem, MenuAlign menuAlign) {

        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /**
     *
     * @param point
     * @return
     */
    public MenuItem getItemAt(PointF point) {
        // Binary search through assumed coordinates of menu items.
        //

        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /**
     *
     * @param canvasView
     * @param paint
     */
    public void onDraw(CanvasView canvasView, Paint paint) {

        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
