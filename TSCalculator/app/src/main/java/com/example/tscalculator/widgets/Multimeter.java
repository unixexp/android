package com.example.tscalculator.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.tscalculator.R;

public class Multimeter extends View {

    final private int bodyRes = R.drawable.rounded_rect_light;
    final private int displayRes = R.drawable.rounded_rect_dark;
    final private int controlRes = R.drawable.control_wheel_mm;

    private Paint paint = null;
    private Drawable body = null;
    private Drawable display = null;
    private int bodyWidth;
    private int bodyHeight;

    private Bitmap control = null;
    private Bitmap controlRotatedBitmap = null;
    private Canvas controlRotatedCanvas = null;
    private Rect controlRect = null;
    private Matrix controlMatrix = null;
    private float controlPivotX, controlPivotY = 0;
    private float controlInnerOffsetX, controlInnerOffsetY = 0.0f;

    private float value;
    private float valueMAX;
    final private float VALUE_MAX_DEFAULT = 2.0f;
    final private float INFINITY_VALUE = 9999999.0f;

    private float valueTextSize = 0.0f;
    private int valueTextColor = Color.GREEN;
    private float valueTextX, valueTextY = 0.0f;

    private float legendTextSize = 0.0f;
    private int legendTextColor = Color.WHITE;

    private int position;
    private int positionMAX;
    // Divide 270 degrees / frequencyMax to convert frequency to degress to rotate control wheel
    // 270 degrees is end position of control wheel
    private float positionToDegreesRate = 0;
    final private int POSITION_MAX_DEFAULT = 21;
    final private int CONTROL_WHEEL_END_POSITION = 270;

    public Multimeter(Context context) {
        super(context);
        init();
    }

