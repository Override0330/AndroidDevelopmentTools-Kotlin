package com.override0330.android.redrock.mredrockexam2019

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet

class RecyclerViewMyLinearLayoutManager:LinearLayoutManager {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try{
            super.onLayoutChildren(recycler, state)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }

    override fun canScrollVertically(): Boolean {
        return false
    }
}
