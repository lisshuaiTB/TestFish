package com.ls.view.fish

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ls.view.fish.databinding.ActivityColorTextBinding
import com.ls.view.fish.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val list = mutableListOf<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.fixDensity()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        binding.iv.setImageDrawable(FishDrawable2())

        list.add(binding.tab1)
        list.add(binding.tab2)
        list.add(binding.tab3)

        binding.tab1.setOnClickListener {
            changeTab(0)
        }

        binding.tab2.setOnClickListener {
            changeTab(1)
            binding.pathTest.startAnimal()
        }

        binding.tab3.setOnClickListener {
            changeTab(2)
            binding.fllower.startAnimation()
        }
        binding.colorTrackTv.setOnClickListener {
            setAnimation()
        }

    }

    fun changeTab(index: Int) {
        binding.bottomView.changeTab(index)
        list.forEachIndexed { i, imageView ->

            val start = if (i == index) {
                25.dip
            } else {
                0f
            }
            val to = if (i == index) {
                0f
            } else {
                25.dip
            }

            val a = ObjectAnimator.ofFloat(imageView, "translationY", start, to)
            a.addUpdateListener {

            }
            a.duration = 500
            a.start()
        }
    }

    //    lateinit var bindingColorText:ActivityColorTextBinding

    var text_direction = 0

    fun setAnimation() {
        val direction = if (text_direction % 2 == 1) {
            ColorTrackTextView.Direction.LEFT_TO_RIGHT
        } else {
            ColorTrackTextView.Direction.RIGHT_TO_LEFT
        }
        text_direction++
        binding.colorTrackTv.setDirection(direction)
        val v = ObjectAnimator.ofFloat(0f, 1f)
        v.duration = 2000
        v.addUpdateListener { animation ->
            val currentProgress = animation.animatedValue as Float
            binding.colorTrackTv.setCurrentProgress(currentProgress)
        }
        v.start()
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        LLog.d("....")
        return super.dispatchTouchEvent(ev)
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }
}