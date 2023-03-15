package com.ls.view.fish

import android.graphics.*
import android.graphics.Paint.Style
import android.graphics.drawable.Drawable
import android.util.Log
import kotlin.math.cos
import kotlin.math.sin

class FishDrawable2 : Drawable() {

    val TAG = "FISH-"

    lateinit var mPath: Path
    lateinit var mPaint: Paint

    // 身体之外的部分的透明度
    private val OTHER_ALPHA = 110

    // 身体的透明度
    private val BODY_ALPHA = 160

    // 鱼的重心（鱼身的中心）
    lateinit var middlePoint: PointF

    private val fishMainAngle = 90f

    val HEAD_RADIUS = 100f

    // 鱼身的长度
    private val BODY_LENGTH = 3.2f * HEAD_RADIUS

    private val FIND_FINS_LENGTH = 0.9f * HEAD_RADIUS

    private val FINS_LENGTH = 1.3f * HEAD_RADIUS

    // -------------鱼尾---------------
    // 尾部大圆的半径(圆心就是身体底部的中点)
    private val BIG_CIRCLE_RADIUS = HEAD_RADIUS * 0.7f

    // 尾部中圆的半径
    private val MIDDLE_CIRCLE_RADIUS = BIG_CIRCLE_RADIUS * 0.6f

    // 尾部小圆的半径
    private val SMALL_CIRCLE_RADIUS = MIDDLE_CIRCLE_RADIUS * 0.4f

    // --寻找尾部中圆圆心的线长
    private val FIND_MIDDLE_CIRCLE_LENGTH = BIG_CIRCLE_RADIUS + MIDDLE_CIRCLE_RADIUS

    // --寻找尾部小圆圆心的线长
    private val FIND_SMALL_CIRCLE_LENGTH = MIDDLE_CIRCLE_RADIUS * (0.4f + 2.7f)

    // --寻找大三角形底边中心点的线长
    private val FIND_TRIANGLE_LENGTH = MIDDLE_CIRCLE_RADIUS * 2.7f

    private val currentValue = 0f

    lateinit var headPoint: PointF


    init {
        mPath = Path()
        mPaint = Paint().apply {
            style = Style.FILL
            isDither = true// 防止抖动
            isAntiAlias = true // 抗锯齿
            setARGB(OTHER_ALPHA, 244, 92, 71) // 设置颜色
            Log.d(TAG,"init----1")

        }


        middlePoint = PointF(4.19f * HEAD_RADIUS, 4.19f * HEAD_RADIUS)

        Log.d(TAG,"init----2")
    }


