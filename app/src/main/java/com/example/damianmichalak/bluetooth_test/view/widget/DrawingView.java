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
    private boolean zooming = false;
    private float prevDistance = 0;
    private float totalScale = 1;
    private float currentScale;

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

        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        ready = true;
    }

    private void addScheduledPoints() {
        for (PointF point : scheduledPoints) {
            append(point);
        }
    }

    private void append(PointF point) {
        final float x = (point.x * totalScale) + center.x + shiftX;
        final float y = (point.y * totalScale) + center.y + shiftY;
        mPath.lineTo(x, y);
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

    private void secondFingerUp() {
        currentScale = 1.0f;
        prevDistance = 0f;
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

    private void zoomImage(float x1, float y1, float x2, float y2) {

        if (prevDistance == 0) {
            prevDistance = (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        }

        float newDistance = (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));

        currentScale = newDistance / prevDistance;
        shiftX = shiftX * currentScale;
        shiftY = shiftY * currentScale;

        totalScale = totalScale * currentScale;

        prevDistance = newDistance;

        final Matrix translateMatrix = new Matrix();
        translateMatrix.setScale(currentScale, currentScale, center.x, center.y);
        mPath.transform(translateMatrix);

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
            case MotionEvent.ACTION_POINTER_DOWN:
                zooming = true;
                invalidate();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                secondFingerUp();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (zooming && event.getPointerCount() == 2) {
                    final float x1 = event.getX(0);
                    final float y1 = event.getY(0);
                    final float x2 = event.getX(1);
                    final float y2 = event.getY(1);

                    zoomImage(x1, y1, x2, y2);
                } else if (!zooming) {
                    moveImage(x, y);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                zooming = false;
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

    public void reset() {
        mPath.reset();
        invalidate();
    }
}