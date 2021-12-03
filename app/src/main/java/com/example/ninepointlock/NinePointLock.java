package com.example.ninepointlock;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NinePointLock extends View {
    private int pointSize = 50;
    private int colorNormal = getResources().getColor(R.color.colorPrimary);
    private int colorSelected = getResources().getColor(R.color.colorAccent);
    //
    private List<LockPoint> lockPointList = new ArrayList<>();
    private List<LockPoint> selectedLockPointList = new ArrayList<>();
    //
    private int x, y;//change when mouse move(onTouchEvent)
    private Consumer<String> onLockResult;

    public NinePointLock(Context context) {
        super(context);
    }

    public NinePointLock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NinePointLock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NinePointLock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnLockResult(Consumer<String> onLockResult) {
        this.onLockResult = onLockResult;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutPoint();
    }

    void layoutPoint() {
        //clear old data
        this.lockPointList.clear();
        //
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        int itemW = w / 3;
        int itemH = h / 3;
        int num = 1;
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                int left = (col * itemW) - (itemW / 2) - (pointSize / 2);
                int top = (row * itemH) - (itemH / 2) - (pointSize / 2);
                int right = left + pointSize;
                int bottom = top + pointSize;
                RectF rect = new RectF(left, top, right, bottom);

                LockPoint lockPoint = new LockPoint(row, col, num++, rect);
                this.lockPointList.add(lockPoint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paintLine = new Paint();
        paintLine.setColor(colorNormal);
        paintLine.setStrokeWidth(15);
        paintLine.setAntiAlias(true);
        //
        drawConnectionLine(canvas, paintLine);
        drawRealtimeLine(canvas, paintLine);
        drawPoint(canvas);
    }

    void drawConnectionLine(Canvas canvas, Paint paintLine) {
        if (this.selectedLockPointList.size() == 0) {
            return;
        }
        //
        for (int i = 0; i < this.selectedLockPointList.size() - 1; i++) {
            LockPoint start = this.selectedLockPointList.get(i);
            LockPoint end = this.selectedLockPointList.get(i + 1);
            canvas.drawLine(
                    start.rect.centerX(), start.rect.centerY(),
                    end.rect.centerX(), end.rect.centerY(),
                    paintLine);
        }
    }

    void drawPoint(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        for (LockPoint lockPoint : this.lockPointList) {
            if (this.selectedLockPointList.contains(lockPoint)) {
                paint.setColor(colorSelected);
            } else {
                paint.setColor(colorNormal);
            }
            canvas.drawOval(lockPoint.rect, paint);
        }
    }

    void drawRealtimeLine(Canvas canvas, Paint paintLine) {
        if (this.selectedLockPointList.size() == 0) {
            return;
        }
        RectF start = this.selectedLockPointList.get(this.selectedLockPointList.size() - 1).rect;
        canvas.drawLine(start.centerX(), start.centerY(), x, y, paintLine);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            handleActionMove(event);
            invalidate();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            handleActionDown();
        }
        return true;
    }

    private void handleActionDown() {
        if (this.selectedLockPointList.size() > 0) {
            this.onLockResult.accept(
                    this.selectedLockPointList.stream().map(f -> String.valueOf(f.num)).collect(Collectors.joining("-"))
            );
        }
        //reset
        this.selectedLockPointList.clear();
        invalidate();
    }

    void handleActionMove(MotionEvent event) {
        x = (int) event.getX();
        y = (int) event.getY();
        //
        this.lockPointList.stream()
                .filter(f -> f.rect.contains(x, y) && !selectedLockPointList.contains(f))
                .findFirst()
                .ifPresent(selectedLockPoint -> selectedLockPointList.add(selectedLockPoint));
    }

    class LockPoint {
        int row;
        int col;
        int num;
        RectF rect;

        public LockPoint() {
        }

        public LockPoint(int row, int col, int num, RectF rect) {
            this.row = row;
            this.col = col;
            this.num = num;
            this.rect = rect;
        }
    }
}
