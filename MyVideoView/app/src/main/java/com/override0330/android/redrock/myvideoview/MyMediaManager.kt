package com.override0330.android.redrock.myvideoview

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import java.util.*
import kotlin.concurrent.timerTask
import com.override0330.android.redrock.myvideoview.`interface`.MediaPrepare
import com.override0330.android.redrock.myvideoview.preparemethod.MediaPrepareFromLocalPath
import com.override0330.android.redrock.myvideoview.preparemethod.MediaPrepareFromUrl

/**
 * 视频控制类
 */
class MyMediaManager(){
    private lateinit var activity: Activity
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var surfaceView: SurfaceView

    private lateinit var videoTitle: TextView
    private lateinit var videoControl: ImageView
    private lateinit var videoSchedule: SeekBar
    private lateinit var videoFull: ImageView

    private lateinit var seekBarTimer: Timer
    private lateinit var controlBarTimer: Timer
    private var controlBarCount = 0//计数器

    private lateinit var parentView: LinearLayout//surfaceView父布局
    private var videoHeight: Int = 0
    private lateinit var controlBar: View//控制bar
    private lateinit var detailBar: View//titleBar

    private lateinit var prepareMethod: MediaPrepare//不同的加载实现
    private lateinit var onError:MediaPlayer.OnErrorListener//播放错误的回调

    private constructor(activity: Activity,mediaPlayer: MediaPlayer):this(){
        this.activity = activity
        this.mediaPlayer = mediaPlayer
    }
    /**
     * 变种的建造者模式
     */
    class Builder(
            //kotlin 专属构造器√
            // 必要参数
            private val activity: Activity) {
        //非必要
        private lateinit var prepareMethod: MediaPrepare
        private var path: String = "实例视频路径"
        private var url: String = "示例视频url"

        fun fromPath(path: String): MediaFromLocalPath {
            val mediaPlayer = MediaPlayer()
            return MediaFromLocalPath(mediaPlayer,path,activity)
        }

        fun fromUrl(url: String): MediaFromUrl{
            val mediaPlayer = MediaPlayer()
            return MediaFromUrl(mediaPlayer,url,activity)
        }
    }

    /**
     * 绑定控件
     */
    private fun beBind(){
        surfaceView = activity.findViewById(R.id.sv_video)
        videoTitle = activity.findViewById(R.id.tv_title)
        videoControl = activity.findViewById(R.id.iv_play)
        videoSchedule = activity.findViewById(R.id.sb_play_control)
        videoFull = activity.findViewById(R.id.iv_screen)
    }
    /**
     *初始化surfaceView，视频资源，seekBar，按钮监听
     */
    fun init() {
        //绑定控件变量
        beBind()
        //初始化变量
        controlBar = videoSchedule.parent as View
        detailBar = videoTitle.parent as View
        parentView = surfaceView.parent as LinearLayout

        //初始化SurfaceView
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            //大小改变的回调
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                fitScreen()
            }

            //销毁的回调
            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                //同时销毁 mediaPlayer
                mediaPlayer.release()
            }

            //初始化成功的回调
            override fun surfaceCreated(holder: SurfaceHolder?) {
//                mediaPlayer.setDataSource(activity, Uri.parse("android.resource://" + activity.packageName + "/raw/a"))
                mediaPlayer.isLooping = true
                mediaPlayer.setDisplay(surfaceView.holder)
                mediaPlayer.setScreenOnWhilePlaying(true)
                prepareMethod.prepare()
                videoHeight = surfaceView.height
            }
        })

        //播放器准备完毕的回调
        mediaPlayer.setOnPreparedListener {
            //开始播放
            mediaPlayer.start()
            //适配分辨率
            fitScreen()
            //倒计时隐藏bar
            hiddenControlBar()
            videoSchedule.max = mediaPlayer.duration
            //开启seekBar更新线程
            startRefreshSeekBar()
            startCountBarTime()

        }
        //播放器播放完毕的回调
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.reset()
            mediaPlayer.pause()
        }
        //播放错误回调,通过注入来实现具体逻辑
