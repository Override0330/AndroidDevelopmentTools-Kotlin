package com.override0330.android.redrock.mredrockexam2019.activity

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class LyricAdapter: RecyclerView.Adapter<LyricAdapter.ViewHolder> {
    var itemId = 0
    lateinit var init:InitViewHolder
    var list:ArrayList<String>
    constructor(itemId:Int,list:ArrayList<String>){
        this.itemId = itemId
        this.list = list
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(itemId,p0,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        init.init(p0,p1)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewHashMap = HashMap<Int,View>()

        private fun <T : View> getView(viewId: Int): T {
            var view = viewHashMap.get(viewId)
            if (view == null) {
                view = itemView.findViewById(viewId)
                viewHashMap.put(viewId, view)
            }
            return view as T
        }

        fun setTextView(viewId:Int, content:String):ViewHolder{
            val view:TextView = getView(viewId)
            view.text = content
            return this
        }

    }
    interface InitViewHolder{
        fun init(viewHolder: ViewHolder,position:Int)
    }
    fun setInitViewHolder(initViewHolder: InitViewHolder){
        this.init = initViewHolder
    }
}