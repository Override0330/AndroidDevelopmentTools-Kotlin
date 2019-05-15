package com.override0330.android.redrock.demoscrollimageview

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.ImageView
import java.util.*

@SuppressLint("NewApi")
class MainActivity : AppCompatActivity() {
    private lateinit var myBanner: MyBanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myBanner = findViewById(R.id.bn_test)
        val a = ImageView(this)
        a.setImageResource(R.drawable.g)
        val b = ImageView(this)
        b.setImageResource(R.drawable.g)
        val c = ImageView(this)
        c.setImageResource(R.drawable.g)
        val d = ImageView(this)
        d.setImageResource(R.drawable.g)
        val e = ImageView(this)
        e.setImageResource(R.drawable.g)
        val viewList = arrayListOf<View>(a,b,c,d,e)
        val adapter = MyViewPagerAdapter(viewList)
        myBanner.setAdapter(adapter,this).setTransformer(CustomTransformer()).setScrollTime(1000)
        myBanner.init()
        myBanner.start()

    }
}
