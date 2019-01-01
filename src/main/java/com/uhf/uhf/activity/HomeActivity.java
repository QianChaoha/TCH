package com.uhf.uhf.activity;


import android.content.Intent;
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
import com.uhf.uhf.ConnectRs232;
import com.uhf.uhf.MainActivity;
import com.uhf.uhf.R;
import com.uhf.uhf.util.GetPermissionUtil;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

/**
 * Description:
 * Data: 2018/12/27
 *
 * @author: cqian
 */
public class HomeActivity extends BaseActivity {
    private SerialPortFinder mSerialPortFinder;
    String[] entries = null;
    String[] entryValues = null;
    private int baud = 115200;
    private List<String> mPortList = new ArrayList<String>();
    private static final String TTYS1 = "ttyS4 (rk_serial)";
    private int mPosPort = 0;
    public static SerialPort mSerialPort = null;
    private ReaderHelper mReaderHelper;
    private CodeReaderHelper mCodeReaderHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
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


    @OnClick({R.id.btSetting, R.id.btTagRw})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btSetting:
                SettingActivity.startSettingActivity(this);
                break;
            case R.id.btTagRw:
                OpenScanUtils.openScan(mActivity);
                if (OpenTTFUtils.openUHF(mActivity)) {
                    OperateTagActivity.startOperateTagActivity(this);
                }
                break;
        }
    }

    private boolean connect() {
        if (mPortList.indexOf(TTYS1) >= 0)
            mPosPort = mPortList.indexOf(TTYS1);

        if (entryValues == null || entryValues.length == 0) {
            Toast.makeText(
                    this,
                    "设备不可用",
                    Toast.LENGTH_SHORT).show();
            return false;

        }
        if (mPosPort < 0) {
            Toast.makeText(
                    this,
                    getResources().getString(R.string.rs232_error),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        try {

            mSerialPort = new SerialPort(new File(entryValues[mPosPort]), baud, 0);


            try {
                mReaderHelper = ReaderHelper.getDefaultHelper();
                mReaderHelper.setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream());

//                mCodeReaderHelper = CodeReaderHelper.getDefaultHelper();
//                mCodeReaderHelper.setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream());

            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }
            try {
                if (ModuleManager.newInstance().getScanStatus()) {
                    ModuleManager.newInstance().setScanStatus(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"摄像头被占用",Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!ModuleManager.newInstance().setUHFStatus(true)) {
                throw new RuntimeException("Scan power on failure,may you open in other" +
                        "Process and do not exit it");
            }
            return true;
            //finish();
        } catch (SecurityException e) {
            Toast.makeText(
                    this,
                    getResources().getString(R.string.error_security),
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(
                    this,
                    getResources().getString(R.string.error_unknown),
                    Toast.LENGTH_SHORT).show();
        } catch (InvalidParameterException e) {
            Toast.makeText(
                    this,
                    getResources().getString(R.string.error_configuration),
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
