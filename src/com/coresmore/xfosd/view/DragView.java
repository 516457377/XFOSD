package com.coresmore.xfosd.view;

import java.math.BigDecimal;

import com.coresmore.xfosd.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class DragView extends FrameLayout {
    private static final String TAG = "XFDragView";
    private OnDateChange dateChange;
    private float defaultX;
    private float defaultY;
    private int height;
    private ImageButton img;
    private float lineLong;
    private Paint mPaint;
    private float padding = 50.0F;
    private float space;
    private float space_default = 10.0F;
    private float space_value = 20.0F;
    private float top;
    private float top_default = 2.0F;
    private float top_value = 1.0F;
    private int width;
    private float width_default = 3.0F;
    private float width_info;
    private float width_value = 2.0F;
    private float x1;
    private float x2;
    private float xMove;
    private float xTemp;
    private float x;
    private float y;
    private float y1, y2;
    private float yMove;
    private float yTemp;
    private float xf, yf;
    private boolean first = true;

    private boolean enAbled = false;

    private float f1, f2, f3, f4;
    private float defaultLong = 0f, maxLong = 0f;

    public DragView(Context paramContext) {
        super(paramContext);
        init();
    }

    public DragView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    private int dp2px(Context paramContext, float paramFloat) {
        return (int) (paramFloat * paramContext.getResources().getDisplayMetrics().density + 0.5F);
    }

    private void init() {
        initData();
        setWillNotDraw(false);
        mPaint = new Paint();
        // mPaint.setColor(-11180425);
        mPaint.setStrokeWidth(15.0F);
        mPaint.setStyle(Paint.Style.STROKE);
        img = new ImageButton(getContext());
        FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(-2, -2);
        img.setLayoutParams(localLayoutParams);
        img.setBackgroundResource(R.drawable.p2_btn_music_selector);
        addView(img);
        img.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!enAbled) {
                    return true;
                }
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xf = event.getX();
                    yf = event.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    float f2 = img.getTranslationX() + event.getX() - xf;
                    float f3 = img.getTranslationY() + event.getY() - yf;
                    float f1 = f2;
                    if (f2 < 0.0F) {
                        f1 = 0.0F;
                    }
                    f2 = f1;
                    if (f1 > width - img.getWidth()) {
                        f2 = width - img.getWidth();
                    }
                    f1 = f3;
                    if (f3 < 0.0F) {
                        f1 = 0.0F;
                    }
                    f3 = f1;
                    if (f1 > height - img.getHeight()) {
                        f3 = height - img.getHeight();
                    }
                    img.setTranslationX(f2);
                    img.setTranslationY(f3);
                    if (dateChange != null) {
                        dateChange.date(f2 / (width - img.getWidth()), f3 / (height - img.getHeight()));
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    xf = 0.0F;
                    yf = 0.0F;
                    break;
                default:
                    break;
                }

                return false;
            }

        });
    }

    private void initData() {
        space_value = dp2px(getContext(), space_value);
        space_default = dp2px(getContext(), space_default);
        width_value = dp2px(getContext(), width_value);
        width_default = dp2px(getContext(), width_default);
        top_value = dp2px(getContext(), top_value);
        top_default = dp2px(getContext(), top_default);
        padding = dp2px(getContext(), padding);
    }

    /** 计算两点距离 */
    public double GetDistance(Point paramPoint1, Point paramPoint2) {
        return Math.sqrt(Math.pow(paramPoint1.x - paramPoint2.x, 2.0D) + Math.pow(paramPoint1.y - paramPoint2.y, 2.0D));
    }

    /** 计算两点距离 */
    private float distance4PointF(PointF paramPointF1, PointF paramPointF2) {
        float f1 = paramPointF2.x - paramPointF1.x;
        float f2 = paramPointF2.y - paramPointF1.y;
        return (float) Math.sqrt(f1 * f1 + f2 * f2);
    }

    public void addOnDateChangeListener(OnDateChange paramOnDateChange) {
        this.dateChange = paramOnDateChange;
    }

    @Override
    public void setEnabled(boolean enabled) {
        enAbled = enabled;
        img.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        defaultLong = (float) GetDistance(new Point(img.getWidth() / 2, img.getHeight() / 2),
                new Point(img.getWidth() / 2, img.getHeight() / 2));
        maxLong = (float) GetDistance(new Point(img.getWidth(), img.getHeight()), new Point(width, height))
                - defaultLong;

        x = (img.getTranslationX() + img.getWidth() / 2);
        y = (img.getTranslationY() + img.getHeight() / 2);

        // 左上
        xMove = x;
        yMove = y;
        // mPaint.setColor(-16219209);
        mPaint.setColor(enAbled ? 0xFF0883B7 : 0x550883B7);
        mPaint.setStrokeWidth(6.0F);
        lineLong = ((float) GetDistance(new Point(0, 0), new Point((int) xMove, (int) yMove)));
        Path localPath = new Path();
        space = space_default;
        width_info = width_default;
        top = top_default;
        f1 = ((float) distance4PointF(new PointF(img.getWidth() / 2, img.getHeight() / 2),
                new PointF((int) xMove, (int) yMove))) - defaultLong;
        while (lineLong >= space + padding) {
            xTemp = (space * (xMove - 0.0F) / lineLong + 0.0F);
            yTemp = (space * (yMove - 0.0F) / lineLong + 0.0F);
            x1 = ((float) (xTemp + width_info * yTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            y1 = ((float) (yTemp - width_info * xTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            x2 = ((float) (xTemp - width_info * yTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            y2 = ((float) (yTemp + width_info * xTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            localPath.reset();
            localPath.moveTo(x1, y1);
            xTemp = ((space + top) * (xMove - 0.0F) / lineLong + 0.0F);
            yTemp = ((space + top) * (yMove - 0.0F) / lineLong + 0.0F);
            localPath.quadTo(xTemp, yTemp, x2, y2);
            canvas.drawPath(localPath, mPaint);
            top += top_value;
            width_info += width_value;
            space += space_value;
        }

        // 右上
        canvas.save();
        canvas.translate(width, 0.0F);
        xMove = (x - width);
        yMove = (y - 0.0F);
        xTemp = 0.0F;
        yTemp = 0.0F;
        space = space_default;
        width_info = width_default;
        top = top_default;
        lineLong = ((float) GetDistance(new Point(0, 0), new Point((int) xMove, (int) yMove)));
        f2 = ((float) distance4PointF(new PointF(-img.getWidth() / 2, img.getHeight() / 2),
                new PointF((int) xMove, (int) yMove))) - defaultLong;
        while (lineLong >= space + padding) {
            xTemp = (space * (xMove - 0.0F) / lineLong + 0.0F);
            yTemp = (space * (yMove - 0.0F) / lineLong + 0.0F);
            x1 = ((float) (xTemp + width_info * yTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            y1 = ((float) (yTemp - width_info * xTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            x2 = ((float) (xTemp - width_info * yTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            y2 = ((float) (yTemp + width_info * xTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            localPath.reset();
            localPath.moveTo(x1, y1);
            xTemp = ((space + top) * (xMove - 0.0F) / lineLong + 0.0F);
            yTemp = ((space + top) * (yMove - 0.0F) / lineLong + 0.0F);
            localPath.quadTo(xTemp, yTemp, x2, y2);
            canvas.drawPath(localPath, mPaint);
            top += top_value;
            width_info += width_value;
            space += space_value;
        }

        // 左下
        canvas.restore();
        canvas.save();
        canvas.translate(0.0F, height);
        xMove = (x - 0.0F);
        yMove = (y - height);
        xTemp = 0.0F;
        yTemp = 0.0F;
        space = space_default;
        width_info = width_default;
        top = top_default;
        lineLong = ((float) GetDistance(new Point(0, 0), new Point((int) xMove, (int) yMove)));
        f3 = ((float) distance4PointF(new PointF(img.getWidth() / 2, -img.getHeight() / 2),
                new PointF((int) xMove, (int) yMove))) - defaultLong;
        while (lineLong >= space + padding) {
            xTemp = (space * (xMove - 0.0F) / lineLong + 0.0F);
            yTemp = (space * (yMove - 0.0F) / lineLong + 0.0F);
            x1 = ((float) (xTemp + width_info * yTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            y1 = ((float) (yTemp - width_info * xTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            x2 = ((float) (xTemp - width_info * yTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            y2 = ((float) (yTemp + width_info * xTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            localPath.reset();
            localPath.moveTo(x1, y1);
            xTemp = ((space + top) * (xMove - 0.0F) / lineLong + 0.0F);
            yTemp = ((space + top) * (yMove - 0.0F) / lineLong + 0.0F);
            localPath.quadTo(xTemp, yTemp, x2, y2);
            canvas.drawPath(localPath, mPaint);
            top += top_value;
            width_info += width_value;
            space += space_value;
        }

        // 右下
        canvas.restore();
        canvas.save();
        canvas.translate(width, height);
        xMove = (x - width);
        yMove = (y - height);
        xTemp = 0.0F;
        yTemp = 0.0F;
        space = space_default;
        width_info = width_default;
        top = top_default;
        lineLong = ((float) GetDistance(new Point(0, 0), new Point((int) xMove, (int) yMove)));
        f4 = ((float) distance4PointF(new PointF(-img.getWidth() / 2, -img.getHeight() / 2),
                new PointF((int) xMove, (int) yMove))) - defaultLong;
        while (lineLong >= space + padding) {
            xTemp = (space * (xMove - 0.0F) / lineLong + 0.0F);
            yTemp = (space * (yMove - 0.0F) / lineLong + 0.0F);
            x1 = ((float) (xTemp + width_info * yTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            y1 = ((float) (yTemp - width_info * xTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            x2 = ((float) (xTemp - width_info * yTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            y2 = ((float) (yTemp + width_info * xTemp / Math.sqrt(xTemp * xTemp + yTemp * yTemp)));
            localPath.reset();
            localPath.moveTo(x1, y1);
            xTemp = ((space + top) * (xMove - 0.0F) / lineLong + 0.0F);
            yTemp = ((space + top) * (yMove - 0.0F) / lineLong + 0.0F);
            localPath.quadTo(xTemp, yTemp, x2, y2);
            canvas.drawPath(localPath, mPaint);
            top += top_value;
            width_info += width_value;
            space += space_value;
        }

        canvas.restore();
        if (!first && dateChange != null) {
            // toast((f1)+"_"+(f2)+"_"+(f3)+"_"+(f4)+"="+(maxLong));
            float br = 0, bl = 0, lr = 0, ll = 0;

            maxLong = new BigDecimal(maxLong).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            f1 = new BigDecimal(f1).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            f2 = new BigDecimal(f2).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            f3 = new BigDecimal(f3).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            f4 = new BigDecimal(f4).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            bl = (f1 / maxLong);
            br = (f2 / maxLong);
            ll = (f3 / maxLong);
            lr = (f4 / maxLong);
            // toast(f1+"#"+f2+"#"+f3+"#"+f4+"#"+(maxLong));

            dateChange.dateLong(bl, br, ll, lr);
        }
        first = false;

    }

    public void onStop() {
        first = true;
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        super.onMeasure(paramInt1, paramInt2);
    }

    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        this.width = paramInt1;
        this.height = paramInt2;
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    public void setData(float paramFloat1, float paramFloat2) {
        this.defaultX = paramFloat1;
        this.defaultY = paramFloat2;
        img.setTranslationX(defaultX * (width - img.getWidth()));
        img.setTranslationY(defaultY * (height - img.getHeight()));
        Log.d("XFDragView", "defaultX" + defaultX + "width" + width + "img.getWidth()" + img.getWidth());
    }

    private Toast mToast = null;// 将此作为全局变量
//

    private void toast(String str) {
        if (mToast != null) {
            // mToast.cancel();
            // mToast = Toast.makeText(getContext(), str, Toast.LENGTH_SHORT);
            mToast.setText(str);
            mToast.setDuration(Toast.LENGTH_SHORT);
        } else {
            mToast = Toast.makeText(getContext(), str, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static abstract interface OnDateChange {
        /***/
        public abstract void date(float paramFloat1, float paramFloat2);

        /** 四个角落对应指定点距离 */
        public abstract void dateLong(float br, float bl, float lr, float ll);
    }
}
