package com.ls.view.fish.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import com.ls.view.fish.ColorTrackTextView
import com.ls.view.fish.databinding.FragmentTab1Binding

class Tab1 : BaseVbFragment<FragmentTab1Binding>() {
    override fun initView(savedInstanceState: Bundle?) {

        mBinding.colorTrackTv.setOnClickListener {
            setAnimation()
        }

        mBinding.pathTest.setOnClickListener {
            mBinding.pathTest.startAnimal()
            mBinding.fllower.startAnimation()
        }

    }

    override fun createObserver() {
    }
    var text_direction = 0
    fun setAnimation() {
        val direction = if (text_direction % 2 == 1) {
            ColorTrackTextView.Direction.LEFT_TO_RIGHT
        } else {
            ColorTrackTextView.Direction.RIGHT_TO_LEFT
        }
        text_direction++
        mBinding.colorTrackTv.setDirection(direction)
        val v = ObjectAnimator.ofFloat(0f, 1f)
        v.duration = 2000
        v.addUpdateListener { animation ->
            val currentProgress = animation.animatedValue as Float
            mBinding.colorTrackTv.setCurrentProgress(currentProgress)
        }
        v.start()
    }
}