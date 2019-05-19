package com.override0330.android.redrock.mredrockexam2019.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import com.override0330.android.redrock.mredrockexam2019.MyRecyclerViewAdapter
import com.override0330.android.redrock.mredrockexam2019.R
import com.override0330.android.redrock.mredrockexam2019.RecyclerViewMyLinearLayoutManager
import com.override0330.android.redrock.mredrockexam2019.httprequsethelper.Callback
import com.override0330.android.redrock.mredrockexam2019.httprequsethelper.NetUtil
import com.override0330.android.redrock.mredrockexam2019.httprequsethelper.Request
import com.override0330.android.redrock.mredrockexam2019.imagetool.cache.DoubleCacheUtils
import com.override0330.android.redrock.mredrockexam2019.imagetool.cache.LocalCacheUtils
import com.override0330.android.redrock.mredrockexam2019.imagetool.cache.MemoryCacheUtils
import com.override0330.android.redrock.mredrockexam2019.imagetool.core.ImageLoader
import com.override0330.android.redrock.mredrockexam2019.mymusic.MyMusic
import com.override0330.android.redrock.mredrockexam2019.mymusic.PrePareMusicFromUrl
import com.override0330.android.redrock.mredrockexam2019.mymusicplayer.MyMusicPlayerManager
import com.override0330.android.redrock.mredrockexam2019.service.MyMusicService
import kotlinx.android.synthetic.main.activity_daily_recommend.*
import org.json.JSONObject

class DailyRecommend : AppCompatActivity(), View.OnClickListener {
    val musicList = ArrayList<MyMusic>()
    private lateinit var musicBinder: MyMusicPlayerManager
    private val connection: ServiceConnection = (object : ServiceConnection {
        //成功连接服务的回调
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicBinder = service as MyMusicPlayerManager
            Log.d("DetailActivity","绑定成功")
            init()
        }
        override fun onServiceDisconnected(name: ComponentName?) {

        }
    })
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_recommend)
        //绑定已经启动的服务
        val intent = Intent(this, MyMusicService::class.java)
        bindService(intent,connection, Context.BIND_AUTO_CREATE)
    }
    //用于初始化推荐歌单-> 网络请求 Json解析
    private fun init(){
        getRecommendId()
        iv_recommend_back.setOnClickListener(this)
    }
    private fun getRecommendId(){
        Log.d("网络请求","get")
        val request = Request.Builder("http://elf.egos.hosigus.com/getRecommendID.php").build()
        NetUtil.getInstance().execute(request,object:Callback{
            override fun onResponse(response: String?) {
                val mainJson = JSONObject(response)
                val recommendId = mainJson.getJSONObject("data").getLong("id")
                getRecommendContent(recommendId.toString())
//                getRecommendContent("442265915")
            }

            override fun onFailed(t: Throwable?) {
                //报错
            }
        })
    }
    private fun getRecommendContent(id:String){
        Log.d("网络请求","post")
        val request = Request.Builder("http://elf.egos.hosigus.com/music/playlist/detail?id=$id")
                .setMethod("GET")
                .build()
        NetUtil.getInstance().execute(request,object :Callback{
            override fun onResponse(response: String?) {
                analysis(response)
            }
            override fun onFailed(t: Throwable?) {
            }
        })
    }
    private fun analysis(json:String?){
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
            val music = MyMusic(name,id,author,imageUrl,PrePareMusicFromUrl("http://music.163.com/song/media/outer/url?id=$id.mp3"))
            musicList.add(music)
        }
        val listName = JSONObject(json)
                .getJSONObject("playlist").getString("name")
        this.runOnUiThread {
            tv_recommend_name.text = listName
        }
        val desc = JSONObject(json)
                .getJSONObject("playlist").getString("description")
        this.runOnUiThread {
            tv_recommend_desc.text = desc
        }
        val listImage = JSONObject(json)
                .getJSONObject("playlist").getString("coverImgUrl")
        ImageLoader.with(this).cacheWith(DoubleCacheUtils.getInstance()).from(listImage).into(iv_recommend_image)
        setRecyclerView()
    }
    private fun setRecyclerView(){
        val myRecyclerViewAdapter = MyRecyclerViewAdapter(R.layout.item_recommend,musicList)
        myRecyclerViewAdapter.setInit { viewHolder, position ->
            viewHolder.setTextViewText(R.id.tv_item_name,musicList[position].name)
                    .setTextViewText(R.id.tv_item_author,musicList[position].author)
                    .setImageUrl(R.id.iv_item,musicList[position].imageUrl,this)
                    .setOnClickListener(R.id.ll_item) {
                        musicBinder.setMusicList(musicList,position)//通过iBinder添加歌曲到播放列表！
                    }
        }
        this.runOnUiThread {
            Log.d("适配器","适配器装载")
            val layoutManager = RecyclerViewMyLinearLayoutManager(this)
            rv_music.isNestedScrollingEnabled = false
            rv_music.layoutManager = layoutManager
            rv_music.adapter = myRecyclerViewAdapter
        }
    }
    override fun onClick(v: View?) {
        when{
            v!!.id == iv_recommend_back.id ->{
                this@DailyRecommend.finish()
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

}
