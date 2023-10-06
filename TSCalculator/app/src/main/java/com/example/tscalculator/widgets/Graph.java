package com.example.tscalculator.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.tscalculator.components.FixTimeStepAnimator;
import com.example.tscalculator.components.NumbersGenerator;

import java.util.ArrayList;
import java.util.List;

public class Graph extends View {

    final private String DEFAULT_BACKGROUND_COLOR = "#ffffff";
    final private String DEFAULT_GRAPH_BACKGROUND_COLOR = "#dbeedb";
    final private String DEFAULT_GRAPH_COLOR = "#000000";
    final private String DEFAULT_GRAPH_GRID_COLOR = "#555555";
    final private String DEFAULT_GRAPH_OUTER_STROKE_COLOR = "#555555";
    final private String DEFAULT_LEGEND_TEXT_COLOR = "#222222";
    final private String DEFAULT_LEGEND_X_LABEL_COLOR = "#0000ff";
    final private String DEFAULT_LEGEND_Y_LABEL_COLOR = "#ff0000";
    final private String DEFAULT_GRAPH_AXIS_COLOR = "#222222";
    final private String DEFAULT_GRAPH_MARKPOINT_COLOR = "#ff0000";
    final private String DEFAULT_GRAPH_MARKPOINT_LABEL_COLOR = "#ffffff";
    final private String DEFAULT_GRAPH_MARKPOINT_LABEL_BGCOLOR = "#0000ff";

    final private float DEFAULT_GRAPH_LINE_WIDTH = 1.5f;
    final private float DEFAULT_GRAPH_STROKE_WIDTH = 1.0f;

    final private int DEFAULT_GRAPH_GRID_X_DENSITY_PERCENT = 100;
    final private int DEFAULT_GRAPH_GRID_Y_GRADUATION_COUNT = 3;

    final private boolean DEFAULT_LEGEND_X_VALUE_TO_INT = false;
    final private boolean DEFAULT_LEGEND_Y_VALUE_TO_INT = false;

    final private String DEFAULT_LEGEND_X_VALUE_FORMAT = "%.2f";
    final private String DEFAULT_LEGEND_Y_VALUE_FORMAT = "%.2f";

    final private int DEFAULT_LEGEND_X_SIZE_PERCENT = 25;
    final private int DEFAULT_LEGEND_Y_SIZE_PERCENT = 15;

    final private int DEFAULT_INTERMEDIATES = 1;
    final private float DEFAULT_UPPER_Y_LIMIT = 0;
    final private float DEFAULT_LEGEND_TEXT_SIZE_PERCENT = 1.97f;
    final private float DEFAULT_LEGEND_AXIS_LABEL_TEXT_SIZE_PERCENT = 1.97f;
    final private float DEFAULT_GRAPH_MARKPOINT_LABEL_TEXT_SIZE_PERCENT = 1.97f;

    final private boolean DEFAULT_START_ANIMATED = false;
    final private float DEFAULT_ANIMATION_INTERVAL = 0.02f;

    private Bitmap graphBitmap;
    private Canvas graphCanvas;
    private Rect graphRect;

    private Bitmap graphXLegendBitmap;
    private Canvas graphXLegendCanvas;
    private Rect graphXLegendRect;

    private Bitmap graphYLegendBitmap;
    private Canvas graphYLegendCanvas;
    private Rect graphYLegendRect;

    private Paint paint;

    private int graphBackgroundColor;
    private int graphColor;
    private int graphGridColor;
    private int graphOuterStrokeColor;
    private int graphLegendTextColor;
    private int graphLegendXLabelColor;
    private int graphLegendYLabelColor;
    private int graphAxisColor;
    private int graphMarkPointColor;
    private int graphMarkPointLabelColor;
    private int graphMarkPointLabelBackgroundColor;

    private float graphLineWidth;
    private float graphStrokeWidth;
    private float graphMarkPointWidth;
    private float graphMarkPointLabelSize;

    private int legendXSizePercent;
    private int legendYSizePercent;

    private float legendXTextSize;
    private float legendYTextSize;

    private float legendXLabelTextSize;
    private float legendYLabelTextSize;

    private int graphGridXDensityPercent;
    private int graphGridYGraduationCount;

    private boolean legendXValueToInt;
    private boolean legendYValueToInt;