    public Multimeter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Multimeter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Multimeter(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        value = 0.0f;
        setValueMAX(VALUE_MAX_DEFAULT);

        position = 0;
        setPositionMAX(POSITION_MAX_DEFAULT);

        body = getContext().getDrawable(bodyRes);
        display = getContext().getDrawable(displayRes);
        valueTextColor = getContext().getColor(R.color.colorFourV1);
        control = BitmapFactory.decodeResource(getContext().getResources(), controlRes);
        controlMatrix = new Matrix();
        paint = new Paint();
        paint.setAntiAlias(true);

        // As view measures will be done
        this.addOnLayoutChangeListener(new OnLayoutChangeListener(){
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                configureDrawBounds();
            }});
    }

    private void configureDrawBounds() {
        if (body == null || control == null) {
            return;
        }

        body.setBounds(0, 0, getWidth(), getHeight());
        display.setBounds(
                Math.round(getWidth() * (10/100.0f)),
                Math.round(getHeight() * (10/100.0f)),
                Math.round(getWidth() - (getWidth() * (10/100.0f))),
                Math.round(getHeight() - (getHeight() * (60/100.0f))));

        // Calculate control wheel center and max bounds during rotation
        controlPivotX = control.getWidth() / 2;
        controlPivotY = control.getHeight() / 2;
        controlMatrix.reset();
        // Max bounds to be after rotate control wheel to 45 degrees
        controlMatrix.setRotate(45.0f, controlPivotX, controlPivotY);
        // Calculate difference between original control wheel and rotated
        RectF originalControlRect = new RectF(0, 0,
                control.getWidth(), control.getHeight());
        RectF rotatedControlRect = new RectF();
        controlMatrix.mapRect(rotatedControlRect, originalControlRect);
        // Calculate control wheel inner offsets
        //controlInnerOffsetX = (rotatedControlRect.width() - originalControlRect.width()) / 2;
        //controlInnerOffsetY = (rotatedControlRect.height() - originalControlRect.height()) / 2;
        // Create final created canvas for draw rotated control wheel
        controlRotatedBitmap = Bitmap.createBitmap(Math.round(originalControlRect.width()),
                Math.round(originalControlRect.height()), Bitmap.Config.ARGB_8888);
        controlRotatedCanvas = new Canvas(controlRotatedBitmap);
        // Calculate control wheel offset and size inside body
        controlRect = new Rect();
        int controlWidth;
        int controlHeight;
        int minBodyDimension = Math.min(body.getBounds().width(), body.getBounds().height());
        controlWidth = controlHeight =
                Math.round((getHeight() - display.getBounds().height() -
                        display.getBounds().top) * (60/100.0f));
        int controlX = Math.round((getWidth() - controlWidth) / 2.0f);
        int controlY = getHeight() - (getHeight() - display.getBounds().bottom) +
            Math.round((getHeight() - display.getBounds().bottom - controlHeight) / 2.0f);
        controlRect.set(controlX, controlY, controlWidth+controlX,
                controlHeight+controlY);

        // Calculate text inner offsets and size
        int minDispDimension = Math.min(display.getBounds().width(), display.getBounds().height());
        valueTextSize = display.getBounds().height() * (45/100.0f);
        valueTextX = display.getBounds().width();
        valueTextY = display.getBounds().top + display.getBounds().height() -
                (display.getBounds().height() * (20/100.0f));

        // Calculate multimeter legend around control wheel
        legendTextSize = display.getBounds().height() * (35/100.0f);
    }

    private float [] getLegendXY(float angle, float marginX, float marginY) {
        float result[] = new float[2];
        float angleRadians = (float) (angle * Math.PI / 180);
        float r = controlRect.width() / 2.0f;
        float centerX = (controlRect.left + controlRect.right) / 2.0f;
        float centerY = (controlRect.top + controlRect.bottom) / 2.0f;
        result[0] = (float) (centerX + r * Math.cos(angleRadians));
        result[1] = (float) (centerY + r * Math.sin(angleRadians));

        if (marginX > 0)
            result[0] = result[0] * marginX;

        if (marginY > 0)
            result[1] = result[1] * marginY;

        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w;
        int h;

        // Desired aspect ratio of the view's contents (not including padding)
        float desiredAspect = 0.0f;

        // We are allowed to change the view's width
        boolean resizeWidth = false;

        // We are allowed to change the view's height
        boolean resizeHeight = false;

        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        if (body == null) {
            bodyWidth = 0;
            bodyHeight = 0;
            w = h = 0;
        } else {
            w = bodyWidth = body.getIntrinsicWidth();
            h = bodyHeight = body.getIntrinsicHeight();
            if (w <= 0) w = 1;
            if (h <= 0) h = 1;

            resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;
            resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;
            desiredAspect = (float) w / (float) h;
        }

        int widthSize;
        int heightSize;

        if (resizeWidth || resizeHeight) {
            /* If we get here, it means we want to resize to match the
                drawables aspect ratio, and we have the freedom to change at
                least one dimension.
            */

            // Get the max possible width given our constraints
            widthSize = resolveAdjustedSize(w, Integer.MAX_VALUE, widthMeasureSpec);

            // Get the max possible height given our constraints
            heightSize = resolveAdjustedSize(h, Integer.MAX_VALUE, heightMeasureSpec);

            if (desiredAspect != 0.0f) {
                // See what our actual aspect ratio is
                final float actualAspect = (float)(widthSize) / (heightSize);

                if (Math.abs(actualAspect - desiredAspect) > 0.0000001) {

                    boolean done = false;

                    // Try adjusting width to be proportional to height
                    if (resizeWidth) {
                        int newWidth = (int)(desiredAspect * (heightSize));

                        // Allow the width to outgrow its original estimate if height is fixed.
                        if (!resizeHeight) {
                            widthSize = resolveAdjustedSize(newWidth, Integer.MAX_VALUE, widthMeasureSpec);
                        }

                        if (newWidth <= widthSize) {
                            widthSize = newWidth;
                            done = true;
                        }
                    }

                    // Try adjusting height to be proportional to width
                    if (!done && resizeHeight) {
                        int newHeight = (int)((widthSize) / desiredAspect);

                        // Allow the height to outgrow its original estimate if width is fixed.
                        if (!resizeWidth) {
                            heightSize = resolveAdjustedSize(newHeight, Integer.MAX_VALUE,
                                    heightMeasureSpec);
                        }

                        if (newHeight <= heightSize) {
                            heightSize = newHeight;
                        }
                    }
                }
            }
        } else {
            w = Math.max(w, getSuggestedMinimumWidth());
            h = Math.max(h, getSuggestedMinimumHeight());

            widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
            heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    private int resolveAdjustedSize(int desiredSize, int maxSize, int measureSpec) {
        int result = desiredSize;
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize =  MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                /* Parent says we can be as big as we want. Just don't be larger
                   than max size imposed on ourselves.
                */
                result = Math.min(desiredSize, maxSize);
                break;
            case MeasureSpec.AT_MOST:
                // Parent says we can be as big as we want, up to specSize.
                // Don't be larger than specSize, and don't be larger than
                // the max size imposed on ourselves.
                result = Math.min(Math.min(desiredSize, specSize), maxSize);
                break;
            case MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        if (body == null || control == null ||
                bodyWidth == 0 || bodyHeight == 0 ||
                control.getWidth() == 0 || control.getHeight() == 0) {
            return;
        }

        // Draw multimeter body
        body.draw(canvas);
        display.draw(canvas);

        // Draw control wheel
        controlMatrix.reset();
        controlMatrix.setRotate(position * positionToDegreesRate,
                controlPivotX, controlPivotY);
        controlMatrix.postTranslate(controlInnerOffsetX, controlInnerOffsetY);
        controlRotatedCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        controlRotatedCanvas.drawBitmap(control, controlMatrix, null);

        // Draw value text
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(valueTextSize);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setColor(valueTextColor);
        if (this.value == INFINITY_VALUE) {
            canvas.drawText(String.valueOf(1), valueTextX, valueTextY, paint);
        } else {
            canvas.drawText(String.format("%.3f", this.value).replace(',', '.'),
                    valueTextX, valueTextY, paint);
        }

        canvas.drawBitmap(controlRotatedBitmap, null, controlRect, null);

        // Draw legend around control wheel
        float legendXY[];
        paint.setColor(legendTextColor);
        paint.setTextSize(legendTextSize);
        paint.setTypeface(Typeface.MONOSPACE);

        paint.setTextAlign(Paint.Align.RIGHT);
        legendXY = getLegendXY(230, 0, 0);
        canvas.drawText(String.valueOf("A~"), legendXY[0], legendXY[1], paint);

        paint.setTextAlign(Paint.Align.LEFT);
        legendXY = getLegendXY(310, 1.05f, 0);
        canvas.drawText(String.valueOf("A-"), legendXY[0], legendXY[1], paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        legendXY = getLegendXY(130, 0, 1.1f);
        canvas.drawText(String.valueOf("V~"), legendXY[0], legendXY[1], paint);

        paint.setTextAlign(Paint.Align.LEFT);
        legendXY = getLegendXY(80, 0, 1.1f);
        canvas.drawText(String.valueOf("V-"), legendXY[0], legendXY[1], paint);

        paint.setTextAlign(Paint.Align.LEFT);
        legendXY = getLegendXY(30, 1.05f, 0);
        canvas.drawText(String.valueOf("Ω"), legendXY[0], legendXY[1], paint);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        if (position > positionMAX)
            return;

        this.position = position;
        invalidate();
    }

    public int getPositionMAX() {
        return positionMAX;
    }

    public void setPositionMAX(int positionMAX) {
        this.positionMAX = positionMAX;

        if (this.positionMAX > 0) {
            positionToDegreesRate = (float) CONTROL_WHEEL_END_POSITION / (float) this.positionMAX;
        }

        if (position > this.positionMAX) {
            setPosition(this.positionMAX);
        }
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;

        if (this.value > this.valueMAX) {
            this.value = INFINITY_VALUE;
        }

        invalidate();
    }

    public float getValueMAX() {
        return valueMAX;
    }

    public void setValueMAX(float valueMAX) {
        this.valueMAX = valueMAX;

        if (this.value > this.valueMAX) {
            this.value = INFINITY_VALUE;
            invalidate();
        }
    }

}
