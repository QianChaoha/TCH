package com.uhf.uhf.activity;


import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.baselib.base.BaseActivity;
import com.example.administrator.baselib.util.TopTitleUtils;
import com.uhf.uhf.R;
import com.uhf.uhf.bean.AssertItemBean;
import com.uhf.uhf.http.HttpUrl;
import com.uhf.uhf.util.DateUtil;

/**
 * Description:
 * Data: 2019/1/19
 *
 * @author: cqian
 */
public class AssertDetailActivity extends BaseActivity {
    @Override
    protected void initView() {
        new TopTitleUtils(this).setTitle("资产详情").setLeft(null);
        AssertItemBean assertItemBean = (AssertItemBean) getIntent().getSerializableExtra("data");
        if (assertItemBean != null) {
            setText(R.id.tvAssertCode, assertItemBean.epcData);
            setText(R.id.tvAssertName, assertItemBean.assetName);
            setText(R.id.tvAssertLoc, assertItemBean.mAssetLocationName);
            setText(R.id.tvPrice, assertItemBean.price + "");
            setText(R.id.tvGys, assertItemBean.supplier);
            setText(R.id.tvDate, DateUtil.DateTZ2Normal(assertItemBean.inDate));
            setText(R.id.tvLife, assertItemBean.expireMonth + "月");
            setText(R.id.tvMark, assertItemBean.note);
            Glide.with(mActivity).load(HttpUrl.SERVER_URL + assertItemBean.imgPath)
                    .placeholder(R.drawable.default_pic).into((ImageView) findViewById(R.id.imageView));
        }

    }

    @Override
    protected void setViewData() {

    }

    @Override
    protected void getData() {

    }

    public void setText(int id, String text) {
        ((TextView) findViewById(id)).setText(text);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_assert_detail;
    }
}
