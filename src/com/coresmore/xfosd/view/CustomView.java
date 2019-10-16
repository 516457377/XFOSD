package com.coresmore.xfosd.view;

import com.coresmore.xfosd.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

/**这个只是外环 小圈*/
public class CustomView extends View {

    private int w, h;
    private int moreMax = 250;
    private float value = 0;
    private int width, height;
    private boolean first = true;

    private float rotateValue = 0;
    
    private OnDataChange dataChange;
    
    /**设置需要的量值*/
    public void setValue(float value) {
//        Log.d("########", "" + value);
        this.value = value;
        invalidate();
    }
    
    public float getValue(){
        return value;
    }
    /**设置控制轮的大小然后根据其再设置外环大小*/
    public void setWidth(int width, int height) {
        this.w = width + moreMax;
        this.h = height + moreMax;
        first = true;
        invalidate();
    }
    
    public void addDataChange(OnDataChange dataChange){
        this.dataChange = dataChange;
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(-135f, width / 2, height / 2);//修正起始位置
        Paint mPaint = new Paint();
        mPaint.setColor(0xff00ff00);
        mPaint.setStrokeWidth(5);
        for (float i = 0; i < 360; i += 22.5f) {//旋转360
            mPaint.setStyle(Paint.Style.FILL);
            if (i == 0) {
                canvas.rotate(22.5f, width / 2, height / 2);//隐藏第一个点
                continue;
            }
            
            rotateValue = i / (360 - 22.5f) * 40;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_small_circle);
            Bitmap bitmap_s = BitmapFactory.decodeResource(getResources(), R.drawable.ic_small_circle_s);
            if (rotateValue <= value) {
                canvas.drawBitmap(bitmap_s, 100, 100, mPaint);
            } else {
                if (value > 0 && i == 22.5f) {
                    canvas.drawBitmap(bitmap_s, 100, 100, mPaint);
                } else
                    canvas.drawBitmap(bitmap, 100, 100, mPaint);
            }
            
            // canvas.drawCircle(100, 100, 5, mPaint);
            canvas.rotate(22.5f, width / 2, height / 2);
            // Log.d("########", rotateValue+"+++++" + i);
        }
        if (!first && (null != dataChange)) {
            dataChange.dataChange((int) value);
        }
        first = false;
    }
    
    public void onStop(){
        first = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        width = w;
        height = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }
    
   public interface OnDataChange {
        void dataChange(int value);
    }
   
   Toast mToast = null;//将此作为全局变量
   int i = 0;
    public void toast(String str) {
        if (mToast != null) {
            mToast.setText(str+i++);
        } else {
            mToast = Toast.makeText(getContext(), str+i++, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }


}