    private String legendXValueFormat;
    private String legendYValueFormat;

    private int intermediates;

    private float graphMarkPointLabelSizePercent;
    private float legendXTextSizePercent;
    private float legendYTextSizePercent;
    private float legendXLabelTextSizePercent;
    private float legendYLabelTextSizePercent;

    private boolean animated;
    private float animationInterval;
    private boolean animationDone;
    private boolean animating;
    private FixTimeStepAnimator animator;

    private String legendXLabel;
    private String legendYLabel;
    private float upperYLimit;

    private float graphXStep;
    private int graphGridXStep;
    private int graphGridYStep;

    private String legendXValue;
    private String legendYValue;

    private float[] dataX;
    private float[] dataY;
    private float[] gridY;

    private List<MarkPoint> markPointsY;

    private boolean prepared = false;

    private float x, y, y1, y2, yScaleToUpperLimitY;

    GraphAnimatorListener graphAnimatorListener;


    private class MarkPoint {
        String label;
        float value;
        float x;
        float y;

        public MarkPoint(String label, float value) {
            this.label = label;
            this.value = value;
            this.x = x;
            this.y = y;
        }

        public String getLabel() {
            return label;
        }

        public float getValue() {
            return value;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public void setX(float x) {
            this.x = x;
        }

        public void setY(float y) {
            this.y = y;
        }
    }

    public interface GraphAnimatorListener {
        void onAnimationUpdate(int value);
        void onAnimationStart(int value);
        void onAnimationEnd(int value);
    }

    public Graph(Context context) {
        super(context);
        init();
    }

