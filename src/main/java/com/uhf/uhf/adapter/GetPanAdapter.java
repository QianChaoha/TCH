package com.uhf.uhf.adapter;

import android.content.Context;
import android.graphics.Color;

import com.uhf.uhf.R;
import com.uhf.uhf.base.BaseRecycleAdapter;
import com.uhf.uhf.base.BaseViewHolder;
import com.uhf.uhf.bean.AssertItemBean;

import java.util.List;

/**
 * Description:
 * Data: 2019/1/5
 *
 * @author: cqian
 */
public class GetPanAdapter extends BaseRecycleAdapter<AssertItemBean> {
    public GetPanAdapter(Context context, List<AssertItemBean> dataList) {
        super(context, dataList);
    }

    @Override
    public void bindData(BaseViewHolder holder, AssertItemBean data, int position) {
        int color = Color.WHITE;
        if (data.state == 1) {
            color = mContext.getResources().getColor(R.color.green);
        } else if (data.state == 2) {
            color = Color.RED;
        }
        holder.setText(R.id.tvNo, (position + 1) + "", color)
                .setText(R.id.tvEpc, data.epcData, color)
                .setText(R.id.tvCount, data.count, color)
                .setText(R.id.tvRssi, data.rssi, color)
                .setText(R.id.tvLocation, data.mAssetLocationName, color);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.get_plan_item;
    }
}
