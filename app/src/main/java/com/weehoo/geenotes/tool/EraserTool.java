package com.weehoo.geenotes.tool;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;

import com.weehoo.geenotes.R;
import com.weehoo.geenotes.canvas.CanvasView;

public class EraserTool extends PenTool
                        implements ITool {

    private Paint mPaint;

    public EraserTool() {
        // Eraser paint.
        mPaint = new Paint();
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mPaint.setStrokeWidth(10);
    }

    /**
     * Called when the tool is selected as the primary drawing tool.
     *
     * @param canvasView
     */
    @Override
    public void onSelect(CanvasView canvasView) {
        super.onSelect(canvasView);
        super.mPaint = this.mPaint;
    }

    /**
     * Get the tool's active icon.
     *
     * @return This tool's active icon res.
     */
    @Override
    public int getIconResActive() {
        return R.drawable.ic_tool_menu_eraser_active;
    }

    /**
     * Get the tool's inactive icon.
     *
     * @return This tool's inactive icon res.
     */
    @Override
    public int getIconResInactive() {
        return R.drawable.ic_tool_menu_eraser_inactive;
    }
}
