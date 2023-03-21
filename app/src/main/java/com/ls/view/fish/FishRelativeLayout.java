package com.ls.view.fish;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FishRelativeLayout extends RelativeLayout {

    private ImageView ivFish;
    private FishDrawable fishDrawable;

    // 绘制水波纹的
    private Paint mPaint;
    private float touchX = -1;
    private float touchY = -1;

    private int alpha;
    // 半径的变化系数
    private float ripple;

    public FishRelativeLayout(Context context) {
        this(context, null);
    }

    public FishRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FishRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // 创建 ImageView 并添加到 FishRelativeLayout
        ivFish = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ivFish.setLayoutParams(layoutParams);

//        ivFish.setBackgroundColor(Color.YELLOW);

        // 创建 fish 并添加到 ImageView
        fishDrawable = new FishDrawable();
        ivFish.setImageDrawable(fishDrawable);

        addView(ivFish);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);

        // 因为是容器，不会执行 onDraw，设置标志位，让onDraw执行
        setWillNotDraw(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取手指按下的 x，y坐标，作为圆心的point
                touchX = event.getX();
                touchY = event.getY();

                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this,
                        "ripple", 0, 1f).setDuration(1000);
                objectAnimator.start();

                makeTrail();
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    private void makeTrail() {
        PointF fishImageViewMiddle = fishDrawable.getMiddlePoint();

        /**
         * 直接跳转到手指按下的位置
         */
//        ivFish.setTranslationX(touchX - ivFish.getLeft() - fishImageViewMiddle.x);
//        ivFish.setTranslationY(touchY - ivFish.getTop() - fishImageViewMiddle.y);

        /**
         * 在路径上慢慢移动到手指按下的位置 -- 使用 ValueAnimator 方式实现
         */
        // 属性动画 -- ValueAnimator(值的改变，与属性无关),ObjectAnimator(属性动画)
        // 小鱼的起始点 上一次平移后的位置
        // x方向的平移
//        ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(ivFish.getTranslationX(),
//                touchX - ivFish.getLeft() - fishImageViewMiddle.x);
//        valueAnimator1.setDuration(2000);
//        valueAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float currentValue = (float) animation.getAnimatedValue();
//                ivFish.setTranslationX(currentValue);
//            }
//        });
//        valueAnimator1.start();
//
//        // y方向的平移
//        ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(ivFish.getTranslationY(),
//                touchY - ivFish.getTop() - fishImageViewMiddle.y);
//        valueAnimator2.setDuration(2000);
//        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float currentValue = (float) animation.getAnimatedValue();
//                ivFish.setTranslationY(currentValue);
//            }
//        });
//        valueAnimator2.start();

        /**
         * 在路径上慢慢移动到手指按下的位置 -- 使用 ObjectAnimator 方式1实现 -- setTranslationX
         */
//        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(ivFish, "translationX",
//                ivFish.getTranslationX(), touchX - ivFish.getLeft() - fishImageViewMiddle.x);
//        objectAnimator1.setDuration(2000);
//        objectAnimator1.start();
//
//        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(ivFish, "translationY",
//                ivFish.getTranslationY(), touchY - ivFish.getTop() - fishImageViewMiddle.y);
//        objectAnimator2.setDuration(2000);
//        objectAnimator2.start();

        /**
         * 在路径上慢慢移动到手指按下的位置 -- 使用 ObjectAnimator 方式2实现 -- setX,setY
         */

//        fun2(fishImageViewMiddle);
        /**
         * 在路径上慢慢移动到手指按下的位置 -- 使用 ObjectAnimator 方式3实现 -- setX,setY -- 通过Path
         */
        fun3(fishImageViewMiddle);

    }



    void fun2(PointF fishImageViewMiddle) {
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(ivFish, "x",
                ivFish.getX(), touchX - fishImageViewMiddle.x);
        objectAnimator1.setDuration(2000);
        objectAnimator1.start();

        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(ivFish, "y",
                ivFish.getY(), touchY - fishImageViewMiddle.y);
        objectAnimator2.setDuration(2000);
        objectAnimator2.start();
    }


    /**
     * 在路径上慢慢移动到手指按下的位置 -- 使用 ObjectAnimator 方式3实现 -- setX,setY -- 通过Path
     * @param fishImageViewMiddle  鱼的中心点
     */
    public void fun3(PointF fishImageViewMiddle) {
        // 起始点
        PointF fishMiddle = new PointF(ivFish.getX() + fishImageViewMiddle.x,
                ivFish.getY() + fishImageViewMiddle.y);
        // 鱼头的坐标 -- 相对于 水池的 坐标 -- 控制点1
        PointF fishHead = new PointF(ivFish.getX() + fishDrawable.getHeadPoint().x,
                ivFish.getY() + fishDrawable.getHeadPoint().y);
        // 点击位置 -- 结束点
        PointF touch = new PointF(touchX, touchY);

        float angle = includedAngle(fishMiddle, fishHead, touch);
        float delta = includedAngle(fishMiddle, new PointF(fishMiddle.x + 1, fishMiddle.y), fishHead);
        // 控制点2
        PointF controlPoint = fishDrawable.calculatePoint(fishMiddle,
                FishDrawable.HEAD_RADIUS * 1.6f, angle / 2 + delta);

        Path path = new Path();
        path.moveTo(ivFish.getX(), ivFish.getY());
        path.cubicTo(fishHead.x - fishImageViewMiddle.x, fishHead.y - fishImageViewMiddle.y,
                controlPoint.x - fishImageViewMiddle.x, controlPoint.y - fishImageViewMiddle.y,
                touchX - fishImageViewMiddle.x, touchY - fishImageViewMiddle.y);


        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivFish, "x", "y", path);
        objectAnimator.setDuration(2000);
        objectAnimator.start();

        final PathMeasure pathMeasure = new PathMeasure(path, false);
        final float[] tan = new float[2];
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float fraction = animator.getAnimatedFraction();
                pathMeasure.getPosTan(pathMeasure.getLength() * fraction, null, tan);
                // y轴与实际坐标相反，tan[1] 需要取反
                float angle = (float) Math.toDegrees(Math.atan2(-tan[1], tan[0]));
                fishDrawable.setFishMainAngle(angle);
            }
        });

        objectAnimator.start();
    }

    public static float includedAngle(PointF O, PointF A, PointF B) {
        // OA的长度
        float OALength = (float) Math.sqrt((A.x - O.x) * (A.x - O.x) + (A.y - O.y) * (A.y - O.y));
        // OB的长度
        float OBLength = (float) Math.sqrt((B.x - O.x) * (B.x - O.x) + (B.y - O.y) * (B.y - O.y));
        //向量的数量积的坐标表示：a·b=x·x'+y·y'
        // 向量的数量积 OA*OB=(Ax-Ox)*(Bx-Ox)+(Ay-Oy)*(By-Oy)
        float OAOB = (A.x - O.x) * (B.x - O.x) + (A.y - O.y) * (B.y - O.y);
        // cosAOB = (OA*OB)/(|OA|*|OB|)
        float cosAOB = OAOB / (OALength * OBLength);

        // 角度 -- 反余弦
        float angleAOB = (float) Math.toDegrees(Math.acos(cosAOB));

        // 鱼是向左转弯，还是向右转弯
        // AB连线和 X轴的夹角 tan 值 - OB连线与X轴的夹角 tan值  --> 负数 说明点击在鱼的右侧，正数在左侧
        float direction = (A.y - B.y) / (A.x - B.x) - (O.y - B.y) / (O.x - B.x);

        // 点击在鱼头延长线上 -- angleAOB == 0 ---点击在鱼尾延长线上 angleAOB 180
        if (direction == 0) {
            if (OAOB >= 0) {
                return 0;
            } else
                return 180;
        } else {
            if (direction > 0) {
                return -angleAOB;
            } else {
                return angleAOB;
            }
        }
    }

    // 会执行吗？？
    @Override
    protected void onDraw(Canvas canvas) {
        LLog.INSTANCE.d("123123");
        if (touchX >= 0 && touchY >= 0) {
            mPaint.setAlpha(alpha);
            canvas.drawCircle(touchX, touchY, ripple * 150, mPaint);
        }
    }

    public void setRipple(float ripple) {
        alpha = (int) (100 * (1 - ripple));
        this.ripple = ripple;

        invalidate();
    }
}
