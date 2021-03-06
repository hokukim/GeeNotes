package com.weehoo.geenotes.tool;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.weehoo.geenotes.GeeNotesApplication;
import com.weehoo.geenotes.R;
import com.weehoo.geenotes.canvas.CanvasView;
import com.weehoo.geenotes.menus.Menu;
import com.weehoo.geenotes.menus.MenuAlignType;
import com.weehoo.geenotes.menus.MenuAnchorType;
import com.weehoo.geenotes.menus.MenuItem;
import com.weehoo.geenotes.menus.MenuItemType;

public class SelectionTool implements ITool {
    private CanvasView mCanvasView;

    // Start and end points of selection rectangle.
    private PointF mStartPoint;
    private PointF mEndPoint;
    private RectF mSelectionRect;

    // Input offsets.
    private PointF mInputOffsets;

    // Menu.
    private Menu mMenu;
    private MenuItem mActiveMenuItem;

    // Drawing.
    private Paint mSelectionRectPaint;
    private boolean mMenuIsOpen;
    private Bitmap mCopiedBitmap;

    private boolean mIsActive;

    /**
     * Constructor.
     */
    public SelectionTool() {
        mIsActive = false;
        mStartPoint = null;
        mEndPoint = null;
        mSelectionRect = null;
        mMenuIsOpen = false;
        mCopiedBitmap = null;
        mActiveMenuItem = null;
        mInputOffsets = new PointF(0 , 0);

        this.initializeSelectionMenu();
        this.initializeSelectionPaint();
    }

    /**
     * Called when the tool is selected as the primary drawing tool.
     *
     * @param canvasView
     */
    @Override
    public void onSelect(CanvasView canvasView) {
        mCanvasView = canvasView;
    }

    /**
     * Called when a touch screen event needs to be handled by the input object.
     * Input object should draw to the canvas bitmap.
     *
     * @param event The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF touchPoint = new PointF(event.getX(0), event.getY(0));
        touchPoint.y -= mInputOffsets.y;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mIsActive = true;

                // Check if down action on a menu item.
                if (mMenuIsOpen) {
                    MenuItem menuItem = this.getMenuItemSelected(touchPoint);

                    if (menuItem != null) {
                        // Save the menu item as being active.
                        mActiveMenuItem = menuItem;

                        if (menuItem.getType() == MenuItemType.MENU_ITEM_TYPE_MOVE) {
                            // Cut selection from primary canvas.
                            // Store selection for moving; selection will be redrawn to the primary canvas when moving is complete.
                            Rect rect = new Rect();
                            mSelectionRect.round(rect);
                            mCopiedBitmap = mCanvasView.copyPrimaryBitmap(rect);

                            // Draw to copied bitmap to overlay.
                            mCanvasView.overlayCanvas.drawBitmap(mCopiedBitmap, null, mSelectionRect, null);

                            // Remove copied bitmap from primary.
                            this.deleteSelection();
                        }

                        return false; // Event handled, nothing to redraw.
                    }
                }

                // Menu item was not used.
                // Clear previous selection UI.
                this.onDeselect();

                // Process touch event.
                if (mStartPoint == null) {
                    // Start new selection rectangle.
                    mStartPoint = touchPoint;
                    this.setInBounds(mStartPoint);
                    mEndPoint = mStartPoint;
                }
            } break;
            case MotionEvent.ACTION_UP: {
                mIsActive = false;

                if (mActiveMenuItem != null && mActiveMenuItem.getType() == MenuItemType.MENU_ITEM_TYPE_MOVE) {
                    // Selection moving is complete.
                    // Draw copied bitmap back to primary.
                    mCanvasView.primaryCanvas.drawBitmap(mCopiedBitmap, null, mSelectionRect, mCanvasView.primaryPaint);
                    mCopiedBitmap = null;
                    mActiveMenuItem = null;

                    return true;
                }

                // Check if Up action on a menu item.
                MenuItem menuItem = this.getMenuItemSelected(touchPoint);

                if (menuItem != null && mActiveMenuItem != null &&
                        menuItem.getType() == mActiveMenuItem.getType()) {
                    // Up action occurred on the active menu item.
                    // Process this action for the menu item.
                    switch(menuItem.getType()) {
                        case MENU_ITEM_TYPE_CANCEL: {
                            // Clear selection UI.
                            this.onDeselect();
                        } break;
                        case MENU_ITEM_TYPE_DELETE: {
                            // Delete content in selection UI.
                            this.deleteSelection();

                            // Clear selection UI.
                            this.onDeselect();
                        } break;
                    }

                    // Menu Up action has been handled.
                    // Reset the active menu item, and stop processing.
                    mActiveMenuItem = null;
                    return true; // Redraw.
                }

                // Menu item was not used.
                mActiveMenuItem = null;

                // Process touch event.
                if (mEndPoint == null) {
                    // End new selection rectangle.
                    mEndPoint = touchPoint;
                    this.setInBounds(mEndPoint);
                }

                // Draw selector UI.
                this.drawSelectionUI();
            } break;
            case MotionEvent.ACTION_MOVE: {
                mIsActive = true;

                // Check if currently moving selection.
                if (mActiveMenuItem != null && mActiveMenuItem.getType() == MenuItemType.MENU_ITEM_TYPE_MOVE) {
                    // Selection is being moved.
                    // Calculate amount to move.
                    int lastPos = event.getHistorySize() - 1;
                    if (lastPos <= 0) {
                        break;
                    }

                    float xDiff = touchPoint.x - event.getHistoricalX(0);
                    float yDiff = touchPoint.y - event.getHistoricalY(0);

                    // Move contents in selection UI.
                    RectF toRect = new RectF(mSelectionRect.left + xDiff,
                            mSelectionRect.top + yDiff,
                            mSelectionRect.right + xDiff,
                            mSelectionRect.bottom + yDiff);

                    // Adjust destination rect boundaries.
                    if (toRect.left < 0) {
                        toRect.left = 0;
                        toRect.right = mSelectionRect.right;
                    }
                    if (toRect.top < 0) {
                        toRect.top = 0;
                        toRect.bottom = mSelectionRect.bottom;
                    }

                    if (toRect.right > mCanvasView.primaryCanvas.getWidth()) {
                        toRect.right = mCanvasView.primaryCanvas.getWidth();
                        toRect.left = mSelectionRect.left;
                    }
                    if (toRect.bottom > mCanvasView.primaryCanvas.getHeight()) {
                        toRect.bottom = mCanvasView.primaryCanvas.getHeight();
                        toRect.top = mSelectionRect.top;
                    }

                    // Move selection UI and redraw.
                    mStartPoint = new PointF(toRect.left, toRect.top);
                    mEndPoint = new PointF(toRect.right, toRect.bottom);

                    mCanvasView.clearOverlay();
                    this.drawSelectionUI(true);

                    return true; // Redraw.
                }

                // Check if Move action on a menu item.
                MenuItem menuItem = this.getMenuItemSelected(touchPoint);

                if (menuItem != null && menuItem.getType() == mActiveMenuItem.getType()) {
                    switch (menuItem.getType()) {
                        case MENU_ITEM_TYPE_CANCEL:
                        case MENU_ITEM_TYPE_DELETE: {
                            // Do nothing.
                            return false; // Nothing to redraw.
                        }
                    }
                }

                // Menu item was not used.
                // Continue expanding selection UI rect without menu.
                // Clear overlay, removing previous selection UI rect.
                mCanvasView.clearOverlay();

                // Draw new selection UI without menu.
                mEndPoint = touchPoint;
                this.setInBounds(mEndPoint);

                this.drawSelectionUI(false);
            } break;
        }

        return true; // Redraw.
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
        mCanvasView.clearOverlay();

        // Reset start and end rect points.
        mStartPoint = null;
        mEndPoint = null;
        mSelectionRect = null;

        mMenuIsOpen = false;
    }

    /**
     * Called to get tool's active icon.
     * @return This tool's active icon res.
     */
    @Override
    public int getIconResActive() {
        return R.drawable.ic_tool_menu_selection_active;
    }

