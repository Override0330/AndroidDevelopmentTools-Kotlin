package com.override0330.android.redrock.mredrockexam2019.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.override0330.android.redrock.mredrockexam2019.R
import java.util.*
import com.override0330.android.redrock.mredrockexam2019.service.MyMusicService as MyMusicService
//这个 SplashActivity 用来启动服务
class SplashActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val intent = Intent(this, MyMusicService::class.java)
        startService(intent)
        val intent2 = Intent(this, HomePageActivity::class.java)
        startActivity(intent2)
        val timer = Timer()
        timer.schedule(object: TimerTask() {
            override fun run() {
                this@SplashActivity.finish()
            }
        },1000)
    }
}
