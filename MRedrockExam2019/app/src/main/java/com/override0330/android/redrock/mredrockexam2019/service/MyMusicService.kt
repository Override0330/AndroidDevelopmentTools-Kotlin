package com.override0330.android.redrock.mredrockexam2019.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.override0330.android.redrock.mredrockexam2019.mymusicplayer.MyMusicPlayerManager

class MyMusicService: Service() {
    private val myMusicPlayerManager by lazy { MyMusicPlayerManager() }//懒加载\
    override fun onBind(intent: Intent?): IBinder? {
        return myMusicPlayerManager
    }

}