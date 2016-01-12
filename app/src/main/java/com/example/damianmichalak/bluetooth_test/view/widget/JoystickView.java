package com.example.damianmichalak.bluetooth_test.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {
    public static final int INVALID_POINTER_ID = -1;

    private final boolean D = false;
    String TAG = "JoystickView";

    private Paint dbgPaint1;
    private Paint dbgPaint2;

    private Paint bgPaint;
    private Paint handlePaint;

    private int innerPadding;
    private int bgRadius;
    private int handleRadius;
    private int movementRadius;
    private int handleInnerBoundaries;

    private JoystickListener moveListener;
    private float moveResolution;
    private boolean yAxisInverted;
    private boolean autoReturnToCenter;
    public final static int CONSTRAIN_BOX = 0;
    public final static int CONSTRAIN_CIRCLE = 1;
    private int movementConstraint;
    private float movementRange;
    public final static int COORDINATE_CARTESIAN = 0;
    public final static int COORDINATE_DIFFERENTIAL = 1;
    private int userCoordinateSystem;
    private float touchPressure;
    private boolean clicked;
    private float clickThreshold;
    private int pointerId = INVALID_POINTER_ID;
    private float touchX, touchY;
    private float reportX, reportY;
    private float handleX, handleY;
    private int cX, cY;
    private int dimX, dimY;
    private int cartX, cartY;
    private double radial;
    private double angle;
    private int userX, userY;
    private int offsetX;
    private int offsetY;

    public JoystickView(Context context) {
        super(context);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initJoystickView();
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initJoystickView();
    }

    private void initJoystickView() {
        setFocusable(true);

        dbgPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        dbgPaint1.setColor(Color.RED);
        dbgPaint1.setStrokeWidth(1);
        dbgPaint1.setStyle(Paint.Style.STROKE);

        dbgPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        dbgPaint2.setColor(Color.GREEN);
        dbgPaint2.setStrokeWidth(1);
        dbgPaint2.setStyle(Paint.Style.STROKE);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.GRAY);
        bgPaint.setStrokeWidth(1);
        bgPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(Color.DKGRAY);
        handlePaint.setStrokeWidth(1);
        handlePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        innerPadding = 10;

        setMovementRange(10);
        setMoveResolution(1.0f);
        setClickThreshold(0.4f);
        setYAxisInverted(true);
        setUserCoordinateSystem(COORDINATE_CARTESIAN);
        setAutoReturnToCenter(true);
    }

    public void setAutoReturnToCenter(boolean autoReturnToCenter) {
        this.autoReturnToCenter = autoReturnToCenter;
    }

    public void setUserCoordinateSystem(int userCoordinateSystem) {
        if (userCoordinateSystem >= COORDINATE_CARTESIAN && movementConstraint <= COORDINATE_DIFFERENTIAL) {
            this.userCoordinateSystem = userCoordinateSystem;
        }
    }

    public void setYAxisInverted(boolean yAxisInverted) {
        this.yAxisInverted = yAxisInverted;
    }

    public void setClickThreshold(float clickThreshold) {
        if (clickThreshold >= 0 && clickThreshold <= 1.0f) {
            this.clickThreshold = clickThreshold;
        }
    }

    public void setMovementRange(float movementRange) {
        this.movementRange = movementRange;
    }

    public void setMoveResolution(float moveResolution) {
        this.moveResolution = moveResolution;
    }

    public void setJostickListener(JoystickListener listener) {
        this.moveListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int d = Math.min(getMeasuredWidth(), getMeasuredHeight());

        dimX = d;
        dimY = d;

        cX = d / 2;
        cY = d / 2;

        bgRadius = dimX / 2 - innerPadding;
        handleRadius = (int) (d * 0.25);
        handleInnerBoundaries = handleRadius;
        movementRadius = Math.min(cX, cY) - handleInnerBoundaries;
    }

    private int measure(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 200;
        } else {
            result = specSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawCircle(cX, cY, bgRadius, bgPaint);

        handleX = touchX + cX;
        handleY = touchY + cY;
        canvas.drawCircle(handleX, handleY, handleRadius, handlePaint);

        if (D) {
            canvas.drawRect(1, 1, getMeasuredWidth() - 1, getMeasuredHeight() - 1, dbgPaint1);

            canvas.drawCircle(handleX, handleY, 3, dbgPaint1);

            if (movementConstraint == CONSTRAIN_CIRCLE) {
                canvas.drawCircle(cX, cY, this.movementRadius, dbgPaint1);
            } else {
                canvas.drawRect(cX - movementRadius, cY - movementRadius, cX + movementRadius, cY + movementRadius, dbgPaint1);
            }

            canvas.drawLine(cX, cY, handleX, handleY, dbgPaint2);

            int baseY = (int) (touchY < 0 ? cY + handleRadius : cY - handleRadius);
            canvas.drawText(String.format("%s (%.0f,%.0f)", TAG, touchX, touchY), handleX - 20, baseY - 7, dbgPaint2);
            canvas.drawText("(" + String.format("%.0f, %.1f", radial, angle * 57.2957795) + (char) 0x00B0 + ")", handleX - 20, baseY + 15, dbgPaint2);
        }
        canvas.restore();
    }

    private void constrainBox() {
        touchX = Math.max(Math.min(touchX, movementRadius), -movementRadius);
        touchY = Math.max(Math.min(touchY, movementRadius), -movementRadius);
    }

    private void constrainCircle() {
        float diffX = touchX;
        float diffY = touchY;
        double radial = Math.sqrt((diffX * diffX) + (diffY * diffY));
        if (radial > movementRadius) {
            touchX = (int) ((diffX / radial) * movementRadius);
            touchY = (int) ((diffY / radial) * movementRadius);
        }
    }

    public void setPointerId(int id) {
        this.pointerId = id;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                return processMoveEvent(ev);
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (pointerId != INVALID_POINTER_ID) {
                    actionUp();
                    returnHandleToCenter();
                    setPointerId(INVALID_POINTER_ID);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                if (pointerId != INVALID_POINTER_ID) {
                    final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = ev.getPointerId(pointerIndex);
                    if (pointerId == this.pointerId) {
                        actionUp();
                        returnHandleToCenter();
                        setPointerId(INVALID_POINTER_ID);
                        return true;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                if (pointerId == INVALID_POINTER_ID) {
                    int x = (int) ev.getX();
                    if (x >= offsetX && x < offsetX + dimX) {
                        setPointerId(ev.getPointerId(0));
                        actionDown();
                        return true;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                if (pointerId == INVALID_POINTER_ID) {
                    final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = ev.getPointerId(pointerIndex);
                    int x = (int) ev.getX(pointerId);
                    if (x >= offsetX && x < offsetX + dimX) {
                        actionDown();
                        setPointerId(pointerId);
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    private void actionUp() {
        if (moveListener != null) {
            moveListener.onStopped();
        }
    }

    private void actionDown() {
        if (moveListener != null) {
            moveListener.onStarted();
        }
    }

    private boolean processMoveEvent(MotionEvent ev) {
        if (pointerId != INVALID_POINTER_ID) {
            final int pointerIndex = ev.findPointerIndex(pointerId);

            float x = ev.getX(pointerIndex);
            touchX = x - cX - offsetX;
            float y = ev.getY(pointerIndex);
            touchY = y - cY - offsetY;

            reportOnMoved();
            invalidate();

            touchPressure = ev.getPressure(pointerIndex);

            return true;
        }
        return false;
    }

    private void reportOnMoved() {
        if (movementConstraint == CONSTRAIN_CIRCLE)
            constrainCircle();
        else
            constrainBox();

        calcUserCoordinates();

        if (moveListener != null) {
            boolean rx = Math.abs(touchX - reportX) >= moveResolution;
            boolean ry = Math.abs(touchY - reportY) >= moveResolution;
            if (rx || ry) {
                this.reportX = touchX;
                this.reportY = touchY;

                moveListener.onMoved(userX, userY);
            }
        }
    }

    private void calcUserCoordinates() {
        //First convert to cartesian coordinates
        cartX = (int) (touchX / movementRadius * movementRange);
        cartY = (int) (touchY / movementRadius * movementRange);

        radial = Math.sqrt((cartX * cartX) + (cartY * cartY));
        angle = Math.atan2(cartY, cartX);

        //Invert Y axis if requested
        if (!yAxisInverted)
            cartY *= -1;

        if (userCoordinateSystem == COORDINATE_CARTESIAN) {
            userX = cartX;
            userY = cartY;
        } else if (userCoordinateSystem == COORDINATE_DIFFERENTIAL) {
            userX = cartY + cartX / 4;
            userY = cartY - cartX / 4;

            if (userX < -movementRange)
                userX = (int) -movementRange;
            if (userX > movementRange)
                userX = (int) movementRange;

            if (userY < -movementRange)
                userY = (int) -movementRange;
            if (userY > movementRange)
                userY = (int) movementRange;
        }

    }

    private void returnHandleToCenter() {
        if (autoReturnToCenter) {
            final int numberOfFrames = 5;
            final double intervalsX = (0 - touchX) / numberOfFrames;
            final double intervalsY = (0 - touchY) / numberOfFrames;

            for (int i = 0; i < numberOfFrames; i++) {
                final int j = i;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        touchX += intervalsX;
                        touchY += intervalsY;

                        reportOnMoved();
                        invalidate();

                    }
                }, i * 40);
            }

        }
    }

}