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

public class FrequencyGenerator extends View {

    final private int bodyRes = R.drawable.rounded_rect_light;
    final private int displayRes = R.drawable.rounded_rect_dark;
    final private int controlRes = R.drawable.control_wheel;

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

    private float frequencyTextSize = 0.0f;
    private int frequencyTextColor = Color.GREEN;
    private float frequencyTextX, frequencyTextY = 0.0f;

    private float headerTextSize = 0.0f;
    private int headerTextColor = Color.WHITE;
    private float headerTextX, headerTextY = 0.0f;

    private int frequency;
    private int frequencyMAX;
    // Divide 270 degrees / frequencyMax to convert frequency to degress to rotate control wheel
    // 270 degrees is end position of control wheel
    private float frequencyToDegreesRate = 0;
    final private int FREQUENCY_MAX_DEFAULT = 20000;
    final private int CONTROL_WHEEL_END_POSITION = 270;

    public FrequencyGenerator(Context context) {
        super(context);
        init();
    }

    public FrequencyGenerator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrequencyGenerator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FrequencyGenerator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        frequency = 0;
        setFrequencyMAX(FREQUENCY_MAX_DEFAULT);

        body = getContext().getDrawable(bodyRes);
        display = getContext().getDrawable(displayRes);
        frequencyTextColor = getContext().getColor(R.color.colorFourV1);
        control = BitmapFactory.decodeResource(getContext().getResources(), controlRes);
        controlMatrix = new Matrix();
        paint = new Paint();
        paint.setAntiAlias(true);

        // As view measures will be done
        this.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){
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
                Math.round(getWidth() * (5/100.0f)),
                Math.round(getHeight() * (25/100.0f)),
                Math.round(getWidth() * (60/100.0f)),
                Math.round(getHeight() - (getHeight() * (10/100.0f))));

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
        controlWidth = controlHeight = Math.round(minBodyDimension * (50/100.0f));
        int controlX = Math.round(
                body.getBounds().width() - controlWidth - (body.getBounds().width() * (10/100.0f)));
        int controlY = (display.getBounds().top + display.getBounds().height()) - controlHeight;
        controlRect.set(controlX, controlY, controlWidth+controlX,
                controlHeight+controlY);

        // Calculate text inner offsets and size
        int minDispDimension = Math.min(display.getBounds().width(), display.getBounds().height());
        frequencyTextSize = minDispDimension * (50/100.0f);
        frequencyTextX = display.getBounds().width();
        frequencyTextY = display.getBounds().top + display.getBounds().height() -
                (display.getBounds().height() * (20/100.0f));

        headerTextSize = display.getBounds().top * (50/100.0f);
        headerTextX = display.getBounds().left;
        headerTextY = display.getBounds().top - headerTextSize / 2;
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

        // Draw generator body
        body.draw(canvas);
        display.draw(canvas);

        // Draw control wheel
        controlMatrix.reset();
        controlMatrix.setRotate(frequency * frequencyToDegreesRate,
                controlPivotX, controlPivotY);
        controlMatrix.postTranslate(controlInnerOffsetX, controlInnerOffsetY);
        controlRotatedCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        controlRotatedCanvas.drawBitmap(control, controlMatrix, null);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(frequencyTextSize);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setColor(frequencyTextColor);
        canvas.drawText(String.valueOf(this.frequency), frequencyTextX, frequencyTextY, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(headerTextColor);
        paint.setTextSize(headerTextSize);
        canvas.drawText(getContext().getString(R.string.generator),
                headerTextX, headerTextY, paint);

        /*
        Draw control wheel bounds rectangle
        Uncomment lines below to debug

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        controlRotatedCanvas.drawRect(0, 0, controlRotatedBitmap.getWidth()-1,
                controlRotatedBitmap.getHeight()-1, paint);

         */

        canvas.drawBitmap(controlRotatedBitmap, null, controlRect, null);
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        if (frequency > frequencyMAX)
            return;

        this.frequency = frequency;
        invalidate();
    }

    public int getFrequencyMAX() {
        return frequencyMAX;
    }

    public void setFrequencyMAX(int frequencyMAX) {
        this.frequencyMAX = frequencyMAX;

        if (this.frequencyMAX > 0) {
            frequencyToDegreesRate = (float) CONTROL_WHEEL_END_POSITION / (float) this.frequencyMAX;
        }

        if (frequency > this.frequencyMAX) {
            setFrequency(this.frequencyMAX);
        }
    }
}
