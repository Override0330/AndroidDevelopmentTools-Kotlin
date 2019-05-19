package com.override0330.android.redrock.mredrockexam2019.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.override0330.android.redrock.demoscrollimageview.CustomTransformer
import com.override0330.android.redrock.demoscrollimageview.MyViewPagerAdapter
import com.override0330.android.redrock.mredrockexam2019.R
import com.override0330.android.redrock.mredrockexam2019.imagetool.cache.DoubleCacheUtils
import com.override0330.android.redrock.mredrockexam2019.imagetool.core.ImageLoader
import com.override0330.android.redrock.mredrockexam2019.imagetool.other.CutToCircle
import com.override0330.android.redrock.mredrockexam2019.mymusic.MyMusic
import com.override0330.android.redrock.mredrockexam2019.mymusicplayer.MyMusicPlayerManager
import com.override0330.android.redrock.mredrockexam2019.service.MyMusicService
import kotlinx.android.synthetic.main.activity_detail.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

class DetailActivity : AppCompatActivity() ,MyMusicPlayerManager.OnStartPlay,View.OnClickListener{
    private lateinit var musicBinder: MyMusicPlayerManager
    private val connection: ServiceConnection = (object : ServiceConnection {
        //成功连接服务的回调
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicBinder = service as MyMusicPlayerManager
            musicBinder.setOnStartPlay(this@DetailActivity)
            Log.d("DetailActivity","绑定成功")
            init()
        }
        override fun onServiceDisconnected(name: ComponentName?) {

        }
    })

    private var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        //绑定已经启动的服务
        val intent = Intent(this, MyMusicService::class.java)
        bindService(intent,connection, Context.BIND_AUTO_CREATE)
    }
    //界面更新
    override fun show() {
        //刷新歌名作者
        tv_detail_name.text = musicBinder.nowMusic().name
        tv_detail_author.text = musicBinder.nowMusic().author
        tv_end.text = musicBinder.getLong()
        //刷新歌曲图片
        ImageLoader.with(this)
                .cacheWith(DoubleCacheUtils.getInstance())
                .from(musicBinder.nowMusic().imageUrl)
                .disposeWith(CutToCircle())
                .into(iv_detail_music)
        musicBinder.setTextView(tv_detail_now,this@DetailActivity)
        //刷新歌词
        showLyric()

    }
    //初始化方法，
    fun init(){
        iv_detail_download.setOnClickListener(this)
        iv_detail_favourite.setOnClickListener(this)
        iv_detail_like.setOnClickListener(this)
        iv_detail_comment.setOnClickListener(this)
        iv_detail_play.setOnClickListener(this)
        iv_detail_previous.setOnClickListener(this)
        iv_detail_next.setOnClickListener(this)
        musicBinder.seekBar = sb_detail
        show()
    }
    private fun changeImage(imageView:ImageView,src:Int){
        this.runOnUiThread { imageView.setImageResource(src) }
    }
    override fun onClick(v: View?) {
        when{
            v!!.id==iv_detail_play.id ->{
                if (musicBinder.isPlaying()) {
                    musicBinder.pause()
                    changeImage(iv_detail_play,R.drawable.ic_play_pause)
                }else{
                    musicBinder.play()
                    changeImage(iv_detail_play,R.drawable.ic_play_running)
                }
            }
            v.id == iv_detail_previous.id ->{
                timer.cancel()
                musicBinder.playPrevious()
            }
            v.id == iv_detail_next.id -> {
                timer.cancel()
                musicBinder.playNext()
            }
            v.id == iv_detail_favourite.id ->{
                showAddView()
            }
            v.id == iv_detail_back.id ->{
                this@DetailActivity.finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

    override fun onResume() {
        super.onResume()
        //绑定已经启动的服务
        val intent = Intent(this, MyMusicService::class.java)
        bindService(intent,connection, Context.BIND_AUTO_CREATE)
    }
    private fun showAddView(){
        ll_detail_content.visibility = View.GONE
        item_detail_add.visibility = View.VISIBLE
    }

    private fun showLyric(){
        musicBinder.nowMusic().setOnCompleteListener(object :MyMusic.OnCompleteListener{
            override fun fail() {
                this@DetailActivity.runOnUiThread { mb_lyric.visibility = View.GONE }
            }

            override fun completed() {
//                //歌词准备完毕的回调
                this@DetailActivity.runOnUiThread {
                    val viewList = ArrayList<View>()
                    if (musicBinder.nowMusic().lyric==null) return@runOnUiThread
                    for (i in 0 until musicBinder.nowMusic().lyric!!.arrayList.size){
                        val view = LayoutInflater.from(this@DetailActivity).inflate(R.layout.item_lyric,null)
                        view.findViewById<TextView>(R.id.tv_item_lyric).text=musicBinder.nowMusic().lyric!!.arrayList[i]
                        viewList.add(view)
                    }
                    val adapter = MyViewPagerAdapter(viewList)
                    mb_lyric.setAdapter(adapter,this@DetailActivity)
                            .setTransformer(CustomTransformer())
                            .setScrollTime(1500)
                    mb_lyric.init()
                    mb_lyric.visibility = View.VISIBLE
                    startRefreshLyric()
                }
            }
        })
        musicBinder.nowMusic().prepareLyric()
    }

    private fun startRefreshLyric() {
        timer.cancel()
        timer = Timer()
        try{
            timer.schedule(timerTask {
                val current= musicBinder.getCurrentPosition()
//            Log.d("","now: $nowPosition")
                val position = musicBinder.nowMusic().lyric!!.getLyric(current)
//            Log.d("现在的歌词：","${musicBinder.nowMusic().lyric!!.getLyric(current)}")
//            Log.d("Position","$position")
                mb_lyric.changeTo(position)
            }, 0, 300)
        }catch (e:Exception){
            e.stackTrace
        }
    }
}
