package com.example.damianmichalak.bluetooth_test.view.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawingView extends View {

    private Path mPath;
    private Paint circlePaint;
    private PointF center;
    private float prevX;
    private float prevY;
    private float downY;
    private float downX;
    private float shiftY;
    private float shiftX;
    private float oldY;
    private float oldX;
    private List<PointF> scheduledPoints = new ArrayList<>();
    private boolean ready = false;

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mPath = new Path();
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        center = new PointF(w / 2, h / 2);

        mPath.moveTo(center.x, center.y);
        addScheduledPoints();
//        addLines();

        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        ready = true;
    }

    private void addScheduledPoints() {
        for (PointF point : scheduledPoints) {
            append(point);
        }
    }

    private void addLines() {
//        float x = 40;
//        append(-x, 0);
//        append(-x, x);
//        append(x, x);
//        append(x, -x);
//        append(-2 * x, -x);
//        append(-2 * x, 2 * x);
//        append(2 * x, 2 * x);
//        append(2 * x, -2 * x);
//        append(-3 * x, -2 * x);
//        append(-3 * x, 3 * x);
//        append(3 * x, 3 * x);
//        append(3 * x, -3 * x);
//        append(-4 * x, -3 * x);
//        append(-4 * x, 4 * x);
//        append(4 * x, 4 * x);
    }

    private void append(PointF point) {
        mPath.lineTo(point.x + center.x + (shiftX), point.y + center.y + (shiftY));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, circlePaint);
    }

    private void actionDown(float x, float y) {
        prevY = y;
        prevX = x;

        downY = y;
        downX = x;

        oldX = shiftX;
        oldY = shiftY;

    }

    private void moveImage(float x, float y) {
        final Matrix translateMatrix = new Matrix();
        translateMatrix.setTranslate(x - prevX, y - prevY);
        mPath.transform(translateMatrix);
        prevY = y;
        prevX = x;

        shiftX = (x - downX) + oldX;
        shiftY = (y - downY) + oldY;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!ready) return true;

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                actionDown(x, y);
                invalidate();
                break;
//            case MotionEvent.ACTION_POINTER_DOWN:
//                invalidate();
//                break;
            case MotionEvent.ACTION_MOVE:
                moveImage(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                invalidate();
                break;
        }
        return true;
    }

    public void addPoint(PointF pointF) {
        if (ready) {
            append(pointF);
        }
    }

    public void schedulePoints(List<PointF> previousPoints) {
        scheduledPoints = previousPoints;
    }
}