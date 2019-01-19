package com.uhf.uhf.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.baselib.base.BaseActivity;
import com.example.administrator.baselib.interfaces.BaseDialogInterface;
import com.example.administrator.baselib.util.JsonParserUtil;
import com.example.administrator.baselib.util.TopTitleUtils;
import com.example.administrator.baselib.view.dialog.CommenDialog;
import com.reader.code.OpenTTFUtils;
import com.tamic.novate.Throwable;
import com.uhf.uhf.R;
import com.uhf.uhf.base.BaseRecycleAdapter;
import com.uhf.uhf.base.BaseViewHolder;
import com.uhf.uhf.bean.AssertBean;
import com.uhf.uhf.bean.AssertItemBean;
import com.uhf.uhf.bean.FilterUnfinishedAssetChecksBean;
import com.uhf.uhf.bean.GetAssetCheckDetailsBean;
import com.uhf.uhf.http.CallBack;
import com.uhf.uhf.http.HttpUrl;
import com.uhf.uhf.http.HttpUtils;
import com.uhf.uhf.util.DateUtil;
import com.uhf.uhf.util.LoaddingUtils;
import com.uhf.uhf.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.uhf.uhf.util.SharedPreferencesUtils.GET_PAN_DATA;

/**
 * Description:
 * Data: 2019/1/7
 *
 * @author: cqian
 */
public class GetPlanActivity extends BaseActivity {
    LoaddingUtils mLoaddingUtils;
    @Bind(R.id.tvTitle)
    TextView mTvTitle;
    @Bind(R.id.centerContent)
    RelativeLayout centerContent;
    @Bind(R.id.tvTime)
    TextView mTvTime;
    @Bind(R.id.arrow)
    View arrow;
    @Bind(R.id.recycleView)
    RecyclerView mRecyclerView;
    public List<FilterUnfinishedAssetChecksBean.ResultBean> result = new ArrayList<FilterUnfinishedAssetChecksBean.ResultBean>();
    private MyAdapter mMyAdapter;
    int mPosition = -1;
    CommenDialog mCommenDialog;

    @Override
    protected void initView() {
        new TopTitleUtils(this).setTitle("获取计划").setLeft(null);
        mLoaddingUtils = new LoaddingUtils(mActivity);
    }

    @Override
    protected void setViewData() {
        mCommenDialog = new CommenDialog(mActivity);
        mCommenDialog.setBottomOneButton();
        mCommenDialog.setDialogInterface(new BaseDialogInterface() {
            @Override
            public void ok(View view) {
                setResult(RESULT_OK);
                mCommenDialog.dismiss();
                finish();
            }

            @Override
            public void cancle(View view) {
                mCommenDialog.dismiss();
            }
        });
        mCommenDialog.setTitle("下载成功");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mMyAdapter = new MyAdapter(mActivity, result);
        mRecyclerView.setAdapter(mMyAdapter);

        mMyAdapter.setOnItemClickLitener(new BaseViewHolder.onItemCommonClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                mPosition = position;
                mTvTitle.setText(result.get(position).name);
                mTvTime.setText(DateUtil.DateTZ2Normal(result.get(position).deadDate));
                mRecyclerView.setVisibility(View.GONE);
                arrow.setBackgroundResource(R.drawable.arrow_down);
            }
        });
    }

    @Override
    protected void getData() {

    }

    @OnClick({R.id.tvGetData, R.id.centerContent, R.id.tvDownLoad, R.id.tvCancel})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.tvDownLoad:
                if (mPosition < 0) {
                    Toast.makeText(mActivity, "请选择盘点任务", Toast.LENGTH_SHORT).show();
                    return;
                }
                mLoaddingUtils.show();
                HashMap map = new HashMap();
                map.put("assetCheckId", result.get(mPosition).id + "");
                HttpUtils.doGet(HttpUrl.GetAssetCheckDetails, map, new CallBack<GetAssetCheckDetailsBean>() {
                    @Override
                    public void onSuccess(GetAssetCheckDetailsBean data) {
                        if (data != null) {
                            data.id = result.get(mPosition).id;
                            String string = JsonParserUtil.serializeToJson(data);
                            SharedPreferencesUtils.putString(mActivity, GET_PAN_DATA, string);
                            mCommenDialog.setContent(DateUtil.DateTZ2Normal(result.get(mPosition).deadDate) + " 的盘点任务已下载");
                            mCommenDialog.show();
                        }
                        mLoaddingUtils.dismiss();
                    }

                    @Override
                    public void onFailed(Throwable e) {
                        mLoaddingUtils.dismiss();
                    }
                });
                break;
            case R.id.centerContent:
                if (mRecyclerView.getVisibility() == View.GONE) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    arrow.setBackgroundResource(R.drawable.arrow_up);
                    if (result.size() == 0) {
                        getPlanData();
                    }

                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    arrow.setBackgroundResource(R.drawable.arrow_down);
                }
                break;
            case R.id.tvGetData:
                getPlanData();
                break;
            case R.id.tvCancel:
                if (OpenTTFUtils.openUHF(mActivity)) {
                    startActivity(new Intent(this, QunduActivity.class));
                }
                break;
        }
    }

    private void getPlanData() {
        mLoaddingUtils.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("deadDate", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.doPost(HttpUrl.FilterUnfinishedAssetChecks, jsonObject, new CallBack<FilterUnfinishedAssetChecksBean>() {
            @Override
            public void onSuccess(FilterUnfinishedAssetChecksBean data) {
                mRecyclerView.setVisibility(View.VISIBLE);
                arrow.setBackgroundResource(R.drawable.arrow_up);
                mLoaddingUtils.dismiss();
                result.clear();
                if (data != null && data.result != null && data.result.size() > 0) {
                    result.addAll(data.result);
                    mMyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(Throwable e) {
                mLoaddingUtils.dismiss();
            }

        });
    }

    class MyAdapter extends BaseRecycleAdapter<FilterUnfinishedAssetChecksBean.ResultBean> {

        public MyAdapter(Context context, List<FilterUnfinishedAssetChecksBean.ResultBean> dataList) {
            super(context, dataList);
        }

        @Override
        public void bindData(BaseViewHolder holder, FilterUnfinishedAssetChecksBean.ResultBean data, int position) {
            holder.setText(R.id.tv, data.name);
            holder.setText(R.id.tvTime, DateUtil.DateTZ2Normal(data.deadDate));
        }

        @Override
        public int getLayoutId(int viewType) {
            return R.layout.get_data_item;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_get_plan;
    }
}
