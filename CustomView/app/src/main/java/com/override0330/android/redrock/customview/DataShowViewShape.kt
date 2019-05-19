package com.override0330.android.redrock.customview

import android.graphics.Canvas

interface DataShowViewShape {
    val dataList:ArrayList<ShowData>
    var nowAngle:Float
    var totalAngle:Float
    fun draw(canvas: Canvas,view: DataShowView)
    fun init()
}