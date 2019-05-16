package com.override0330.android.redrock.customview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val showData1 = ShowData(0xFFCCFF00.toInt(),"1",0F,30F)
        val showData2 = ShowData(0xFF6495ED.toInt(),"2",30F,90F)
        val showDate3 = ShowData(0xFFE32636.toInt(),"3",90F,150F)
        val showDataList = arrayListOf(showData1,showData2,showDate3)
        val circleShape = CircleShape(showDataList)
        val dataShowView: DataShowView = findViewById(R.id.ds_test)
        val button: Button = findViewById(R.id.bt_text)
        dataShowView.setDataShowViewShape(circleShape)
        button.setOnClickListener {
            dataShowView.start()
        }

    }
}
