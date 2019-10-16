package com.coresmore.xfosd;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
    ImageView img;
    PointF pointCenter, pointA, pointB;
    float lastX, lastY;

    private View img_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainPager.class));
        finish();
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.img);
        img_view = findViewById(R.id.img_view);
        
        img.setVisibility(View.GONE);
        img_view.setVisibility(View.GONE);
        img_view.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // pointCenter = new PointF()
                    lastX = event.getX();
                    lastY = event.getY();
                    pointCenter = new PointF(img.getWidth() / 2, img.getHeight() / 2);
                    break;
                case MotionEvent.ACTION_MOVE:
                    pointA = new PointF(lastX, lastY);
                    pointB = new PointF(event.getX(), event.getY());

                    float v1 = angle(pointCenter, pointA, pointB) + img.getRotation();
                    //限制旋转的角度
                    v1 = v1 > 270 ? 270 : v1;
                    v1 = v1 < 0 ? 0 :v1;
                    
                    img.setRotation(v1);

                    toast(v1 + "___" + (int) pointB.x + "__" + (int) pointCenter.y);
                    lastX = event.getX();
                    lastY = event.getY();
                    // Canvas canvas = new Canvas();
                    // Paint paint = new Paint();
                    // paint.setColor(0xff00ff00);
                    // paint.setStrokeWidth(300);
                    // canvas.drawPoint(pointCenter.x, pointCenter.y, paint);
                    // v.draw(canvas);
                    // v.invalidate();
                    break;
                default:

                    break;
                }

                return true;
            }
        });

    }

    // @Override
    // public boolean onTouchEvent(MotionEvent event) {
    // // TODO Auto-generated method stub
    // switch (event.getAction()) {
    // case MotionEvent.ACTION_DOWN:
    // lastX = event.getX();
    // lastY = event.getY();
    //
    // break;
    // case MotionEvent.ACTION_MOVE:
    // pointA = new PointF(lastX, lastY);
    // pointB = new PointF(event.getX(), event.getY());
    //
    // // 角度
    // double a = distance4PointF(pointCenter,
    // pointA);
    // double b = distance4PointF(
    // pointA,
    // pointB);
    // double c = distance4PointF(
    // pointCenter,
    // pointB);
    //
    // double cosb = (a * a + c * c - b * b) / (2 * a * c);
    // if (cosb >= 1) {
    // cosb = 1f;
    // }
    //
    // double radian = Math.acos(cosb);
    // float newDegree = (float) radianToDegree(radian);
    //
    // // center -> proMove的向量， 我们使用PointF来实现
    // PointF centerToProMove = new PointF(
    // (pointA.x - pointCenter.x),
    // (pointA.y - pointCenter.y));
    //
    // // center -> curMove 的向量
    // PointF centerToCurMove = new PointF(
    // (pointB.x - pointCenter.x),
    // (pointB.y - pointCenter.y));
    //
    // // 向量叉乘结果,如果结果为负数， 表示为逆时针， 结果为正数表示顺时针
    // float result = centerToProMove.x * centerToCurMove.y
    // - centerToProMove.y * centerToCurMove.x;
    //
    //// if (result < 0) {
    //// newDegree = -newDegree;
    //// }
    // float v1 = img.getRotation() + newDegree;
    // v1 = angle(pointCenter, pointA, pointB)+img.getRotation();
    //
    // Log.d(TAG, "rotation:"+v1);
    //// v1 = v1 > 270 ? 270 : v1;
    //// v1 = v1 < 0 ? 0 :v1;
    //
    // img.setRotation(v1);
    // lastX = event.getX();
    // lastY = event.getY();
    // break;
    // default:
    // break;
    // }
    //
    // return true;
    // }

    /**
     * 根据坐标系中的3点确定夹角的方法（注意：夹角是有正负的）
     * @param cen 旋转的中心点
     * @param first 起始点
     * @param second 结束点
     * @return 返回旋转的角度具有方向
     */
    public float angle(PointF cen, PointF first, PointF second) {
        float dx1, dx2, dy1, dy2;

        dx1 = first.x - cen.x;
        dy1 = first.y - cen.y;
        dx2 = second.x - cen.x;
        dy2 = second.y - cen.y;

        // 计算三边的平方
        float ab2 = (second.x - first.x) * (second.x - first.x) + (second.y - first.y) * (second.y - first.y);
        float oa2 = dx1 * dx1 + dy1 * dy1;
        float ob2 = dx2 * dx2 + dy2 * dy2;

        // 根据两向量的叉乘来判断顺逆时针
        boolean isClockwise = ((first.x - cen.x) * (second.y - cen.y) - (first.y - cen.y) * (second.x - cen.x)) > 0;

        // 根据余弦定理计算旋转角的余弦值
        double cosDegree = (oa2 + ob2 - ab2) / (2 * Math.sqrt(oa2) * Math.sqrt(ob2));

        // 异常处理，因为算出来会有误差绝对值可能会超过一，所以需要处理一下
        if (cosDegree > 1) {
            cosDegree = 1;
        } else if (cosDegree < -1) {
            cosDegree = -1;
        }

        // 计算弧度
        double radian = Math.acos(cosDegree);

        // 计算旋转过的角度，顺时针为正，逆时针为负
        return (float) (isClockwise ? Math.toDegrees(radian) : -Math.toDegrees(radian));

    }

    Toast mToast = null;// 将此作为全局变量

    private void toast(String toa) {
        if (mToast != null) {
            mToast.setText(toa);
        } else {
            mToast = Toast.makeText(MainActivity.this, toa, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

}

