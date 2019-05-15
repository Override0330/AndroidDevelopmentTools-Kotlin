## 自定义视频播放器

差手势处理(时间分发相关)

### 基本使用

~~~Kotlin
//        val myMediaManager = MyMediaManager.Builder(this).fromPath("android.resource://$packageName/raw/a").build()
        val myMediaManager = MyMediaManager.Builder(this).fromUrl("https://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4").build()
        myMediaManager.init()
~~~

### 注意，需要在AndroidManifest.xml中添加对横屏竖屏的监听已防止activity重启，需要在activity中添加横屏竖屏的回调

添加configChanges属性
~~~xml
<activity android:name=".MainActivity"
                  android:configChanges="orientation">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
~~~

在Activity中添加回调
~~~kotlin
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
~~~