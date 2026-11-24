package com.example.numistracker.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class TypePieChart extends View {

    private static final int COIN_COLOR =
            Color.rgb(212, 175, 55);

    private static final int BANKNOTE_COLOR =
            Color.rgb(38, 166, 154);

    private static final int EMPTY_COLOR =
            Color.rgb(220, 220, 220);

    private final Paint fillPaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint holePaint =
            new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF oval = new RectF();

    private int coinTypes = 0;
    private int banknoteTypes = 0;

    public TypePieChart(Context context) {
        super(context);
        init();
    }

    public TypePieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TypePieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        fillPaint.setStyle(Paint.Style.FILL);

        holePaint.setStyle(Paint.Style.FILL);
        holePaint.setColor(Color.WHITE);
    }

    public void setTypeCounts(int coins, int banknotes) {
        coinTypes = Math.max(0, coins);
        banknoteTypes = Math.max(0, banknotes);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        float radius = Math.min(getWidth(), getHeight()) / 2f - dp(10);

        if (radius <= 0) {
            return;
        }

        oval.set(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
        );

        int total = coinTypes + banknoteTypes;

        if (total == 0) {
            fillPaint.setColor(EMPTY_COLOR);
            canvas.drawOval(oval, fillPaint);
            drawCenterHole(canvas, centerX, centerY, radius);
            return;
        }

        float coinAngle = 360f * coinTypes / total;

        if (coinTypes > 0) {
            fillPaint.setColor(COIN_COLOR);
            canvas.drawArc(oval, -90f, coinAngle, true, fillPaint);
        }

        if (banknoteTypes > 0) {
            fillPaint.setColor(BANKNOTE_COLOR);
            canvas.drawArc(oval, -90f + coinAngle, 360f - coinAngle, true, fillPaint);
        }

        drawCenterHole(canvas, centerX, centerY, radius);
    }

    private void drawCenterHole(Canvas canvas, float cx, float cy, float radius) {
        canvas.drawCircle(cx, cy, radius * 0.42f, holePaint);
    }

    private float dp(int value) {
        return value * getResources().getDisplayMetrics().density;
    }
}