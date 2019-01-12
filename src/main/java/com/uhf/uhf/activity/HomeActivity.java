package com.uhf.uhf.activity;


import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.baselib.base.BaseActivity;
import com.nativec.tools.ModuleManager;
import com.nativec.tools.SerialPort;
import com.nativec.tools.SerialPortFinder;
import com.reader.code.OpenScanUtils;
import com.reader.code.OpenTTFUtils;
import com.reader.code.helper.CodeReaderHelper;
import com.reader.helper.ReaderHelper;
import com.tamic.novate.Throwable;
import com.uhf.uhf.ConnectRs232;
import com.uhf.uhf.MainActivity;
import com.uhf.uhf.R;
import com.uhf.uhf.bean.TokenBean;
import com.uhf.uhf.http.CallBack;
import com.uhf.uhf.http.HttpUrl;
import com.uhf.uhf.http.HttpUtils;
import com.uhf.uhf.util.GetPermissionUtil;
import com.uhf.uhf.util.LoaddingUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

import static com.uhf.uhf.UHFApplication.mUserBean;

/**
 * Description:
 * Data: 2018/12/27
 *
 * @author: cqian
 */
public class HomeActivity extends BaseActivity {
    LoaddingUtils mLoaddingUtils;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        mLoaddingUtils = new LoaddingUtils(mActivity);
        mLoaddingUtils.show();

    }

    @Override
    protected void setViewData() {

    }

    @Override
    protected void getData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("usernameOrEmailAddress", "admin");
            jsonObject.put("password", "123qwe");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.doPost(HttpUrl.TOKEN_AUTH, jsonObject, new CallBack<TokenBean>() {

            @Override
            public void onSuccess(TokenBean data) {
                mUserBean = data.result;
                mLoaddingUtils.dismiss();
            }

            @Override
            public void onFailed(Throwable e) {
                mLoaddingUtils.dismiss();
            }
        });
    }


    @OnClick({R.id.btSetting, R.id.btTagRw, R.id.btGet, R.id.btQun})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btQun:
                if (OpenTTFUtils.openUHF(mActivity)) {
                    startActivity(new Intent(this, QunduActivity.class));
                }
                break;
            case R.id.btGet:
                if (OpenTTFUtils.openUHF(mActivity)) {
                    GetDataActivity.startGetDataActivity(this);
                }
                break;
            case R.id.btSetting:
                //SettingActivity.startSettingActivity(this);
                //startActivity(new Intent(mActivity, ConnectRs232.class));
                break;
            case R.id.btTagRw:
                OpenScanUtils.openScan(mActivity);
                if (OpenTTFUtils.openUHF(mActivity)) {
                    OperateTagActivity.startOperateTagActivity(this);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplication().onTerminate();
    }
}
