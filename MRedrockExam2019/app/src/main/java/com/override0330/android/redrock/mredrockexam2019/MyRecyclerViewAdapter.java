package com.override0330.android.redrock.mredrockexam2019;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.override0330.android.redrock.mredrockexam2019.imagetool.cache.DoubleCacheUtils;
import com.override0330.android.redrock.mredrockexam2019.imagetool.cache.MemoryCacheUtils;
import com.override0330.android.redrock.mredrockexam2019.imagetool.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    private int itemAddress;
    private ArrayList list = new ArrayList();
    private initViewHolder init;

    public  MyRecyclerViewAdapter(int itemAddress,ArrayList list) {
        this.itemAddress = itemAddress;
        this.list = list;
    }

    public void setInit(initViewHolder init) {
        this.init = init;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (itemAddress==0) return null;
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemAddress,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        init.init(viewHolder,i);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View itemView;
        @SuppressLint("UseSparseArrays")
        private HashMap<Integer,View> viewHashMap = new HashMap<>();
        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        /**
         * 根据ViewId获得相对应的控件
         * @param viewId
         * @param <T>
         * @return
         */
        private  < T extends View> T getView(int viewId){
            View view = viewHashMap.get(viewId);
            if (view==null){
                view = itemView.findViewById(viewId);
                viewHashMap.put(viewId,view);
            }
            return (T) view;
        }

        /**
         * 设置控件的初始属性
         * @param viewID
         * @param text
         * @return
         */
        public ViewHolder setTextViewText(int viewID, String text){
            TextView textView = getView(viewID);
            textView.setText(text);
            return this;
        }

        public ViewHolder setButtonText(int viewId, String text){
            Button button = getView(viewId);
            button.setText(text);
            return this;
        }

        public ViewHolder setImageResource(int viewId, int resource){
            ImageView imageView = getView(viewId);
            imageView.setImageResource(resource);
            return this;
        }

        public ViewHolder setImageBitmap(int viewId, Bitmap bitmap){
            ImageView imageView = getView(viewId);
            imageView.setImageBitmap(bitmap);
            return this;
        }
        //这里导入自己的那个图片加载类
        public ViewHolder setImageUrl(int viewId, String url, Activity activity){
            ImageView imageView = getView(viewId);
            ImageLoader.with(activity).cacheWith(DoubleCacheUtils.getInstance()).from(url).into(imageView);
            return this;
        }

        public ViewHolder setOnClickListener(int viewId,View.OnClickListener onClickListener){
            View view = getView(viewId);
            view.setOnClickListener(onClickListener);
            return this;
        }

    }
    public interface initViewHolder{
        void init(ViewHolder viewHolder, int position);
    }
}
