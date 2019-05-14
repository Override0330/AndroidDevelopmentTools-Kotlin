package com.override0330.android.redrock.myvideoview.preparemethod

import android.media.MediaPlayer
import com.override0330.android.redrock.myvideoview.`interface`.MediaPrepare

/**
 *从网络上加载视频的实现，待优化
 */
class MediaPrepareFromUrl(private val url:String,
                          private val mediaPlayer: MediaPlayer): MediaPrepare {
    override fun prepare() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
    }
}