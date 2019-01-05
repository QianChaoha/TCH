package com.uhf.uhf.activity;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.administrator.baselib.base.BaseActivity;
import com.google.gson.JsonObject;
import com.tamic.novate.Throwable;
import com.uhf.uhf.R;
import com.uhf.uhf.UHFApplication;
import com.uhf.uhf.adapter.GetPanAdapter;
import com.uhf.uhf.bean.AssertBean;
import com.uhf.uhf.bean.AssertItemBean;
import com.uhf.uhf.http.CallBack;
import com.uhf.uhf.http.HttpUrl;
import com.uhf.uhf.http.HttpUtils;
import com.uhf.uhf.util.LoaddingUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Description:
 * Data: 2019/1/2
 *
 * @author: cqian
 */
public class GetDataActivity extends BaseActivity {
    @Bind(R.id.recycleView)
    RecyclerView mRecyclerView;
    List<AssertItemBean> mDatas = new ArrayList<AssertItemBean>();
    private GetPanAdapter mGetPanAdapter;
    LoaddingUtils mLoaddingUtils;

    public static void startGetDataActivity(Context context) {
        Intent intent = new Intent(context, GetDataActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        mGetPanAdapter = new GetPanAdapter(mActivity, mDatas);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mGetPanAdapter);
        mLoaddingUtils=new LoaddingUtils(mActivity);
        mLoaddingUtils.show();
    }

    @Override
    protected void setViewData() {

    }

    @Override
    protected void getData() {

        HttpUtils.doGet(HttpUrl.GET_ASSERT, null, new CallBack<AssertBean>() {
            @Override
            public void onSuccess(AssertBean data) {
                mLoaddingUtils.dismiss();
                if (data != null && data.result != null && data.result.size() > 0) {
                    AssertBean.ResultBean resultBean = data.result.get(0);
                    if (resultBean != null && resultBean.assetCheckDetails != null) {
                        for (int i = 0; i < resultBean.assetCheckDetails.size() && resultBean.assetCheckDetails.get(i) != null; i++) {
                            mDatas.add(new AssertItemBean(resultBean.assetCheckDetails.get(i).assetCode, "", ""));
                        }
                        mGetPanAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailed(Throwable e) {
                mLoaddingUtils.dismiss();
            }

        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_get_data;
    }
}
