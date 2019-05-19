package com.override0330.android.redrock.customview

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log

class CircleShape(override val dataList: ArrayList<ShowData>) :DataShowViewShape{
    override var totalAngle:Float = 0F
    override var nowAngle:Float = 0F
    private val paint = Paint()
    override fun init(){
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        for (i in 0 until dataList.size){
            totalAngle += dataList[i].angle
        }
    }

    override fun draw(canvas: Canvas, view: DataShowView) {
        canvas.translate(view.width/2F,view.height/2F)
        val r = Math.min(view.width,view.height)/2*0.8F
        val rect = RectF(-r,-r,r,r)
        for (i in 0 until dataList.size){
            val showData = dataList[i]
            paint.color = showData.color
            when{
                nowAngle>=showData.startAngle&&nowAngle<showData.startAngle+showData.angle ->{
                    //正在绘制的图
                    Log.d("正在绘图","position: $i, startAngle: ${showData.startAngle}, sweepAngle: ${nowAngle-showData.startAngle}, nowAngle: $nowAngle")
                    canvas.drawArc(rect,showData.startAngle,nowAngle-showData.startAngle,true,paint)
                }
                nowAngle>=showData.startAngle+showData.angle ->{
                    //已经绘制的图
                    Log.d("已经画过的图","position: $i, startAngle: ${showData.startAngle},sweepAngle: ${showData.angle}, nowAngle: $nowAngle")
                    canvas.drawArc(rect,showData.startAngle,showData.angle,true,paint)
                }
            }

//            val stopAngle = nowAngle
//            if (showData.startAngle>stopAngle)continue
//            else if (showData.nowAngle >= showData.endAngle) {
//                canvas.drawArc(rect, showData.startAngle, showData.endAngle, true, paint)
////                stopAngle -= Math.abs(showData.endAngle-showData.startAngle)
//                Log.d("这个图已经画过了", "$i")
//                continue
//            }
//            if (stopAngle>=showData.endAngle){
//                stopAngle = showData.endAngle
//            }
//            canvas.drawArc(rect,showData.startAngle,stopAngle,true,paint)
//            Log.d("图像绘制，index:","$i 从${showData.startAngle}到$stopAngle")
//            showData.nowAngle =stopAngle
        }
    }
}