    public Graph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Graph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Graph(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(graphStrokeWidth);

        setGraphBackgroundColor(DEFAULT_GRAPH_BACKGROUND_COLOR);
        setGraphColor(DEFAULT_GRAPH_COLOR);
        setGraphGridColor(DEFAULT_GRAPH_GRID_COLOR);
        setGraphOuterStrokeColor(DEFAULT_GRAPH_OUTER_STROKE_COLOR);
        setGraphLegendTextColor(DEFAULT_LEGEND_TEXT_COLOR);
        setGraphLegendXLabelColor(DEFAULT_LEGEND_X_LABEL_COLOR);
        setGraphLegendYLabelColor(DEFAULT_LEGEND_Y_LABEL_COLOR);
        setGraphAxisColor(DEFAULT_GRAPH_AXIS_COLOR);
        setGraphMarkPointColor(DEFAULT_GRAPH_MARKPOINT_COLOR);
        setGraphLineWidth(DEFAULT_GRAPH_LINE_WIDTH);
        setGraphStrokeWidth(DEFAULT_GRAPH_STROKE_WIDTH);
        setGraphMarkPointLabelColor(DEFAULT_GRAPH_MARKPOINT_LABEL_COLOR);
        setGraphMarkPointLabelBackgroundColor(DEFAULT_GRAPH_MARKPOINT_LABEL_BGCOLOR);

        graphGridXDensityPercent = DEFAULT_GRAPH_GRID_X_DENSITY_PERCENT;
        graphGridYGraduationCount = DEFAULT_GRAPH_GRID_Y_GRADUATION_COUNT;

        intermediates = DEFAULT_INTERMEDIATES;
        upperYLimit = DEFAULT_UPPER_Y_LIMIT;

        legendXValueToInt = DEFAULT_LEGEND_X_VALUE_TO_INT;
        legendYValueToInt = DEFAULT_LEGEND_Y_VALUE_TO_INT;

        legendXValueFormat = DEFAULT_LEGEND_X_VALUE_FORMAT;
        legendYValueFormat = DEFAULT_LEGEND_Y_VALUE_FORMAT;

        legendXSizePercent = DEFAULT_LEGEND_X_SIZE_PERCENT;
        legendYSizePercent = DEFAULT_LEGEND_Y_SIZE_PERCENT;

        legendXTextSizePercent = DEFAULT_LEGEND_TEXT_SIZE_PERCENT;
        legendYTextSizePercent = DEFAULT_LEGEND_TEXT_SIZE_PERCENT;

        legendXLabelTextSizePercent = DEFAULT_LEGEND_AXIS_LABEL_TEXT_SIZE_PERCENT;
        legendYLabelTextSizePercent = DEFAULT_LEGEND_AXIS_LABEL_TEXT_SIZE_PERCENT;

        graphMarkPointLabelSizePercent = DEFAULT_GRAPH_MARKPOINT_LABEL_TEXT_SIZE_PERCENT;

        animator = new FixTimeStepAnimator();
        animated = DEFAULT_START_ANIMATED;
        setAnimationInterval(DEFAULT_ANIMATION_INTERVAL);

        dataX = new float[0];
        dataY = new float[0];
        graphXStep = 0.0f;
        legendXLabel = "X";
        legendYLabel = "Y";
        markPointsY = new ArrayList<>();

        if (graphMarkPointWidth == 0)
            graphMarkPointWidth = graphLineWidth * 7;

        // As view measures will be done
        this.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // Sto animation and call prepare
                stopAnimation();
            }});
    }

    private void prepare() {
        prepared = false;
        animationDone = false;
        animating = false;

        if (getWidth() == 0 || getHeight() == 0 || dataX.length == 0 || dataY.length == 0)
            return;

        int legendXHeight = Math.round(getHeight() * (legendXSizePercent / 100.0f));
        int legendYWidth = Math.round(getWidth() * (legendYSizePercent / 100.0f));

        int graphHeight = getHeight() - legendXHeight;
        int graphLeft = legendYWidth;

        graphRect = new Rect();
        graphRect.set(graphLeft, 0, getWidth(), graphHeight);
        graphBitmap = Bitmap.createBitmap(graphRect.width(), graphRect.height(),
                Bitmap.Config.ARGB_8888);
        graphCanvas = new Canvas(graphBitmap);

        // Bounds of X-legend
        graphXLegendRect = new Rect();
        graphXLegendRect.set(graphRect.left, 0, graphRect.right,
                getHeight() - graphHeight);
        graphXLegendBitmap = Bitmap.createBitmap(graphXLegendRect.width(),
                graphXLegendRect.height(), Bitmap.Config.ARGB_8888);
        graphXLegendCanvas = new Canvas(graphXLegendBitmap);
        graphXStep = (float) graphRect.width() / (float) dataX.length;
        graphGridXStep = Math.round((dataX.length-1) /
                ((dataX.length-1) * (graphGridXDensityPercent / 100.0f)));

        // Bounds of Y-legend
        graphYLegendRect = new Rect();
        graphYLegendRect.set(0, 0, getWidth() - graphRect.width(), getHeight());
        graphYLegendBitmap = Bitmap.createBitmap(graphYLegendRect.width(),
                graphYLegendRect.height(), Bitmap.Config.ARGB_8888);
        graphYLegendCanvas = new Canvas(graphYLegendBitmap);

        gridY = NumbersGenerator.getNumList(0.0f, upperYLimit, graphGridYGraduationCount);
        graphGridYStep = Math.round((float) getHeight() / (float) gridY.length);

        x = 0.0f;
        y = 0.0f;
        y1 = 0.0f;
        y2 = graphBitmap.getHeight();
        if (upperYLimit > 0)
            yScaleToUpperLimitY = y2 / upperYLimit;
        else
            yScaleToUpperLimitY = y2;

        int maxDimension = Math.max(getWidth(), getHeight());
        if (legendXTextSize == 0)
            legendXTextSize = maxDimension * (legendXTextSizePercent / 100.0f);

        if (legendYTextSize == 0)
            legendYTextSize = maxDimension * (legendYTextSizePercent / 100.0f);

        if (legendXLabelTextSize == 0)
            legendXLabelTextSize = maxDimension * (legendXLabelTextSizePercent / 100.0f);

        if (legendYLabelTextSize == 0)
            legendYLabelTextSize = maxDimension * (legendYLabelTextSizePercent / 100.0f);

        if (graphMarkPointLabelSize == 0)
            graphMarkPointLabelSize = maxDimension * (graphMarkPointLabelSizePercent / 100.0f);

        animator.setFixTimeStepAnimatorListener(new FixTimeStepAnimator.FixTimeStepAnimatorListener() {
            @Override
            public void onAnimationUpdate(int value) {
                if (graphAnimatorListener != null)
                    graphAnimatorListener.onAnimationUpdate(value);

                drawGraphPoint(value);
                invalidate();
            }

            @Override
            public void onAnimationStart(int value) {
                if (graphAnimatorListener != null)
                    graphAnimatorListener.onAnimationStart(value);

                animationDone = false;
                animating = true;
            }

            @Override
            public void onAnimationEnd(int value) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            drawMarkPoints();
                            invalidate();
                        } catch (InterruptedException e) {}
                    }
                }).start();

                animationDone = true;
                animated = false;
                animating = false;

                if (graphAnimatorListener != null)
                    graphAnimatorListener.onAnimationEnd(value);
            }
        });

        // Prepare mark points
        for (MarkPoint markPoint : markPointsY) {
            float markX = 0.0f;
            float markY;
            for (float value : dataY) {
                markY = value * yScaleToUpperLimitY;
                markY = (y2 + ((markY - y1) * (y1 - y2) / (y2 - y1)));
                if (markPoint.getValue() == value) {
                    markPoint.setX(markX);
                    markPoint.setY(markY);
                    break;
                }
                markX += graphXStep;
            }
        }

        prepared = true;
    }

    private void drawBackplane() {
        if (!prepared)
            return;

        final Rect textBounds = new Rect();
        int graphGridXStep1 = graphGridXStep;

        x = 0.0f;
        y = 0.0f;

        // Pre-draw X-grid
        paint.setTextAlign (Paint.Align.CENTER);
        paint.setTextSize (legendXTextSize);
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setColor(graphBackgroundColor);
        graphCanvas.drawColor(graphBackgroundColor);
        for (int i = 0; i < dataX.length-1; i++) {
            x += graphXStep;

            if (i == graphGridXStep1) {
                paint.setColor(graphGridColor);
                graphCanvas.drawLine(x, y1, x, y2, paint);
                graphGridXStep1 += graphGridXStep;

                paint.setColor(graphLegendTextColor);
                if (legendXValueToInt)
                    legendXValue = String.valueOf((int) dataX[i]);
                else
                    legendXValue = String.format(legendXValueFormat, dataX[i]);

                paint.getTextBounds(legendXValue, 0, legendXValue.length(), textBounds);
                graphXLegendCanvas.drawText(legendXValue, x,
                        (graphXLegendRect.height() / 2) - textBounds.exactCenterY(), paint);
            }
        }
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(graphLegendXLabelColor);
        paint.setFakeBoldText(true);
        paint.getTextBounds(legendXLabel, 0, legendXLabel.length(), textBounds);
        graphXLegendCanvas.drawText (
                legendXLabel,
                graphXLegendRect.width()-(legendXLabelTextSize / 2),
                (graphXLegendRect.height() / 2) - textBounds.exactCenterY(),
                paint);

        x = 0.0f;

        // Pre-draw Y-grid
        paint.setFakeBoldText(false);
        paint.setTextAlign (Paint.Align.CENTER);
        paint.setTextSize (legendYTextSize);
        paint.setTypeface(Typeface.MONOSPACE);
        for (int j = 1; j < gridY.length-1; j++) {
            paint.setColor(graphGridColor);
            y = gridY[j] * yScaleToUpperLimitY;
            y = (y2 + ((y-y1)*(y1-y2)/(y2-y1)));
            graphCanvas.drawLine(x, y, graphRect.width(), y, paint);

            paint.setColor(graphLegendTextColor);
            if (j < gridY.length-2) {
                if (legendYValueToInt)
                    legendYValue = String.valueOf((int) gridY[j]);
                else
                    legendYValue = String.format(legendYValueFormat, gridY[j]);
                paint.getTextBounds(legendYValue, 0, legendYValue.length(), textBounds);
                graphYLegendCanvas.drawText (legendYValue,
                        (graphYLegendRect.width() / 2),
                        y  - textBounds.exactCenterY(), paint);
            } else {
                paint.setFakeBoldText(true);
                paint.setColor(graphLegendYLabelColor);
                paint.getTextBounds(legendYValue, 0, legendYValue.length(), textBounds);
                graphYLegendCanvas.drawText (legendYLabel,
                        (graphYLegendRect.width() / 2),
                        y  - textBounds.exactCenterY(), paint);
                paint.setFakeBoldText(false);
            }
        }

        paint.setColor(graphAxisColor);
        graphCanvas.drawLine(0, graphRect.height(), graphRect.width(),
                graphRect.height(), paint);
        graphCanvas.drawLine(0, 0, 0, graphRect.height(), paint);

        y = 0.0f;
    }

    private void drawGraphPoint(int dataXValue) {
        if (!prepared)
            return;

        final float intermediates[] = NumbersGenerator.getNumList(dataY[dataXValue],
                dataY[dataXValue+1], this.intermediates);
        for (float j : intermediates) {
            y = j * yScaleToUpperLimitY;
            y = (y2 + ((y-y1)*(y1-y2)/(y2-y1)));
            paint.setColor(graphColor);
            graphCanvas.drawCircle(x, y, graphLineWidth, paint);

            x += graphXStep / this.intermediates;
        }
    }

    private void drawMarkPoints() {
        if (!prepared)
            return;

        paint.setTextAlign (Paint.Align.LEFT);
        paint.setTextSize (graphMarkPointLabelSize);
        paint.setTypeface(Typeface.MONOSPACE);
        final Rect textBounds = new Rect();

        for (MarkPoint markPoint : markPointsY) {
            String label = markPoint.getLabel();
            paint.getTextBounds(label, 0, label.length(), textBounds);

            paint.setColor(graphMarkPointLabelBackgroundColor);
            graphCanvas.drawRect(markPoint.getX()-4,
                    markPoint.getY() - graphMarkPointWidth - textBounds.height() - 4,
                    markPoint.getX() + textBounds.width() + 8,
                    markPoint.getY() - graphMarkPointWidth + 8,
                    paint);

            paint.setColor(graphMarkPointLabelColor);
            graphCanvas.drawText(markPoint.getLabel(),
                    markPoint.getX(),
                    markPoint.getY() - graphMarkPointWidth,
                    paint);

            paint.setColor(graphMarkPointColor);
            graphCanvas.drawCircle(markPoint.getX(), markPoint.getY(), graphMarkPointWidth, paint);
            graphCanvas.drawLine(markPoint.getX(), y1, markPoint.getX(), y2, paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!prepared)
            return;

        if (!animated)
            draw();
        else
            drawAnimation();

        canvas.drawColor(graphBackgroundColor);
        canvas.drawBitmap(graphBitmap, null, graphRect, null);
        canvas.drawBitmap(graphXLegendBitmap, graphRect.left, graphRect.height(), null);
        canvas.drawBitmap(graphYLegendBitmap, 0, 0, null);

        paint.setColor(graphOuterStrokeColor);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(graphStrokeWidth, graphStrokeWidth ,
                getWidth()-graphStrokeWidth, getHeight()-graphStrokeWidth, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void draw() {
        for (int i = 0; i < dataX.length-1; i++)
            drawGraphPoint(i);

        drawMarkPoints();
    }

    private void drawAnimation() {
        if (animating || animationDone)
            return;

        animator.setRange(0, dataX.length-1);
        animator.start();
    }

    public void setIntermediates(int count) {
        if (count <= 0)
            this.intermediates = 1;
        else
            this.intermediates = count;
        invalidate();
    }

    public void setGraphLineWidth(float width) {
        this.graphLineWidth = width;
        invalidate();
    }

    public void setGraphColor(String graphColor) {
        this.graphColor = Color.parseColor(graphColor);
        drawBackplane();
        invalidate();
    }

    public void setGraphBackgroundColor(String graphBackgroundColor) {
        this.graphBackgroundColor = Color.parseColor(graphBackgroundColor);
        drawBackplane();
        invalidate();
    }

    public void setGraphGridColor(String graphGridColor) {
        this.graphGridColor = Color.parseColor(graphGridColor);
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setGraphAxisColor(String graphAxisColor) {
        this.graphAxisColor = Color.parseColor(graphAxisColor);
        drawBackplane();
        invalidate();
    }

    public void setGraphMarkPointColor(String markPointColor) {
        this.graphMarkPointColor = Color.parseColor(markPointColor);
        drawBackplane();
        invalidate();
    }

    public void setGraphOuterStrokeColor(String graphOuterStrokeColor) {
        this.graphOuterStrokeColor = Color.parseColor(graphOuterStrokeColor);
        invalidate();
    }

    public void setGraphLegendTextColor(String graphLegendTextColor) {
        this.graphLegendTextColor = Color.parseColor(graphLegendTextColor);
        drawBackplane();
        invalidate();
    }

    public void setGraphLegendXLabelColor(String graphLegendXLabelColor) {
        this.graphLegendXLabelColor = Color.parseColor(graphLegendXLabelColor);
    }

    public void setGraphLegendYLabelColor(String graphLegendYLabelColor) {
        this.graphLegendYLabelColor = Color.parseColor(graphLegendYLabelColor);
    }

    public void setGraphMarkPointLabelColor(String graphMarkPointLabelColor) {
        this.graphMarkPointLabelColor = Color.parseColor(graphMarkPointLabelColor);
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setGraphMarkPointLabelBackgroundColor(String graphMarkPointLabelBackgroundColor) {
        this.graphMarkPointLabelBackgroundColor =
                Color.parseColor(graphMarkPointLabelBackgroundColor);
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setGraphColor(int graphColor) {
        this.graphColor = getContext().getColor(graphColor);
        drawBackplane();
        invalidate();
    }

    public void setGraphBackgroundColor(int graphBackgroundColor) {
        this.graphBackgroundColor = getContext().getColor(graphBackgroundColor);
        drawBackplane();
        invalidate();
    }

    public void setGraphGridColor(int graphGridColor) {
        this.graphGridColor = getContext().getColor(graphGridColor);
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setGraphAxisColor(int graphAxisColor) {
        this.graphAxisColor = getContext().getColor(graphAxisColor);
        drawBackplane();
        invalidate();
    }

    public void setGraphMarkPointColor(int markPointColor) {
        this.graphMarkPointColor = getContext().getColor(markPointColor);
        drawBackplane();
        invalidate();
    }

    public void setGraphOuterStrokeColor(int graphOuterStrokeColor) {
        this.graphOuterStrokeColor = getContext().getColor(graphOuterStrokeColor);
        invalidate();
    }

    public void setGraphLegendTextColor(int graphLegendTextColor) {
        this.graphLegendTextColor = getContext().getColor(graphLegendTextColor);
        drawBackplane();
        invalidate();
    }

    public void setGraphLegendXLabelColor(int graphLegendXLabelColor) {
        this.graphLegendXLabelColor = getContext().getColor(graphLegendXLabelColor);
    }

    public void setGraphLegendYLabelColor(int graphLegendYLabelColor) {
        this.graphLegendYLabelColor = getContext().getColor(graphLegendYLabelColor);
    }

    public void setGraphMarkPointLabelColor(int graphMarkPointLabelColor) {
        this.graphMarkPointLabelColor = getContext().getColor(graphMarkPointLabelColor);
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setGraphMarkPointLabelBackgroundColor(int graphMarkPointLabelBackgroundColor) {
        this.graphMarkPointLabelBackgroundColor =
                getContext().getColor(graphMarkPointLabelBackgroundColor);
        prepare();
        drawBackplane();
        invalidate();
    }

    public float[] getDataY() {
        return dataY;
    }

    public void setDataY(float[] dataY) {
        this.dataY = dataY;
        prepare();
        drawBackplane();
        invalidate();
    }

    public float[] getDataX() {
        return dataX;
    }

    public void setDataX(float[] dataX) {
        this.dataX = dataX;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setUpperYLimit(float upperYLimit) {
        this.upperYLimit = upperYLimit;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setGraphGridXDensityPercent(int graphGridXDensityPercent) {
        if (graphGridXDensityPercent <= 0)
            this.graphGridXDensityPercent = 1;
        else
            this.graphGridXDensityPercent = graphGridXDensityPercent;

        prepare();
        drawBackplane();
        invalidate();
    }

    public void setGraphGridYGraduationCount(int graphGridYGraduationCount) {
        if (graphGridYGraduationCount <= 0)
            this.graphGridYGraduationCount = 1;
        else
            this.graphGridYGraduationCount = graphGridYGraduationCount;

        prepare();
        drawBackplane();
        invalidate();
    }

    public void setLegendXTextSize(float legendXTextSize) {
        this.legendXTextSize = legendXTextSize;
        drawBackplane();
        invalidate();
    }

    public void setLegendXValueToInt(boolean legendXValueToInt) {
        this.legendXValueToInt = legendXValueToInt;
        drawBackplane();
        invalidate();
    }

    public void setLegendYTextSize(float legendYTextSize) {
        this.legendYTextSize = legendYTextSize;
        drawBackplane();
        invalidate();
    }

    public void setLegendYValueToInt(boolean legendYValueToInt) {
        this.legendYValueToInt = legendYValueToInt;
        drawBackplane();
        invalidate();
    }

    public void setLegendXLabel(String legendXLabel) {
        this.legendXLabel = legendXLabel;
        drawBackplane();
        invalidate();
    }

    public void setLegendYLabel(String legendYLabel) {
        this.legendYLabel = legendYLabel;
        drawBackplane();
        invalidate();
    }

    public void setGraphStrokeWidth(float graphStrokeWidth) {
        this.graphStrokeWidth = graphStrokeWidth;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setLegendXTextSizePercent(float legendXTextSizePercent) {
        if (legendXTextSizePercent == 0)
            this.legendXTextSizePercent = DEFAULT_LEGEND_TEXT_SIZE_PERCENT;
        else
            this.legendXTextSizePercent = legendXTextSizePercent;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setLegendYTextSizePercent(float legendYTextSizePercent) {
        if (legendYTextSizePercent == 0)
            this.legendYTextSizePercent = DEFAULT_LEGEND_TEXT_SIZE_PERCENT;
        else
            this.legendYTextSizePercent = legendYTextSizePercent;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setLegendXLabelTextSize(float legendXLabelTextSize) {
        this.legendXLabelTextSize = legendXLabelTextSize;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setLegendYLabelTextSize(float legendYLabelTextSize) {
        this.legendYLabelTextSize = legendYLabelTextSize;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setLegendXLabelTextSizePercent(float legendXLabelTextSizePercent) {
        if (legendXLabelTextSizePercent == 0)
            this.legendXLabelTextSizePercent = DEFAULT_LEGEND_TEXT_SIZE_PERCENT;
        else
            this.legendXLabelTextSizePercent = legendXLabelTextSizePercent;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setLegendYLabelTextSizePercent(float legendYLabelTextSizePercent) {
        if (legendYLabelTextSizePercent == 0)
            this.legendYLabelTextSizePercent = DEFAULT_LEGEND_TEXT_SIZE_PERCENT;
        else
            this.legendYLabelTextSizePercent = legendYLabelTextSizePercent;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setLegendXValueFormat(String legendXValueFormat) {
        this.legendXValueFormat = legendXValueFormat;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setLegendYValueFormat(String legendYValueFormat) {
        this.legendYValueFormat = legendYValueFormat;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setLegendXSizePercent(int legendXSizePercent) {
        this.legendXSizePercent = legendXSizePercent;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setLegendYSizePercent(int legendYSizePercent) {
        this.legendYSizePercent = legendYSizePercent;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setAnimationInterval(float animationInterval) {
        this.animationInterval = animationInterval;
        if (animator != null)
            animator.setInterval(animationInterval);
    }

    public void showAnimation() {
        animated = true;

        if (animating)
            return;

        prepare();
        drawBackplane();
        invalidate();
    }

    public void setGraphAnimatorListener(GraphAnimatorListener graphAnimatorListener) {
        this.graphAnimatorListener = graphAnimatorListener;
    }

    public void clearMarkPointsY() {
        markPointsY.clear();
    }

    public void addMarkPointY(String label, Float value) {
        for (MarkPoint markPoint : markPointsY)
            if (markPoint.getValue() == value)
                return;

        markPointsY.add(new MarkPoint(label, value));
    }

    public void setGraphMarkPointWidth(float graphMarkPointWidth) {
        this.graphMarkPointWidth = graphMarkPointWidth;
    }

    public void setGraphMarkPointLabelSizePercent(int graphMarkPointLabelSizePercent) {
        this.graphMarkPointLabelSizePercent = graphMarkPointLabelSizePercent;
        prepare();
        drawBackplane();
        invalidate();
    }

    public void setGraphMarkPointLabelSize(float graphMarkPointLabelSize) {
        this.graphMarkPointLabelSize = graphMarkPointLabelSize;
    }

    public void stopAnimation() {
        animator.stop();
        animationDone = true;
        animated = false;
        animating = false;

        if (graphAnimatorListener != null)
            graphAnimatorListener.onAnimationEnd(dataX.length-1);

        prepare();
        drawBackplane();
        invalidate();
    }

}
