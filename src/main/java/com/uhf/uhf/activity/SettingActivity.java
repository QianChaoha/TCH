package com.uhf.uhf.activity;

import android.content.Context;
import android.content.Intent;

import com.example.administrator.baselib.base.BaseActivity;
import com.uhf.uhf.R;

/**
 * Description:
 * Data: 2018/12/28
 *
 * @author: cqian
 */
public class SettingActivity extends BaseActivity {
    public static void startSettingActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void initView() {

    }

    @Override
    protected void setViewData() {

    }

    @Override
    protected void getData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }
}