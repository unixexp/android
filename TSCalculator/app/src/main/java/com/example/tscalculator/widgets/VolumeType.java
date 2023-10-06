package com.example.tscalculator.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.tscalculator.R;
import com.example.tscalculator.ts.Driver;

public class VolumeType extends View {

    private Drawable CLOSED_BOX_DRAWABLE =
            getContext().getDrawable(R.drawable.closed_box);
    private Drawable VENTED_BOX_DRAWABLE =
            getContext().getDrawable(R.drawable.vented_box);
    private Drawable CLOSED_OR_VENTED_BOX_DRAWABLE =
            getContext().getDrawable(R.drawable.closed_or_vented_box);
    private Drawable UNKNOWN_BOX_TYPE_DRAWABLE =
            getContext().getDrawable(R.drawable.unknown);

    private Drawable drawable = null;

    private int volumeType = Driver.VT_NONE;

    public VolumeType(Context context) {
        super(context);
        init();
    }

    public VolumeType(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VolumeType(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VolumeType(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        this.addOnLayoutChangeListener(new OnLayoutChangeListener(){
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                configureDrawBounds();
            }});
    }

    private void configureDrawBounds() {
        final float unknownBoxTypeDrawableRatio = getDrawableRatio(UNKNOWN_BOX_TYPE_DRAWABLE);
        final float closedBoxDrawableRatio = getDrawableRatio(CLOSED_BOX_DRAWABLE);
        final float ventedBoxDrawableRatio = getDrawableRatio(VENTED_BOX_DRAWABLE);
        final float closedOrVentedBoxDrawableRatio = getDrawableRatio(CLOSED_OR_VENTED_BOX_DRAWABLE);

        final int unknownBoxTypeDrawableWidth = Math.round(getHeight()*unknownBoxTypeDrawableRatio);
        final int closedBoxDrawableWidth = Math.round(getHeight()*closedBoxDrawableRatio);
        final int ventedBoxDrawableWidth = Math.round(getHeight()*ventedBoxDrawableRatio);
        final int closedOrVentedBoxDrawableWidth = Math.round(getHeight()*closedOrVentedBoxDrawableRatio);

        final int unknownBoxTypeDrawableX = Math.round((getWidth()-unknownBoxTypeDrawableWidth)/2);
        final int closedBoxDrawableX = Math.round((getWidth()-closedBoxDrawableWidth)/2);
        final int ventedBoxDrawableX = Math.round((getWidth()-ventedBoxDrawableWidth)/2);
        final int closedOrVentedBoxDrawableX = Math.round((getWidth()-closedOrVentedBoxDrawableWidth)/2);

        UNKNOWN_BOX_TYPE_DRAWABLE.setBounds(unknownBoxTypeDrawableX, 0,
                getWidth()-unknownBoxTypeDrawableX, getHeight());
        CLOSED_BOX_DRAWABLE.setBounds(closedBoxDrawableX, 0,
                getWidth()-closedBoxDrawableX, getHeight());
        VENTED_BOX_DRAWABLE.setBounds(ventedBoxDrawableX, 0,
                getWidth()-ventedBoxDrawableX, getHeight());
        CLOSED_OR_VENTED_BOX_DRAWABLE.setBounds(closedOrVentedBoxDrawableX, 0,
                getWidth()-closedOrVentedBoxDrawableX, getHeight());
    }

    private float getDrawableRatio(Drawable drawable) {
        final float w = drawable.getIntrinsicWidth();
        final float h = drawable.getIntrinsicHeight();
        try {
            return w / h;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        if (volumeType == Driver.VT_NONE) {
            UNKNOWN_BOX_TYPE_DRAWABLE.draw(canvas);
            return;
        }

        if (volumeType == Driver.VT_CLOSED_BOX) {
            CLOSED_BOX_DRAWABLE.draw(canvas);
            return;
        }

        if (volumeType == Driver.VT_VENTED_BOX) {
            VENTED_BOX_DRAWABLE.draw(canvas);
            return;
        }

        if (volumeType == Driver.VT_CLOSED_OR_VENTED_BOX) {
            CLOSED_OR_VENTED_BOX_DRAWABLE.draw(canvas);
            return;
        }

    }

    public void setVolumeType(int vType) {
        volumeType = vType;
        invalidate();
    }

}
