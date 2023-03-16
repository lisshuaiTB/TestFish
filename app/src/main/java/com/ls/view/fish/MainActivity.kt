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



    }

    fun changeTab(index:Int){
        binding.bottomView.changeTab(index)
        list.forEachIndexed { i, imageView ->
            if (i == index) {
                imageView.translationY = 0f
            } else {
                imageView.translationY = 25.dip
            }
        }
    }


}