package com.ls.view.fish

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Path
import android.graphics.PathEffect
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import kotlin.math.abs

class BottomView :View{
    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initData()
    }




    lateinit var mPaint:Paint
    lateinit var mPath:Path
    var mColor:Int = Color.parseColor("#FFBB86FC")
    fun initData(){

         mPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Style.FILL
        }

         mPath = Path()


    }


    var currentIndext = 0f
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        mPaint.color = mColor
        val indexValue = currentIndext
        //先固定分为三等分
        val tabWith = width / 3f
        //每一个弧度的中心控制点
        val centerTabWith = tabWith / 2f
        //弧度端口到两边 ONewidth距离
        val controllerX = centerTabWith * 0.625f
        val startX = controllerX / 2f

        val keyAnimal = tabWith * indexValue
        canvas.save()
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //绘制阴影 开启硬件加速无效果
        mPaint.setShadowLayer(
            15f,
            5f,
            -6f,
            mColor
        )

        val lineH = 0.3f * height
        val grooveH = 0.6f * height
        //绘制圆
        canvas.drawCircle(centerTabWith + keyAnimal, lineH, 40.dip, mPaint)


        mPath.reset()
        mPath.moveTo(0f, lineH)
        mPath.lineTo(startX + keyAnimal, lineH)
//        mPath.lineTo(controllerX + keyAnimal, lineH)
//        mPath.lineTo(controllerX + keyAnimal, grooveH)
//        mPath.lineTo(tabWith - controllerX, grooveH)
//        mPath.lineTo(tabWith - controllerX, lineH)
        mPath.cubicTo(
            controllerX + keyAnimal,
            lineH,
            controllerX + keyAnimal,
            grooveH,
            centerTabWith + keyAnimal,
            grooveH
        )
        mPath.cubicTo(
            tabWith - controllerX + keyAnimal,
            grooveH,
            tabWith- controllerX + keyAnimal,
            lineH,
            tabWith - startX + keyAnimal,
            lineH
        )


        mPath.lineTo(width.toFloat(), lineH)
        mPath.lineTo(width.toFloat(), height.toFloat())
        mPath.lineTo(0f, height.toFloat())
        mPath.close()
        //canvas.clipPath(path)
//        mPaint.pathEffect = CornerPathEffect(50.dip)
        canvas.drawPath(mPath, mPaint)
    }



    fun setIndex(offset:Float,color: Int){
        currentIndext = offset
        mColor = color
        invalidate()
    }



    val listColor = mutableListOf(R.color.purple_200, R.color.purple_500, R.color.purple_700)
    var current = 0
    fun changeTab(index: Int) {

        ValueAnimator.ofFloat(current.toFloat(), index.toFloat()).run {
            duration = 500
            addUpdateListener {
                var value = it.animatedValue as Float
                val cv = if (index-current == 1) abs( value - current) else  value/2f

                val c1 =  ContextCompat.getColor(context, listColor[current])
                val c2 =  ContextCompat.getColor(context, listColor[index])
                val c = ArgbEvaluator().evaluate(cv, c1,c2)
                setIndex(value, c as Int)
                Log.d("xxx", " $cv  $c1  $c2  $c   $current->$index")

            }
            doOnEnd {
                current =index
            }

            start()
        }



    }




}

