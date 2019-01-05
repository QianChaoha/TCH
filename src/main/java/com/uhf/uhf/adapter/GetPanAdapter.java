package com.uhf.uhf.adapter;

import android.content.Context;

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
        holder.setText(R.id.tvNo, (position + 1) + "")
                .setText(R.id.tvEpc, data.epcData)
                .setText(R.id.tvCount, data.count)
                .setText(R.id.tvRssi,  data.rssi);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.get_plan_item;
    }
}
