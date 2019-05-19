package com.override0330.android.redrock.mredrockexam2019.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.Gravity
import android.view.View
import com.override0330.android.redrock.mredrockexam2019.service.MyMusicService
import com.override0330.android.redrock.mredrockexam2019.R
import com.override0330.android.redrock.mredrockexam2019.httprequsethelper.Callback
import com.override0330.android.redrock.mredrockexam2019.httprequsethelper.NetUtil
import com.override0330.android.redrock.mredrockexam2019.httprequsethelper.Request
import com.override0330.android.redrock.mredrockexam2019.imagetool.cache.DoubleCacheUtils
import com.override0330.android.redrock.mredrockexam2019.imagetool.core.ImageLoader
import com.override0330.android.redrock.mredrockexam2019.imagetool.other.CutToCircle
import com.override0330.android.redrock.mredrockexam2019.mymusic.MyMusic
import com.override0330.android.redrock.mredrockexam2019.mymusic.PrePareMusicFromUrl
import com.override0330.android.redrock.mredrockexam2019.mymusicplayer.MyMusicPlayerManager
import kotlinx.android.synthetic.main.activity_homepage.*
import kotlinx.android.synthetic.main.navigation_header.*
import org.json.JSONObject

/**
 * 该activity需求：
 * 1.显示歌曲的简单信息
 * 2.emotion
 * 3.向右的一个导航栏
 * 4.按钮启动新activity
 */
class HomePageActivity : AppCompatActivity() , MyMusicPlayerManager.OnStartPlay, View.OnClickListener{
    private var isFirst = true

    private lateinit var musicBinder: MyMusicPlayerManager
    private val connection: ServiceConnection = (object : ServiceConnection {
        //成功连接服务的回调
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicBinder = service as MyMusicPlayerManager
            musicBinder.setOnStartPlay(this@HomePageActivity)
            //初始化心情歌单
            if (isFirst){
                init("HAPPY")
            }else{
                show()
            }

        }
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("服务断开绑定","$this@HomePageActivity")
        }
    })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        iv_homepage_detail.setOnClickListener(this)
        iv_homepage_setting.setOnClickListener(this)
        iv_homepage_emotion.setOnClickListener(this)
        iv_homepage_exciting.setOnClickListener(this)
        iv_homepage_clam.setOnClickListener(this)
        iv_homepage_unhappy.setOnClickListener(this)
        //绑定已经启动的服务
        val intent = Intent(this, MyMusicService::class.java)
        bindService(intent,connection, Context.BIND_AUTO_CREATE)
        dl_homepage.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        ll_select.visibility = View.GONE
    }
    override fun onClick(v: View?) {
        when{
            v!!.id == iv_homepage_detail.id ->{
                //开启歌曲详情
                val intent = Intent(this,DetailActivity::class.java)
                startActivity(intent)
            }
            v.id == iv_homepage_music.id
                    || v.id == tv_homepage_music_name.id
                    ||v.id ==tv_homepage_music_author.id ->{
                //开启歌词页面
            }
            v.id == iv_homepage_setting.id ->{
                dl_homepage.openDrawer(Gravity.RIGHT)
                Log.d("单击事件","打开导航栏")
                tv_navigation_collection.setOnClickListener(this)
                tv_navigation_recommend.setOnClickListener(this)
                tv_navigation_setting.setOnClickListener(this)
                tv_navigation_square.setOnClickListener(this)
            }
            v.id == tv_navigation_collection.id ->{
                Log.d("单击事件","我的收藏")
            }
            v.id == tv_navigation_recommend.id ->{
                val intent = Intent(this,DailyRecommend::class.java)
                startActivity(intent)
            }
            v.id == iv_homepage_emotion.id ->{
                //点击了第四个按钮
                this.runOnUiThread {
                    if (ll_select.visibility==View.GONE) ll_select.visibility = View.VISIBLE
                    else ll_select.visibility =View.GONE
                }
            }
            v.id == iv_homepage_exciting.id ->{
                //点击了第一个按钮
                exchange(0,3)
            }
            v.id == iv_homepage_clam.id ->{
                //点击了第二个按钮
                exchange(1,3)
            }
            v.id == iv_homepage_unhappy.id ->{
                //点击了第三个按钮
                exchange(2,3)
            }
        }
    }
    //交换心情
    fun exchange(p0:Int,p1:Int){
        val a = musicBinder.faceList[p0]
        musicBinder.faceList[p0] = musicBinder.faceList[p1]
        musicBinder.faceList[p1] = a
        //改变歌单
        init(musicBinder.faceList[3].face)
    }

    /**
     * 用来和已经开启的服务链接更新时数据
     */
    override fun show(){
//        iv_homepage_music.setImageBitmap(musicBinder.nowMusic.image)
        this.runOnUiThread { tv_homepage_music_name.text = musicBinder.nowMusic().name
            tv_homepage_music_author.text = musicBinder.nowMusic().author
            ImageLoader.with(this)
                    .from(musicBinder.nowMusic().imageUrl)
                    .cacheWith(DoubleCacheUtils.getInstance())
                    .disposeWith(CutToCircle())
                    .into(iv_homepage_music) }
    }

    override fun onStop() {
        super.onStop()
        //取消绑定
        unbindService(connection)
    }

    override fun onResume() {
        super.onResume()
        //绑定已经启动的服务
        val intent = Intent(this, MyMusicService::class.java)
        bindService(intent,connection, Context.BIND_AUTO_CREATE)
    }

    fun init(type:String){
        //初始化心情歌单
        val requst = Request.Builder("http://elf.egos.hosigus.com/getSongListID.php?type="+type).build()
        NetUtil.getInstance().execute(requst,object :Callback{
            override fun onResponse(response: String?) {
                val json = JSONObject(response)
                val id = json.getJSONObject("data").getString("id")
                getRecommendContent(id)
            }

            override fun onFailed(t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
    private fun getRecommendContent(id:String){
        Log.d("网络请求","post")
        val request = Request.Builder("http://elf.egos.hosigus.com/music/playlist/detail?id=$id")
                .setMethod("GET").build()
        NetUtil.getInstance().execute(request,object :Callback{
            override fun onResponse(response: String?) {
                analyze(response)
            }
            override fun onFailed(t: Throwable?) {
            }
        })
    }
    private fun analyze(json:String?){
        val list:ArrayList<MyMusic> = ArrayList()
        val musicArray = JSONObject(json)
                .getJSONObject("playlist")
                .getJSONArray("tracks")
        for (i in 0 until musicArray.length()){
            val musicJson = musicArray.getJSONObject(i)
            val name = musicJson.getString("name")
            val id = musicJson.getString("id")
            val authorArray = musicJson.getJSONArray("ar")
            var author = ""
            for (j in 0 until authorArray.length()){
                author += authorArray.getJSONObject(j).getString("name")
                if (j!=authorArray.length()-1) author+=","
            }
            Log.d("","$author")
            val al = musicJson.getJSONObject("al")
            val alName = al.getString("name")
            val imageUrl = al.getString("picUrl")
            val music = MyMusic(name,id,author,imageUrl, PrePareMusicFromUrl("http://music.163.com/song/media/outer/url?id=$id.mp3"))
            list.add(music)
        }
        musicBinder.setMusicList(list,0)
        isFirst = false
        show()
    }

}