    /**
     * Called to get tool's inactive icon.
     * @return This tool's inactive icon res.
     */
    @Override
    public int getIconResInactive() {
        return R.drawable.ic_tool_menu_selection_inactive;
    }

    /**
     * Adjusts the point to be within the bounds of the drawing canvas.
     * @param point Point to adjust.
     */
    private void setInBounds(PointF point) {
        point.x = Math.max(point.x, 0);
        point.x = Math.min(point.x, mCanvasView.primaryCanvas.getWidth());
        point.y = Math.max(point.y, 0);
        point.y = Math.min(point.y, mCanvasView.primaryCanvas.getHeight());
    }

    /**
     * Delete contents in selection rect.
     */
    private void deleteSelection() {
        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvasView.primaryCanvas.drawRect(mSelectionRect, clearPaint);
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

        // Copied bitmap while moving selection.
        if (mActiveMenuItem != null && mActiveMenuItem.getType() == MenuItemType.MENU_ITEM_TYPE_MOVE && mCopiedBitmap != null) {
            // Draw copied bitmap to overlay for moving.
            mCanvasView.overlayCanvas.drawBitmap(mCopiedBitmap, null, mSelectionRect, null);
        }
    }

    /**
     * Gets a menu item selected at the specified point.
     * @param point Point at which to find a menu item.
     * @return Menu item selected, or null if a menu item does not exist at the point.
     */
    private MenuItem getMenuItemSelected(PointF point) {
        if (!mMenuIsOpen) {
            return null;
        }

        MenuItem menuItem = null;

        if (mMenuIsOpen) {
            menuItem = mMenu.getItemAt(point);
        }

        return menuItem;
    }

    private void initializeSelectionMenu() {
        // Initialize selector menu.
        mMenu = new Menu();

        Resources resources = GeeNotesApplication.getContext().getResources();

        // Menu item: Cancel.
        Bitmap cancelBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_selector_menu_cancel);
        mMenu.addItem(new MenuItem(MenuItemType.MENU_ITEM_TYPE_CANCEL, cancelBitmap), MenuAlignType.MENU_ALIGN_RIGHT);

        // Menu item: Move.
        Bitmap moveBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_selector_menu_move);
        mMenu.addItem(new MenuItem(MenuItemType.MENU_ITEM_TYPE_MOVE, moveBitmap), MenuAlignType.MENU_ALIGN_RIGHT);

        // Menu item: Delete.
        Bitmap deleteBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_selector_menu_delete);
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
