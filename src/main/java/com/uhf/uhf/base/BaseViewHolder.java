package com.uhf.uhf.base;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by cqian on 2018/6/13.
 * 通用的ViewHolder
 */

public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

    // SparseArray 比 HashMap 更省内存，在某些条件下性能更好，只能存储 key 为 int 类型的数据，
    // 用来存放 View 以减少 findViewById 的次数
    private SparseArray<View> viewSparseArray;

    private onItemCommonClickListener commonClickListener;
    private onItemCommonLongClickListener commonLongClickListener;

    public BaseViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        viewSparseArray = new SparseArray<>();
    }

    /**
     * 根据 ID 来获取 View
     *
     * @param viewId viewID
     * @param <T>    泛型
     * @return 将结果强转为 View 或 View 的子类型
     */
    public <T extends View> T getView(int viewId) {
        // 先从缓存中找，找打的话则直接返回
        // 如果找不到则 findViewById ，再把结果存入缓存中
        View view = viewSparseArray.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            viewSparseArray.put(viewId, view);
        }
        return (T) view;
    }

    public BaseViewHolder setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        if (text == null) {
            tv.setText("");
        } else {
            tv.setText(text);
        }
        return this;
    }
    public BaseViewHolder setText(int viewId, CharSequence text,int color ) {
        TextView tv = getView(viewId);
        if (text == null) {
            tv.setText("");
        } else {
            tv.setText(text);
            tv.setBackgroundColor(color);
        }
        return this;
    }
    public BaseViewHolder setTextWithDefault(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        if (TextUtils.isEmpty(text)) {
            tv.setText("0");
        } else {
            tv.setText(text);
        }
        return this;
    }

    public BaseViewHolder setViewVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

    public BaseViewHolder setImageResource(int viewId, int resourceId) {
        ImageView imageView = getView(viewId);
        imageView.setBackgroundResource(resourceId);
        return this;
    }

    public interface onItemCommonClickListener {

        void onItemClickListener(View view, int position);
    }

    public interface onItemCommonLongClickListener {

        void onItemLongClickListener(View view, int position);
    }

    /**
     * Item点击事件
     *
     * @param commonClickListener
     */
    public void setOnItemClickLitener(onItemCommonClickListener commonClickListener) {
        this.commonClickListener = commonClickListener;
    }

    /**
     * Item长按事件
     *
     * @param commonClickListener
     */
    public void setOnItemLongClickLitener(onItemCommonLongClickListener commonClickListener) {
        this.commonLongClickListener = commonClickListener;
    }

    @Override
    public void onClick(View v) {
        if (commonClickListener != null) {
            commonClickListener.onItemClickListener(v, getLayoutPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (commonLongClickListener != null) {
            commonLongClickListener.onItemLongClickListener(v, getLayoutPosition());
        }
        return false;
    }
}
