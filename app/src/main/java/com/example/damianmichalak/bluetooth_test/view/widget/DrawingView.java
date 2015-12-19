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

public class DrawingView extends View {

    private Path mPath;
    Context context;
    private Paint circlePaint;
    private PointF center;
    private float firstX;
    private float firstY;

    public DrawingView(Context context) {
        super(context);
        init(context);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context c) {
        context = c;
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
//        addLines();

        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    private void addLines() {
        float x = 40;
        append(-x, 0);
        append(-x, x);
        append(x, x);
        append(x, -x);
        append(-2 * x, -x);
        append(-2 * x, 2 * x);
        append(2 * x, 2 * x);
        append(2 * x, -2 * x);
        append(-3 * x, -2 * x);
        append(-3 * x, 3 * x);
        append(3 * x, 3 * x);
        append(3 * x, -3 * x);
        append(-4 * x, -3 * x);
        append(-4 * x, 4 * x);
        append(4 * x, 4 * x);
    }

    private void append(PointF point) {
        mPath.lineTo(point.x + center.x, point.y + center.y);
        invalidate();
    }

    private void append(float x, float y) {
        x = x + center.x;
        y = y + center.y;
        mPath.lineTo(x, y);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, circlePaint);
    }

    private void firstFinger(float x, float y) {
        firstY = y;
        firstX = x;
    }

    private void moveImage(float x, float y) {
        Matrix translateMatrix = new Matrix();
        translateMatrix.setTranslate(x - firstX, y - firstY);
        mPath.transform(translateMatrix);
        firstY = y;
        firstX = x;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                firstFinger(x, y);
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
        append(pointF);
    }

}