//        mediaPlayer.setOnErrorListener(onError)

        /**
         * 滑动条视频跳转的监听
         */
        videoSchedule.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var isTouch = false
            //正在脱移
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (isTouch) {
                    val position = seekBar.progress
                    mediaPlayer.seekTo(position)
                    controlBarCount = 0
                    mediaPlayer.pause()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isTouch = true
                showControlBar()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isTouch = false
//                hiddenControlBar()
                mediaPlayer.start()
            }
        })
        surfaceView.setOnClickListener {
            showControlBar()
        }
        /**
         * 暂停播放按钮的监听
         */
        videoControl.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                activity.runOnUiThread { videoControl.setImageResource(R.drawable.play) }
                controlBarCount = 0
            } else {
                mediaPlayer.start()
                activity.runOnUiThread { videoControl.setImageResource(R.drawable.pause) }
                controlBarCount= 0
            }
        }
        /**
         * 全屏按钮的监听
         */
        videoFull.setOnClickListener {
            val state: Int = activity.resources.configuration.orientation
            if (state == Configuration.ORIENTATION_LANDSCAPE){
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                activity.runOnUiThread { videoFull.setImageResource(R.drawable.fullscreen) }
                controlBarCount= 0
            }else{
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                activity.runOnUiThread { videoFull.setImageResource(R.drawable.smallscreen) }
                controlBarCount= 0
            }
        }
    }
    /**
     * SeekBar的进度更新
     */
    private fun startRefreshSeekBar() {
        seekBarTimer = Timer()
        seekBarTimer.schedule(timerTask {
            val nowPosition = mediaPlayer.currentPosition
            videoSchedule.progress = nowPosition
        }, 0, 10)
    }

    /**
     * 控制条和标题的计数器，如果到了3(s)那就隐藏，从其他地方更新controlBarCount来充值计数器
     */
    private fun startCountBarTime(){
        controlBarTimer = Timer()
        controlBarTimer.schedule(timerTask {
            controlBarCount++
            if (controlBarCount == 3){
                hiddenControlBar()
            }
        },0,1000)
    }
    private fun fitScreen(){
        //获取父布局和surface布局的参数
        val parentParameters = parentView.layoutParams
        //计算视频的宽高
        val videoWidth: Float = mediaPlayer.videoWidth.toFloat()
        val videoHeight: Float = mediaPlayer.videoHeight.toFloat()
        //保持视频的高宽比
        val videoRatio: Float = (videoWidth/videoHeight)
        Log.d("视频的高宽比为","$videoRatio width: $videoWidth height: $videoHeight")
        //通过改变surface的父布局来改变,高度锁定，改变宽度来维持高宽比
        parentParameters.width = (videoWidth/videoHeight*parentParameters.height).toInt()
        parentView.layoutParams = parentParameters
    }
    //转换成全屏模式
    fun changeToFullScreen(){
        //因为已经对surfaceView做了保持比例适配，所以只需要改变父布局的高度就可以做到适配了
        val parentParameter = parentView.layoutParams
        parentParameter.height = activity.windowManager.defaultDisplay.height
        //隐藏状态栏
        this.activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        parentView.layoutParams = parentParameter
        fitScreen()
    }
    fun changeToSmallScreen(){
        val parentParameter = parentView.layoutParams
        parentParameter.height = videoHeight
        //显示状态栏
        this.activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        parentView.layoutParams = parentParameter
        fitScreen()
    }

    //隐藏控制条和标题
    private fun hiddenControlBar(){
        activity.runOnUiThread{
            controlBar.visibility = View.GONE
            detailBar.visibility = View.GONE
        }
    }
    //显示控制条和标题
    private fun showControlBar(){
        activity.runOnUiThread{
            controlBar.visibility = View.VISIBLE
            detailBar.visibility = View.VISIBLE
        }

    }

    //Builder模式变种内部类
    class MediaFromLocalPath (private val mediaPlayer: MediaPlayer,
                              private val localPath: String,
                              private val activity: Activity){
        fun build():MyMediaManager{
            val mediaPrepareFromLocalPath = MediaPrepareFromLocalPath(mediaPlayer,localPath,activity)
            val myMediaManager = MyMediaManager(activity,mediaPlayer)
            myMediaManager.prepareMethod = mediaPrepareFromLocalPath
            return myMediaManager
        }
    }

    class MediaFromUrl(private val mediaPlayer: MediaPlayer,
                       private val url: String,
                       private val activity: Activity){
        fun build():MyMediaManager{
            val mediaPrepareFromUrl = MediaPrepareFromUrl(url,mediaPlayer)
            val myMediaManager = MyMediaManager(activity,mediaPlayer)
            myMediaManager.prepareMethod = mediaPrepareFromUrl
            return myMediaManager
        }
    }
}