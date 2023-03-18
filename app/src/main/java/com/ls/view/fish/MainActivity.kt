package com.ls.view.fish

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.ls.view.fish.databinding.ActivityColorTextBinding
import com.ls.view.fish.databinding.ActivityMainBinding
import com.ls.view.fish.fragment.Tab1
import com.ls.view.fish.fragment.Tab2
import com.ls.view.fish.fragment.Tab3

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val list = mutableListOf<ImageView>()
    val listF = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.fixDensity()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listF.add(f1)
        listF.add(f2)
        listF.add(f3)

//        binding.iv.setImageDrawable(FishDrawable2())

        list.add(binding.tab1)
        list.add(binding.tab2)
        list.add(binding.tab3)

        supportFragmentManager.beginTransaction()
            .replace(R.id.content, listF[0]).commit()

        binding.tab1.setOnClickListener {
            changeTab(0)
        }

        binding.tab2.setOnClickListener {
            changeTab(1)
        }

        binding.tab3.setOnClickListener {
            changeTab(2)
        }


    }

    val f1: Tab1 by lazy {
        Tab1()
    }
    val f2: Tab2 by lazy {
        Tab2()
    }
    val f3: Tab3 by lazy {
        Tab3()
    }



    fun changeTab(index: Int) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.content, listF[index]).commit()

        binding.bottomView.changeTab(index)
        list.forEachIndexed { i, imageView ->
            if (i == index){
                val a = ObjectAnimator.ofFloat(imageView, "translationY", imageView.translationY, 0f)
                a.addUpdateListener {

                }
                a.duration = 500
                a.start()
            }else{
                imageView.translationY = 25.dip
            }



        }
    }

    //    lateinit var bindingColorText:ActivityColorTextBinding






    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)

    }


}