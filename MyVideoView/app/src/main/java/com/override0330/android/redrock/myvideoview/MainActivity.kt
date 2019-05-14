package com.override0330.android.redrock.myvideoview

import android.content.res.Configuration
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var myMediaManager: MyMediaManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        myMediaManager = MyMediaManager.Builder(this).fromPath("android.resource://$packageName/raw/a").build()
        myMediaManager = MyMediaManager.Builder(this).fromUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4").build()
        myMediaManager.init()
    }
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
                //横屏
                myMediaManager.changeToFullScreen()

            }else{
                //竖屏
                myMediaManager.changeToSmallScreen()
            }
        }
    }
}
