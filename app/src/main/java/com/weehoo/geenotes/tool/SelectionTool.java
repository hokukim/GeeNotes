package com.weehoo.geenotes.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.weehoo.geenotes.R;
import com.weehoo.geenotes.canvas.CanvasView;
import com.weehoo.geenotes.menu.Menu;
import com.weehoo.geenotes.menu.MenuAlignType;
import com.weehoo.geenotes.menu.MenuAnchorType;
import com.weehoo.geenotes.menu.MenuItem;
import com.weehoo.geenotes.menu.MenuItemType;

public class SelectionTool implements ITool {
    private CanvasView mCanvasView;

    // Start and end points of selection rectangle.
    private PointF mStartPoint;
    private PointF mEndPoint;
    private RectF mSelectionRect;

    // Menu.
    private Menu mMenu;

    // Drawing.
    private Paint mSelectionRectPaint;
    private boolean mMenuIsOpen;

    /**
     * Constructor.
     */
    public SelectionTool(Context context, CanvasView canvasView) {
        mCanvasView = canvasView;

        mStartPoint = null;
        mEndPoint = null;
        mSelectionRect = null;
        mMenuIsOpen = false;

        this.initializeSelectionMenu(context);
        this.initializeSelectionPaint();
    }

    /**
     * Called when a touch screen event needs to be handled by the input object.
     * Input object should draw to the bitmap.
     *
     * @param event      The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        PointF touchPoint = new PointF(event.getX(0), event.getY(0));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // Check if down action on a menu item.
                MenuItem menuItem = this.getMenuItemSelected(touchPoint);

                if (menuItem != null) {
                    // Ignore this event.
                    // Menu items do not respond to down events.
                    return true;
                }

                // Menu item was not used.
                // Clear previous selection UI.
                this.onDeselect();

                // Process touch event.
                if (mStartPoint == null) {
                    // Start new selection rectangle.
                    mStartPoint = touchPoint;
                }
            } break;
            case MotionEvent.ACTION_UP: {
                // Check if up action on a menu item.
                MenuItem menuItem = this.getMenuItemSelected(touchPoint);

                if (menuItem != null) {
                    switch (menuItem.getType()) {
                        case MENU_ITEM_TYPE_CANCEL: {
                            // Clear selection UI.
                            this.onDeselect();

                            return true;
                        }
                        case MENU_ITEM_TYPE_DELETE: {
                            // Delete contents in selection rect.
                            this.deleteSelection();

                            // Clear selection UI.
                            this.onDeselect();

                            return true;
                        }
                    }
                }

                // Menu item was not used.
                // Process touch event.
                if (mEndPoint == null) {
                    // End new selection rectangle.
                    mEndPoint = touchPoint;
                }

                // Draw selector UI.
                this.drawSelectionUI();

                // Reset points so a new selection can be drawn.
                mStartPoint = null;
                mEndPoint = null;
            } break;
            case MotionEvent.ACTION_MOVE: {
                mEndPoint = touchPoint;

                // Clear overlay, removing previous selection UI.
                mCanvasView.ClearOverlay();

                // Draw selector UI.
                this.drawSelectionUI(false);
            } break;
        }

        return true;
    }

    /**
     * Called when the tool is deselected as the primary drawing tool.
     *
     * This implementation:
     * Clear selection UI.
     */
    @Override
    public void onDeselect() {
        // Clear selector rectangle.
        mCanvasView.ClearOverlay();

        // Reset start and end rect points.
        mStartPoint = null;
        mEndPoint = null;
        mSelectionRect = null;
        mMenuIsOpen = false;
    }

    /**
     * Delete contents in selection rect.
     */
    private void deleteSelection() {
        // Create an empty bitmap with the same size as the selection rect.
        Bitmap emptyBitmap = Bitmap.createBitmap((int)(mSelectionRect.right - mSelectionRect.left + 1),
                                                 (int)(mSelectionRect.bottom - mSelectionRect.top + 1),
                                                  mCanvasView.bitmapConfig);

        mCanvasView.primaryCanvas.drawBitmap(emptyBitmap, null, mSelectionRect, null);
    }

    /**
     * Draw selection box with menu.
     */
    private void drawSelectionUI() {
        this.drawSelectionUI(true);
    }

    /**
     * Draw selection UI with optional menu.
     */
    private void drawSelectionUI(boolean drawMenu) {
        if (mStartPoint == null || mEndPoint == null) {
            return;
        }

        // Selection rectangle.
        mSelectionRect = new RectF(Math.min(mStartPoint.x, mEndPoint.x), Math.min(mStartPoint.y, mEndPoint.y),
                                      Math.max(mStartPoint.x, mEndPoint.x), Math.max(mStartPoint.y, mEndPoint.y));

        mCanvasView.overlayCanvas.drawRect(mSelectionRect, mSelectionRectPaint);

        mMenuIsOpen = false;

        // Optional menu.
        if (drawMenu) {
            RectF menuRect = new RectF(mSelectionRect.left, 0, mSelectionRect.right, mSelectionRect.top);
            mMenu.draw(mCanvasView.overlayCanvas, menuRect, MenuAnchorType.MENU_ANCHOR_BOTTOM, mSelectionRectPaint);
            mMenuIsOpen = true;
        }
    }

    /**
     * Gets a menu item selected at the specified point.
     * @param point Point at which to find a menu item.
     * @return Menu item selected, or null if a menu item does not exist at the point.
     */
    private MenuItem getMenuItemSelected(PointF point) {
        MenuItem menuItem = null;

        if (mMenuIsOpen) {
            menuItem = mMenu.getItemAt(point);
        }

        return menuItem;
    }
    private void initializeSelectionMenu(Context context) {
        // Initialize selector menu.
        mMenu = new Menu();

        // Menu item: Cancel.
        Bitmap cancelBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_selector_menu_cancel);
        mMenu.addItem(new MenuItem(MenuItemType.MENU_ITEM_TYPE_CANCEL, cancelBitmap), MenuAlignType.MENU_ALIGN_RIGHT);

        // Menu item: Move.
        Bitmap moveBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_selector_menu_move);
        mMenu.addItem(new MenuItem(MenuItemType.MENU_ITEM_TYPE_MOVE, moveBitmap), MenuAlignType.MENU_ALIGN_RIGHT);

        // Menu item: Delete.
        Bitmap deleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_selector_menu_delete);
        mMenu.addItem(new MenuItem(MenuItemType.MENU_ITEM_TYPE_DELETE, deleteBitmap), MenuAlignType.MENU_ALIGN_LEFT);
    }

    private void initializeSelectionPaint() {
        // Initialize and set selector paint, thin dashed.
        mSelectionRectPaint = new Paint();
        mSelectionRectPaint.setColor(Color.BLACK);
        mSelectionRectPaint.setAlpha(150);
        mSelectionRectPaint.setStyle(Paint.Style.STROKE);
        mSelectionRectPaint.setStrokeWidth(2);
        mSelectionRectPaint.setStrokeCap(Paint.Cap.SQUARE);
        mSelectionRectPaint.setStrokeJoin(Paint.Join.BEVEL);
        mSelectionRectPaint.setAntiAlias(true);
        mSelectionRectPaint.setDither(true);
        mSelectionRectPaint.setPathEffect(new DashPathEffect(new float [] {10, 10}, 0));
    }
}
