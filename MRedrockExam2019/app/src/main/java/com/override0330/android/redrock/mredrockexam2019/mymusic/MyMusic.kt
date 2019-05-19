package com.override0330.android.redrock.mredrockexam2019.mymusic

import android.util.Log
import com.override0330.android.redrock.mredrockexam2019.lyric.Lyric
import com.override0330.android.redrock.mredrockexam2019.httprequsethelper.Callback
import com.override0330.android.redrock.mredrockexam2019.httprequsethelper.NetUtil
import com.override0330.android.redrock.mredrockexam2019.httprequsethelper.Request
import org.json.JSONObject


class MyMusic(val name: String, val id:String, val author: String, val imageUrl: String,val musicPrepare:MusicPrepare){
    var lyric: Lyric? = null
    lateinit var request: Request
    private var onCompleteListener: OnCompleteListener? = null
    fun prepareLyric(){
        Log.d("开始准备歌词","name：$name id:$id")
        request = Request.Builder("http://elf.egos.hosigus.com/music/lyric?id=$id")
                .setMethod("GET").build()
        NetUtil.getInstance().execute(request,object :Callback{
            override fun onResponse(response: String?) {
                Log.d("歌曲id","$id")
                Log.d("歌词","$response")
                val mainJson = JSONObject(response)
                val str = mainJson.getJSONObject("lrc").getString("lyric")
                lyric = Lyric(str!!)
                onCompleteListener!!.completed()
            }

            override fun onFailed(t: Throwable?) {
                Log.d("歌词准备","出错啦")
                onCompleteListener!!.fail()
            }
        })
    }
    //歌词加载完成的回调
    interface OnCompleteListener{
        fun completed()
        fun fail()
    }
    fun setOnCompleteListener(onCompleteListener: OnCompleteListener){
        this.onCompleteListener = onCompleteListener
    }
}