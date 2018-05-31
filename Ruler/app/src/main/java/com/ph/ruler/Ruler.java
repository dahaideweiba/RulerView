package com.ph.ruler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Ruler extends View {
    //使用wrap_content时默认的尺寸
    public static final int DEFAULT_WIDTH = 100;
    public static final int DEFAULT_HEIGHT = 600;

    /**
     * 指标画笔
     */
    private Paint mIndexPaint;
    /**
     * 梯度背景画笔
     **/
    private Paint mGradientPaint;
    /**
     * 文字显示
     */
    private Paint mTextPaint;
    /**
     * 数据文本的大小
     */
    private Rect dataTextBound = new Rect();
    /**
     * 绘制画布
     */
    private Canvas mCanvas;
    private Bitmap mBitmap;

    /**
     * 默认梯度色背景宽度
     */
    private int DEFAULT_GRADIENT_WIDTH = 60;
    private int gradientWidth;
    /**
     * 三角形标记边长
     */
    private int DEFAULT_INDEX_SIGN = 30;
    private int indexSign;

    int[] startColorValue = {255, 0, 0};
    int[] endColorValue = {0, 0, 255};

    int left, top, right, bottom;

    float touchY;
    float touchPercent;



    public Ruler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Ruler(Context context) {
        super(context);
        init();
    }

    private void init() {
        mIndexPaint = new Paint();
        mIndexPaint.setStrokeWidth(1);
        mIndexPaint.setAntiAlias(true);
        mIndexPaint.setStyle(Paint.Style.FILL);

        mGradientPaint = new Paint();
        mGradientPaint.setStrokeWidth(1);
        mGradientPaint.setAntiAlias(true);
        mGradientPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(2);
        mTextPaint.setTextSize(30);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.BLACK);

        gradientWidth = DEFAULT_GRADIENT_WIDTH;
        indexSign = DEFAULT_INDEX_SIGN;

        initTouchListener();
    }

    private void initTouchListener() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    touchY = 0;
                    invalidate();
                } else {
                    float y = motionEvent.getY();
                    float x = motionEvent.getX();
                    int action = motionEvent.getAction();
                    if (action == MotionEvent.ACTION_MOVE) {
                        Log.e("-------", "move site x:" + x + ";y:" + y);
                    }
                    if (x >= left && x <= right) {
                        if (top <= y && y <= bottom) {
                            touchPercent = (y - top) / (bottom - top);
                            touchY = y;
                            getRGB((int) x, (int) y);
                            invalidate();
                            Log.e("-------", "click percent is " + touchPercent);
                        }
                    } else {
                        touchY = 0;
                        invalidate();
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (measureWidthMode == MeasureSpec.AT_MOST
                && measureHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        } else if (measureWidthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(DEFAULT_WIDTH, measureHeightSize);
        } else if (measureHeightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(measureWidthSize, DEFAULT_HEIGHT);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int measuredWidth = getMeasuredWidth();
        left = (measuredWidth - gradientWidth) / 2;
        right = left + gradientWidth;
        top = 0;
        bottom = getMeasuredHeight();
        //创建绘制使用的画布
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawGradientBg(canvas);
        drawGradientBg(mCanvas);
        mCanvas.drawBitmap(mBitmap, 0, 0, null);
        if (touchY != 0) {
            drawTriangle(canvas);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    /**
     * 像素转RGB
     */
    private void getRGB(int pointX, int pointY) {
        int pixel = mBitmap.getPixel(pointX, pointY);
        int r = Color.red(pixel);
        int g = Color.green(pixel);
        int b = Color.blue(pixel);
        int a = Color.alpha(pixel);
        mIndexPaint.setColor(Color.rgb(r, g, b));
//        colorStr = "#" + toBrowserHexValue(r) + toBrowserHexValue(g)
//                + toBrowserHexValue(b);    //十六进制的颜色字符串。
    }

    private String toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(
                Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

    /**
     * 画梯度颜色背景
     */
    private void drawGradientBg(Canvas canvas) {


        LinearGradient lg = new LinearGradient(left, top, left, bottom,
                Color.rgb(startColorValue[0], startColorValue[1], startColorValue[2]),
                Color.rgb(endColorValue[0], endColorValue[1], endColorValue[2]), Shader.TileMode.MIRROR);
        mGradientPaint.setShader(lg);
        canvas.drawRect(left, top, right, bottom, mGradientPaint);

    }

    /**
     * 画指向标记三角形 and  文字touchData
     */
    private void drawTriangle(Canvas canvas) {
        float y1 = touchY - indexSign / 2;
        float y2 = y1 + indexSign;
        float y3 = touchY;

        float x3 = right + (float) Math.sqrt(indexSign * indexSign * 3 / 4);
        Path path = new Path();
        path.moveTo(right, y1);
        path.lineTo(right, y2);
        path.lineTo(x3, y3);
        path.close();

        canvas.drawPath(path, mIndexPaint);
        String content = Float.toString(touchPercent);
        mTextPaint.getTextBounds(content, 0, content.length(), dataTextBound);
        canvas.drawText(new StringBuffer().append("=").append(touchPercent).toString(), x3, (y3 + dataTextBound.height() / 2), mTextPaint);
    }

    /**
     * 设置渐变色，由上到下
     */
    public void setGradientColor(int[] startColor, int[] endColor) {
        if (startColor.length == 3 && endColor.length == 3) {
            this.startColorValue = startColor;
            this.endColorValue = endColor;
        }
    }

    /**
     * 设置排序方式
     */
    public void setOder() {

    }
}
