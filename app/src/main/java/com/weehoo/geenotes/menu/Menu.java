package com.weehoo.geenotes.menu;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import java.util.ArrayList;

/**
 * A menu with items that can be drawn to a canvas.
 */
public class Menu {
    private final float MENU_ITEM_WIDTH = 48;
    private final float MENU_ITEM_HEIGHT = 48;

    // Menu rectangle.
    private RectF mRect;

    // List of menu items.
    private ArrayList<MenuItem> mLeftItems;
    private ArrayList<MenuItem> mRightItems;

    /**
     * Default constructor.
     */
    public Menu() {
        mRect = new RectF();
        mLeftItems = new ArrayList<>();
        mRightItems = new ArrayList<>();
    }

    /**
     * Adds a menu item to the menu.
     * @param menuItem Menu item.
     * @param menuAlign Indicates on which side of the menu the item will be placed.
     */
    public void addItem(MenuItem menuItem, MenuAlign menuAlign) {
        // Create new menu item with scaled bitmap.
        MenuItem scaledMenuItem = new MenuItem(menuItem.getId(), Bitmap.createScaledBitmap(menuItem.getBitmap(), (int)MENU_ITEM_WIDTH, (int)MENU_ITEM_HEIGHT, false));

        switch (menuAlign) {
            case MENU_ALIGN_LEFT: {
                mLeftItems.add(scaledMenuItem);
            } break;

            case MENU_ALIGN_RIGHT: {
                mRightItems.add(scaledMenuItem);
            } break;
        }

        return;
    }

    /**
     * Gets the menu item at the specified point.
     * @param point Point used for determining which menu item to get.
     * @return
     */
    public MenuItem getItemAt(PointF point) {
        MenuItem menuItem = null;

        if (point.y >= mRect.top && point.y <= mRect.bottom) {
            if (point.x >= mRect.left && point.x <= mRect.left + (mLeftItems.size() * MENU_ITEM_WIDTH)) {
                // Event occurred in left menu.
                int index = (int)((point.x - mRect.left) / MENU_ITEM_WIDTH);
                menuItem = mLeftItems.get(index);
            }
            else if (point.x <= mRect.right && point.x >= mRect.right - (mRightItems.size() * MENU_ITEM_WIDTH)) {
                // Event occurred in right menu.
                int index = (int)(mRightItems.size() - ((mRect.right - point.x) / mRightItems.size()));
                menuItem = mRightItems.get(index);
            }
        }

        return menuItem;
    }

    /**
     * Draws the menu items based on the specified point and anchor.
     * @param canvas The canvas to draw on.
     * @param rect The rect from which the Menu rectangle will originate.
     * @param menuAnchor Indicates if the origination point is top-left or bottom-right.
     *                   A top-left anchor means that the specified point is the top-left point; bottom-right point will be calculated internally.
     *                   A bottom-right anchor means that the specified point is the bottom-right point; top-left point will be calculated internally.
     * @param paint The paint used to draw on the canvas.
     */
    public void draw(Canvas canvas, RectF rect, MenuAnchor menuAnchor, Paint paint) {
        mRect = null;

        // Determine menu rect.
        switch (menuAnchor) {
            case MENU_ANCHOR_TOP: {
                mRect = new RectF(rect.left, rect.top, rect.right, rect.top + MENU_ITEM_HEIGHT); // Anchor menu at top; expand menu down.
            } break;

            case MENU_ANCHOR_BOTTOM: {
                mRect = new RectF(rect.left, rect.bottom - MENU_ITEM_HEIGHT, rect.right, rect.bottom); // Anchor menu at bottom; expand menu up.
            } break;
        }

        // Draw left and right menu items concurrently.
        for (int i = 0; i < mLeftItems.size(); i++) {
            // Calculate item rect.
            float xTranslate = i * MENU_ITEM_WIDTH;  // x translation from rect left edge.
            float left = mRect.left + xTranslate;
            RectF itemRect = new RectF(left, mRect.top, left + MENU_ITEM_WIDTH, mRect.bottom);

            // Draw left item.
            (new Thread(new MenuItemDrawRunnable(canvas, itemRect, mLeftItems.get(i).getBitmap(), paint))).start();
        }

        for (int i = 0; i < mRightItems.size(); i++) {
            float xTranslate = (mRightItems.size() - i) * MENU_ITEM_WIDTH; // x translation from rect right edge.
            float left = mRect.right - xTranslate;
            RectF itemRect = new RectF(left, mRect.top, left + MENU_ITEM_WIDTH, mRect.bottom);

            // Draw right item.
            (new Thread(new MenuItemDrawRunnable(canvas, itemRect, mRightItems.get(i).getBitmap(), paint))).start();
        }

        return;
    }


    /**
     * Runnable class draws a menu item to a canvas.
     */
    private class MenuItemDrawRunnable implements Runnable
    {
        private Canvas mCanvas;
        private Bitmap mBitmap;
        private RectF mRect;
        private Paint mPaint;

        /**
         *
         * @param canvas The canvas to draw on.
         * @param rect Menu item rectangle.
         * @param menuItem Menu item.
         * @param paint Drawing paint.
         */
        public MenuItemDrawRunnable(Canvas canvas, RectF rect, Bitmap menuItem, Paint paint) {
            mCanvas = canvas;
            mBitmap = menuItem;
            mRect = rect;
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
            // Draw icon.
            if (mBitmap != null) {
                mCanvas.drawBitmap(mBitmap, mRect.left, mRect.top, mPaint);
            }

            // Draw border rectangle.
            mCanvas.drawRect(mRect, mPaint);

            return;
        }
    }
}
