package com.override0330.android.redrock.mredrockexam2019

import android.graphics.*

class ImageTools{
    companion object{
        fun cutToCircle(bitmap: Bitmap):Bitmap{
            var r = bitmap.width
            if (bitmap.height<bitmap.width){
                r=bitmap.height
            }
            val newBitmap = Bitmap.createBitmap(r,r,Bitmap.Config.ARGB_8888)
            val canvas = Canvas(newBitmap)
            val paint = Paint()
            paint.isAntiAlias = true
            val rectF = RectF(0F,0F,r.toFloat(),r.toFloat())
            canvas.drawRoundRect(rectF,r/2F,r/2F,paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, null, rectF, paint)
            return newBitmap
        }
    }
}