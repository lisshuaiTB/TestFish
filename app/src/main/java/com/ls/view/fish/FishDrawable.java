package com.ls.view.fish;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FishDrawable extends Drawable {
    private Paint mPaint;
    private Path mPath;

    // 身体之外的部分的透明度
    private final static int OTHER_ALPHA = 110;
    // 身体的透明度
    private final static int BODY_ALPHA = 160;

    // 鱼的重心（鱼身的中心）
    private PointF middlePoint;

    private float fishMainAngle = 90;

    public final static float HEAD_RADIUS = 25;

    // 鱼身的长度
    private final static float BODY_LENGTH = 3.2f * HEAD_RADIUS;

    private final static float FIND_FINS_LENGTH = 0.9f * HEAD_RADIUS;

    private final static float FINS_LENGTH = 1.3f * HEAD_RADIUS;

    // -------------鱼尾---------------[
    // 尾部大圆的半径(圆心就是身体底部的中点)
    private final float BIG_CIRCLE_RADIUS = HEAD_RADIUS * 0.7f;
    // 尾部中圆的半径
    private final float MIDDLE_CIRCLE_RADIUS = BIG_CIRCLE_RADIUS * 0.6f;
    // 尾部小圆的半径
    private final float SMALL_CIRCLE_RADIUS = MIDDLE_CIRCLE_RADIUS * 0.4f;
    // --寻找尾部中圆圆心的线长
    private final float FIND_MIDDLE_CIRCLE_LENGTH = BIG_CIRCLE_RADIUS + MIDDLE_CIRCLE_RADIUS;
    // --寻找尾部小圆圆心的线长
    private final float FIND_SMALL_CIRCLE_LENGTH = MIDDLE_CIRCLE_RADIUS * (0.4f + 2.7f);
    // --寻找大三角形底边中心点的线长
    private final float FIND_TRIANGLE_LENGTH = MIDDLE_CIRCLE_RADIUS * 2.7f;

    private float currentValue = 0;

    private PointF headPoint;

    public FishDrawable() {
        init();
    }

    private void init() {
        mPath = new Path();// 路径
        mPaint = new Paint();// 画笔
        mPaint.setStyle(Paint.Style.FILL);// 画笔类型，填充
        mPaint.setARGB(OTHER_ALPHA, 244, 92, 71);// 设置颜色
        mPaint.setAntiAlias(true);// 抗锯齿
        mPaint.setDither(true);// 防抖

        middlePoint = new PointF(4.19f * HEAD_RADIUS, 4.19f * HEAD_RADIUS);

        // currentValue * 1.2=  360 * 整数  currentValue * 1.5= 360 * 整数
        // currentValue = 300 --- currentValue = 240
        // 300/4/5/3 = 5 和 240/4/5/3 = 4 的最小公倍速 ---》 5* 240 -- 4*300 == 1200
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1200f);
        valueAnimator.setDuration(5 * 1000);// 周期
        valueAnimator.setRepeatMode(ValueAnimator.RESTART); // 设置循环模式， 先从-1到1，再从1到-1
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE); // 循环次数，无限制
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (float) animation.getAnimatedValue();
                invalidateSelf();
            }
        });
        valueAnimator.start();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // Math.sin(Math.toRadians(currentValue)) --> -1到1
        float fishAngle = (float) (fishMainAngle +
                Math.sin(Math.toRadians(currentValue * 1.2)) * 4);

        // 绘制鱼头
        headPoint = calculatePoint(middlePoint, BODY_LENGTH / 2, fishAngle);
        canvas.drawCircle(headPoint.x, headPoint.y, HEAD_RADIUS, mPaint);


        // 鱼右鳍
        PointF rightFinsPoint = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle - 110);
        makeFins(canvas, rightFinsPoint, fishAngle, true);

        // 鱼左鳍
        PointF leftFinsPoint = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle + 110);
        makeFins(canvas, leftFinsPoint, fishAngle, false);

        // 身体的底部的中心点
        PointF bodyBottomCenterPoint = calculatePoint(headPoint, BODY_LENGTH, fishAngle - 180);
        // 绘制节肢1
        PointF middleCircleCenterPoint = makeSegment(canvas, bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, MIDDLE_CIRCLE_RADIUS,
                FIND_MIDDLE_CIRCLE_LENGTH, fishAngle, true);

        // 绘制节肢2
