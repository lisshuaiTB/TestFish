package com.ls.view.fish

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.ls.view.fish.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val list = mutableListOf<ImageView>()
    val listColor = mutableListOf(R.color.purple_200, R.color.purple_500, R.color.purple_700)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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



    }


    var current = 0
    fun changeTab(index: Int) {

        ValueAnimator.ofFloat(current.toFloat(), index.toFloat()).run {
            duration = 500
            addUpdateListener {
                var value = it.animatedValue as Float
                val cv = if (index-current == 1)abs( value - current) else  value/2f

                val c1 =  ContextCompat.getColor(this@MainActivity, listColor[current])
                val c2 =  ContextCompat.getColor(this@MainActivity, listColor[index])
                val c = ArgbEvaluator().evaluate(cv, c1,c2)

                binding.bottomView.setIndex(value, c as Int)

                Log.d("xxx", " $cv  $c1  $c2  $c   $current->$index")

            }
            doOnEnd {
                current =index
            }

            start()
        }

        list.forEachIndexed { i, imageView ->
            if (i == index) {
                imageView.translationY = 0f
            } else {
                imageView.translationY = 25.dip
            }
        }


    }

}