    override fun draw(canvas: Canvas) {
        Log.d(TAG,"draw----")

        // Math.sin(Math.toRadians(currentValue)) --> -1到1
        val fishAngle = (fishMainAngle +
                Math.sin(Math.toRadians(currentValue * 1.2)) * 4).toFloat()
        // 绘制鱼头
        headPoint = calculatePoint(middlePoint, BODY_LENGTH / 2, fishAngle)
        canvas.drawCircle(headPoint.x, headPoint.y, HEAD_RADIUS, mPaint)

//        //右边 鱼鳍
        val rightFinsPointFStart = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle - 110)
        makeFins(canvas, rightFinsPointFStart,fishAngle,true)
        //左边边 鱼鳍
        val leftFinsPointFStart = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle + 110)
        makeFins(canvas, leftFinsPointFStart,fishAngle,false)

        // 身体的底部的中心点

        // 身体的底部的中心点
        val bodyBottomCenterPoint =
            FishDrawable.calculatePoint(headPoint, BODY_LENGTH, fishAngle - 180)
        // 绘制节肢1
        val middleCircleCenterPoint: PointF = makeSegment(
            canvas, bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, MIDDLE_CIRCLE_RADIUS,
            FIND_MIDDLE_CIRCLE_LENGTH, fishAngle, true
        )

        // 绘制节肢2
        makeSegment(
            canvas, middleCircleCenterPoint, MIDDLE_CIRCLE_RADIUS, SMALL_CIRCLE_RADIUS,
            FIND_SMALL_CIRCLE_LENGTH, fishAngle, false
        )
    }

    //设置透明度
    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    // 设置颜色过滤器
    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        // 只有绘制的地方才覆盖底下的内容
        return PixelFormat.TRANSLUCENT;
    }

    // 如果ImageView的宽高为wrap_content,则获取这个值
    //因 鱼可以绕重心旋转， 所以 8.38  = （6.79（身长） -2.6（顶部到中间的位置））*2
    override fun getIntrinsicHeight(): Int {
        return (8.38f * HEAD_RADIUS).toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return (8.38f * HEAD_RADIUS).toInt()
    }

    /**
     * 求对应点的坐标 -- 知道起始点，知道鱼头的角度，知道两点间的距离，就可以算出想要的点的坐标
     *
     * @param startPoint 起始点的坐标
     * @param length     两点间的长度
     * @param angle      鱼头相对于x坐标的角度
     * @return
     */
    fun calculatePoint(startPoint: PointF, length: Float, angle: Float): PointF {
        // angle 角度（0度~360度）  三角函数 -- 弧度
        val deltaX = (cos(Math.toRadians(angle.toDouble())) * length).toFloat()
        val deltaY = (-sin(Math.toRadians(angle.toDouble())) * length).toFloat()
        return PointF(startPoint.x + deltaX, startPoint.y + deltaY)
    }

    /**
     * 绘制鱼鳍
     *
     * @param startPoint  起始点的坐标
     * @param fishAngle   鱼头相对于x坐标的角度
     * @param isRightFins
     */
    private fun makeFins(
        canvas: Canvas,
        startPoint: PointF,
        fishAngle: Float,
        isRightFins: Boolean
    ) {
        val controlAngle = 115f

        // 结束点
        val endPoint = calculatePoint(startPoint, FINS_LENGTH, fishAngle - 180)
        // 控制点
        val controlPoint = calculatePoint(
            startPoint,
            1.8f * FINS_LENGTH,
            if (isRightFins) fishAngle - controlAngle else fishAngle + controlAngle
        )
        mPath.reset()
        mPath.moveTo(startPoint.x, startPoint.y)
        // 二阶贝塞尔曲线
        mPath.quadTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y)
        canvas.drawPath(mPath, mPaint)
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
    private fun makeSegment(
        canvas: Canvas, bottomCenterPoint: PointF, bigRadius: Float,
        smallRadius: Float, findSmallCircleLength: Float, fishAngle: Float,
        hasBigCircle: Boolean
    ): PointF {
        // 节肢摆动的角度
        val segmentAngle: Float = 90f
        // 节肢1 和 节肢2 哪个用sin  哪个用 cos --》 随便1
        // 节肢1 用 cos
//        segmentAngle = if (hasBigCircle) {
//            (fishAngle +
//                    Math.cos(Math.toRadians(currentValue * 1.5)) * 15).toFloat()
//        } else {
//            (fishAngle +
//                    Math.sin(Math.toRadians(currentValue * 1.5)) * 35).toFloat()
//        }

        // 梯形上底的中心点（短边）
        val upperCenterPoint = FishDrawable.calculatePoint(
            bottomCenterPoint, findSmallCircleLength,
            segmentAngle - 180
        )
        // 梯形的四个顶点
        val bottomLeftPoint =
            FishDrawable.calculatePoint(bottomCenterPoint, bigRadius, segmentAngle + 90)
        val bottomRightPoint =
            FishDrawable.calculatePoint(bottomCenterPoint, bigRadius, segmentAngle - 90)
        val upperLeftPoint =
            FishDrawable.calculatePoint(upperCenterPoint, smallRadius, segmentAngle + 90)
        val upperRightPoint =
            FishDrawable.calculatePoint(upperCenterPoint, smallRadius, segmentAngle - 90)
        if (hasBigCircle) {
            // 绘制大圆
            canvas.drawCircle(bottomCenterPoint.x, bottomCenterPoint.y, bigRadius, mPaint)
        }
        // 绘制小圆
        canvas.drawCircle(upperCenterPoint.x, upperCenterPoint.y, smallRadius, mPaint)

        // 绘制梯形
        mPath.reset()
        mPath.moveTo(bottomLeftPoint.x, bottomLeftPoint.y)
        mPath.lineTo(upperLeftPoint.x, upperLeftPoint.y)
        mPath.lineTo(upperRightPoint.x, upperRightPoint.y)
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y)
        canvas.drawPath(mPath, mPaint)
        return upperCenterPoint
    }

}