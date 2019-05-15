package com.override0330.android.redrock.demoscrollimageview

import android.app.Activity
import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.animation.Interpolator
import android.widget.LinearLayout
import android.widget.Scroller
import java.util.*

class MyBanner: LinearLayout{
    private val viewPager = ViewPager(context)
    private lateinit var activity: Activity
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setAdapter(adapter: PagerAdapter,activity: Activity):MyBanner{
        viewPager.adapter = adapter
        this.activity = activity
        return this
    }
    fun setTransformer(transformer: ViewPager.PageTransformer):MyBanner{
        viewPager.setPageTransformer(true,transformer)
        return this
    }
    fun setScrollTime(time: Int){
        val viewPagerScroller = ViewPagerScroller(context)
        viewPagerScroller.setScrollDuration(time)
    }
    fun init(){
        viewPager.offscreenPageLimit = 2
        viewPager.pageMargin = -250
        viewPager.currentItem = Int.MAX_VALUE/2
        this.clipChildren = false
        this.orientation = VERTICAL
        this.addView(viewPager)
    }
    fun start(){
        val timer = Timer()
        timer.schedule(object :TimerTask(){
            override fun run() {
                activity.runOnUiThread {
                    viewPager.currentItem = viewPager.currentItem+1
                }
            }
        },0,3000)
    }



    /**
     * ViewPager 滚动速度设置
     *
     */
    inner class ViewPagerScroller : Scroller {
        private var mScrollDuration = 2000             // 滑动速度

        constructor(context: Context?) : super(context)
        constructor(context: Context?, interpolator: Interpolator?) : super(context, interpolator)
        constructor(context: Context?, interpolator: Interpolator?, flywheel: Boolean) : super(context, interpolator, flywheel)

        /**
         * 设置速度速度
         * @param duration
         */
        fun setScrollDuration(duration: Int) {
            this.mScrollDuration = duration
            initViewPagerScroll(viewPager)
        }
        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration)
        }

        fun initViewPagerScroll(viewPager: ViewPager) {
            try {
                val mScroller = ViewPager::class.java.getDeclaredField("mScroller")
                mScroller.isAccessible = true
                mScroller.set(viewPager, this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}