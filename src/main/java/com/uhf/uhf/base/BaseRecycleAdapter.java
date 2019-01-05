package com.uhf.uhf.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cqian on 2018/6/13.
 * 通用的RecyclerView.Adapter
 */

public abstract class BaseRecycleAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    protected LayoutInflater layoutInflater;

    protected Context mContext;
    protected List<T> mDataList = new ArrayList<>();


    protected BaseViewHolder.onItemCommonClickListener mClickListener;
    protected BaseViewHolder.onItemCommonLongClickListener mLongClickListener;

    public BaseRecycleAdapter(Context context, List<T> dataList) {
        this.layoutInflater = LayoutInflater.from(context);
        mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(getLayoutId(viewType), parent, false);
        BaseViewHolder viewHolder = new BaseViewHolder(itemView);
        if (mClickListener != null) {
            viewHolder.setOnItemClickLitener(mClickListener);
        }
        if (mLongClickListener != null) {
            viewHolder.setOnItemLongClickLitener(mLongClickListener);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        bindData(holder, mDataList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return (mDataList != null && mDataList.size() > 0) ? mDataList.size() : 0;
    }

    public void setOnItemClickLitener(BaseViewHolder.onItemCommonClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setOnLongClickLitener(BaseViewHolder.onItemCommonLongClickListener commonClickListener) {
        this.mLongClickListener = commonClickListener;
    }

    public void updateData(List<T> dataList) {
        this.mDataList = dataList;
        notifyDataSetChanged();
    }


    public abstract void bindData(BaseViewHolder holder, T data, int position);



    public abstract int getLayoutId(int viewType);

    public List<T> getDataList() {
        return mDataList;
    }

    public void setDataList(List<T> dataList) {
        this.mDataList = dataList;
    }

    public T getItem(int position) {
        return mDataList.get(position);
    }
}