//        PointF middleCircleCenterPoint = calculatePoint(bodyBottomCenterPoint,
//                FIND_MIDDLE_CIRCLE_LENGTH, fishAngle - 180);
        makeSegment(canvas, middleCircleCenterPoint, MIDDLE_CIRCLE_RADIUS, SMALL_CIRCLE_RADIUS,
                FIND_SMALL_CIRCLE_LENGTH, fishAngle, false);


        float findEdgeLength = (float) Math.abs(Math.sin(Math.toRadians(currentValue * 1.5))
                * BIG_CIRCLE_RADIUS);
        // 绘制大三角形
        makeTriangle(canvas, middleCircleCenterPoint, FIND_TRIANGLE_LENGTH,
                findEdgeLength, fishAngle);
        // 绘制小三角形
        makeTriangle(canvas, middleCircleCenterPoint, FIND_TRIANGLE_LENGTH - 10,
                findEdgeLength - 20, fishAngle);

        // 画身体
        makeBody(canvas, headPoint, bodyBottomCenterPoint, fishAngle);
    }

    /**
     * 画鱼身
     *
     * @param headPoint
     * @param bodyBottomCenterPoint
     */
    private void makeBody(Canvas canvas, PointF headPoint, PointF bodyBottomCenterPoint, float fishAngle) {
        // 身体的四个点
        PointF topLeftPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle + 90);
        PointF topRightPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle - 90);
        PointF bottomLeftPoint = calculatePoint(bodyBottomCenterPoint, BIG_CIRCLE_RADIUS,
                fishAngle + 90);
        PointF bottomRightPoint = calculatePoint(bodyBottomCenterPoint, BIG_CIRCLE_RADIUS,
                fishAngle - 90);

        // 二阶贝塞尔曲线的控制点
        PointF controlLeft = calculatePoint(headPoint, BODY_LENGTH * 0.56f,
                fishAngle + 130);
        PointF controlRight = calculatePoint(headPoint, BODY_LENGTH * 0.56f,
                fishAngle - 130);

        // 画鱼身
        mPath.reset();
        mPath.moveTo(topLeftPoint.x, topLeftPoint.y);
        mPath.quadTo(controlLeft.x, controlLeft.y, bottomLeftPoint.x, bottomLeftPoint.y);
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);
        mPath.quadTo(controlRight.x, controlRight.y, topRightPoint.x, topRightPoint.y);
        mPaint.setAlpha(BODY_ALPHA);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 画三角形
     *
     * @param findCenterLength 顶点到底部的垂直线长
     * @param findEdgeLength   底部一半
     */
    private void makeTriangle(Canvas canvas, PointF startPoint,
                              float findCenterLength, float findEdgeLength, float fishAngle) {
        // 三角形鱼尾的摆动角度需要跟着节肢2走
        float triangleAngle = (float) (fishAngle +
                Math.sin(Math.toRadians(currentValue * 1.5)) * 35);

        // 底部中心点的坐标
        PointF centerPoint = calculatePoint(startPoint, findCenterLength, triangleAngle - 180);
        // 三角形底部两个点
        PointF leftPoint = calculatePoint(centerPoint, findEdgeLength, triangleAngle + 90);
        PointF rightPoint = calculatePoint(centerPoint, findEdgeLength, triangleAngle - 90);

        // 绘制三角形
        mPath.reset();
        mPath.moveTo(startPoint.x, startPoint.y);
        mPath.lineTo(leftPoint.x, leftPoint.y);
        mPath.lineTo(rightPoint.x, rightPoint.y);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 画节肢
     *
     * @param bottomCenterPoint     梯形底部的中心点坐标（长边）
     * @param bigRadius             大圆的半径
     * @param smallRadius           小圆的半径
     * @param findSmallCircleLength 寻找梯形小圆的线长
     * @param hasBigCircle          是否有大圆
     */
    private PointF makeSegment(Canvas canvas, PointF bottomCenterPoint, float bigRadius,
                               float smallRadius, float findSmallCircleLength, float fishAngle,
                               boolean hasBigCircle) {
        // 节肢摆动的角度
        float segmentAngle;
        // 节肢1 和 节肢2 哪个用sin  哪个用 cos --》 随便1
        // 节肢1 用 cos
        if (hasBigCircle) {
            segmentAngle = (float) (fishAngle +
                    Math.cos(Math.toRadians(currentValue * 1.5)) * 15);
        } else {
            segmentAngle = (float) (fishAngle +
                    Math.sin(Math.toRadians(currentValue * 1.5)) * 35);
        }

        // 梯形上底的中心点（短边）
        PointF upperCenterPoint = calculatePoint(bottomCenterPoint, findSmallCircleLength,
                segmentAngle - 180);
        // 梯形的四个顶点
        PointF bottomLeftPoint = calculatePoint(bottomCenterPoint, bigRadius, segmentAngle + 90);
        PointF bottomRightPoint = calculatePoint(bottomCenterPoint, bigRadius, segmentAngle - 90);
        PointF upperLeftPoint = calculatePoint(upperCenterPoint, smallRadius, segmentAngle + 90);
        PointF upperRightPoint = calculatePoint(upperCenterPoint, smallRadius, segmentAngle - 90);

        if (hasBigCircle) {
            // 绘制大圆
            canvas.drawCircle(bottomCenterPoint.x, bottomCenterPoint.y, bigRadius, mPaint);
        }
        // 绘制小圆
        canvas.drawCircle(upperCenterPoint.x, upperCenterPoint.y, smallRadius, mPaint);

        // 绘制梯形
        mPath.reset();
        mPath.moveTo(bottomLeftPoint.x, bottomLeftPoint.y);
        mPath.lineTo(upperLeftPoint.x, upperLeftPoint.y);
        mPath.lineTo(upperRightPoint.x, upperRightPoint.y);
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);
        canvas.drawPath(mPath, mPaint);

        return upperCenterPoint;
    }

    /**
     * 绘制鱼鳍
     *
     * @param startPoint  起始点的坐标
     * @param fishAngle   鱼头相对于x坐标的角度
     * @param isRightFins
     */
    private void makeFins(Canvas canvas, PointF startPoint, float fishAngle, boolean isRightFins) {
        float controlAngle = 115;

        // 结束点
        PointF endPoint = calculatePoint(startPoint, FINS_LENGTH, fishAngle - 180);
        // 控制点
        PointF controlPoint = calculatePoint(startPoint, 1.8f * FINS_LENGTH,
                isRightFins ? fishAngle - controlAngle : fishAngle + controlAngle);

        mPath.reset();
        mPath.moveTo(startPoint.x, startPoint.y);
        // 二阶贝塞尔曲线
        mPath.quadTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y);

        canvas.drawPath(mPath, mPaint);

    }

    /**
     * 求对应点的坐标 -- 知道起始点，知道鱼头的角度，知道两点间的距离，就可以算出想要的点的坐标
     *
     * @param startPoint 起始点的坐标
     * @param length     两点间的长度
     * @param angle      鱼头相对于x坐标的角度
     * @return
     */
    public static PointF calculatePoint(PointF startPoint, float length, float angle) {
        // angle 角度（0度~360度）  三角函数 -- 弧度
        float deltaX = (float) (Math.cos(Math.toRadians(angle)) * length);
        float deltaY = (float) (-Math.sin(Math.toRadians(angle)) * length);
        return new PointF(startPoint.x + deltaX, startPoint.y + deltaY);
    }

    // 设置透明度
    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    // 设置颜色过滤器
    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        // 只有绘制的地方才覆盖底下的内容
        return PixelFormat.TRANSLUCENT;
    }

    // 如果ImageView的宽高为wrap_content,则获取这个值
    @Override
    public int getIntrinsicHeight() {
        return (int) (8.38f * HEAD_RADIUS);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) (8.38f * HEAD_RADIUS);
    }

    public PointF getMiddlePoint() {
        return middlePoint;
    }

    public PointF getHeadPoint() {
        return headPoint;
    }

    public void setFishMainAngle(float fishMainAngle) {
        this.fishMainAngle = fishMainAngle;
    }
}
