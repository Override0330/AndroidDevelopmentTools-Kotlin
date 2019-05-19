package com.override0330.android.redrock.customview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Interpolator
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator

/**
 * 一个显示数值的一个view，有两种显示方式 1.条形图 2.饼图 都可以拥有自定义的动画效果(提供预设
 */
class DataShowView : View {
    //画两种图的依赖
    private lateinit var shape: DataShowViewShape

    fun setDataShowViewShape(shape: DataShowViewShape){
        this.shape = shape
    }
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    //每次更新了值的时候就进行重绘
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        shape.draw(canvas as Canvas,this)
    }
    fun show(){
        invalidate()
    }
    fun start(){
        shape.init()
            val animation = ObjectAnimator.ofFloat(shape,"nowAngle",0F,shape.totalAngle)
            animation.duration = 1000
            animation.addUpdateListener {
                Log.d("nowAngle","${shape.nowAngle}")
                invalidate()
            }
//            animation.interpolator = AccelerateInterpolator(1.5f)
            animation.start()
    }
}