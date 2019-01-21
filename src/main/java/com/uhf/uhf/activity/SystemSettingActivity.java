package com.uhf.uhf.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.com.tools.Beeper;
import com.example.administrator.baselib.base.BaseActivity;
import com.example.administrator.baselib.util.TopTitleUtils;
import com.reader.base.CMD;
import com.reader.base.ReaderBase;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;
import com.uhf.uhf.R;
import com.uhf.uhf.spiner.AbstractSpinerAdapter;
import com.uhf.uhf.spiner.SpinerPopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.com.tools.Beeper.mQuite;

/**
 * Description:
 * Data: 2019/1/18
 *
 * @author: cqian
 */
public class SystemSettingActivity extends BaseActivity {
    private TableRow mDropDownRow;
    private List<String> mList = new ArrayList<String>();
    private SpinerPopWindow mSpinerPopWindow;
    private TextView mTagAccessListText;
    private int mPos = -1;
    private ReaderBase mReader;
    private ReaderHelper mReaderHelper;
    private static ReaderSetting m_curReaderSetting;
    private LocalBroadcastManager lbm;
    RadioGroup mRadioGroup;
    HashMap<String, String> mHashMap = new HashMap<String, String>();
    LinearLayout mLlBottom;

    @Override
    protected void onResume() {
        if (mReader != null) {
            if (!mReader.IsAlive())
                mReader.StartWait();
        }
        super.onResume();
    }

    @Override
    protected void initView() {
        new TopTitleUtils(this).setTitle("系统设置");
        for (int i = 5; i <= 30; i = i + 5) {
            mList.add(i + "");
        }

        try {
            mReaderHelper = ReaderHelper.getDefaultHelper();
            mReader = mReaderHelper.getReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        m_curReaderSetting = mReaderHelper.getCurReaderSetting();
        mDropDownRow = (TableRow) findViewById(R.id.table_row_tag_access_list);
        mSpinerPopWindow = new SpinerPopWindow(mActivity);
        mSpinerPopWindow.refreshData(mList, 0);
        mTagAccessListText = (TextView) findViewById(R.id.tag_access_list_text);
        mLlBottom = findViewById(R.id.llBottom);
        mSpinerPopWindow.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
            public void onItemClick(int pos) {
                setAccessSelectText(pos);
            }
        });
        mDropDownRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSpinerPopWindow.refreshData(mList, 0);
                mSpinerPopWindow.setWidth(mDropDownRow.getWidth());
                mSpinerPopWindow.showAsDropDown(mDropDownRow);
            }
        });
        mRadioGroup = ((RadioGroup) findViewById(R.id.rg));
        lbm = LocalBroadcastManager.getInstance(this);

        final IntentFilter itent = new IntentFilter();
        itent.addAction(ReaderHelper.BROADCAST_REFRESH_READER_SETTING);
        itent.addAction(ReaderHelper.BROADCAST_WRITE_LOG);

        lbm.registerReceiver(mRecv, itent);
        mRadioGroup.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateView();
            }
        }, 500);


        findViewById(R.id.tvSetConfig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置输出功率
                byte btOutputPower = 0x00;
                try {
                    btOutputPower = (byte) Integer.parseInt(mList.get(mPos));
                } catch (Exception e) {
                }

                int i1 = mReader.setOutputPower(m_curReaderSetting.btReadId, btOutputPower);
                m_curReaderSetting.btAryOutputPower = new byte[]{btOutputPower};


                //设置蜂鸣器
                byte btMode = 0;
                Beeper.BeepMode beepMode;
                switch (mRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.rbOn:
                        btMode = 0;
                        mQuite = false;
                        break;
                    case R.id.rbOff:
                        btMode = 1;
                        mQuite = true;
                        break;
                }
                int i2 = mReader.setBeeperMode(m_curReaderSetting.btReadId, btMode);
                m_curReaderSetting.btBeeperMode = btMode;

                if (i1 == 0 && i2 == 0) {
                    Toast.makeText(mActivity, "设置成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.getInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHashMap.clear();

                mReader.getFirmwareVersion(m_curReaderSetting.btReadId);
            }
        });
    }

    private void updateView() {
        mReader.getOutputPower(m_curReaderSetting.btReadId);

        if (m_curReaderSetting.btBeeperMode == 0) {
            mRadioGroup.check(R.id.rbOn);
        } else if (m_curReaderSetting.btBeeperMode == 1) {
            mRadioGroup.check(R.id.rbOff);
        }
    }

    private void setAccessSelectText(int pos) {
        if (pos >= 0 && pos < mList.size()) {
            String value = mList.get(pos);
            mTagAccessListText.setText(value);
            mPos = pos;
        }
    }

    private final BroadcastReceiver mRecv = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_READER_SETTING)) {
                byte btCmd = intent.getByteExtra("cmd", (byte) 0x00);

                if (btCmd == CMD.GET_OUTPUT_POWER || btCmd == CMD.SET_OUTPUT_POWER) {
                    if (m_curReaderSetting.btAryOutputPower != null) {
                        String text=String.valueOf(m_curReaderSetting.btAryOutputPower[0] & 0xFF);
                        if (mList.contains(text)){
                            mPos=mList.indexOf(text);
                        }
                        mTagAccessListText.setText(text);
                    }
                }
                if (btCmd == CMD.GET_READER_TEMPERATURE) {
                    String strTemperature = "";
                    if (m_curReaderSetting.btPlusMinus == 0x0) {
                        strTemperature = "-" + String.valueOf(m_curReaderSetting.btTemperature & 0xFF) + "°C";
                    } else {
                        strTemperature = String.valueOf(m_curReaderSetting.btTemperature & 0xFF) + "°C";
                    }
                    mHashMap.put("工作温度", strTemperature);
                    mReader.getAntConnectionDetector(m_curReaderSetting.btReadId);
                }
                if (btCmd == CMD.GET_FIRMWARE_VERSION) {
                    mReader.getReaderTemperature(m_curReaderSetting.btReadId);
                    mHashMap.put("固件版本", String.valueOf(m_curReaderSetting.btMajor & 0xFF) + "." + String.valueOf(m_curReaderSetting.btMinor & 0xFF));
                }

                if (btCmd == CMD.GET_ANT_CONNECTION_DETECTOR || btCmd == CMD.SET_ANT_CONNECTION_DETECTOR) {
                    mHashMap.put("天线灵敏度", String.valueOf(m_curReaderSetting.btAntDetector & 0xFF) + "dB");
                    mReader.getFrequencyRegion(m_curReaderSetting.btReadId);
                }

                if (btCmd == CMD.SET_FREQUENCY_REGION || btCmd == CMD.GET_FREQUENCY_REGION) {
                    switch (m_curReaderSetting.btRegion & 0xFF) {
                        case 0x01:
                            mHashMap.put("射频频谱", "默认频谱FCC");
                            break;
                        case 0x02:
                            mHashMap.put("射频频谱", "默认频谱ETSI");
                            break;
                        case 0x03:
                            mHashMap.put("射频频谱", "默认频谱CHN");
                            break;
                        case 0x04:
                            mHashMap.put("射频频谱", "自定义频谱");
                            break;
                        default:
                            break;
                    }
                    mReader.getRfLinkProfile(m_curReaderSetting.btReadId);
                }
                if (btCmd == CMD.GET_RF_LINK_PROFILE || btCmd == CMD.SET_RF_LINK_PROFILE) {
                    if ((m_curReaderSetting.btRfLinkProfile & 0xFF) == 0xD0) {
                        mHashMap.put("射频频谱", "Tari 25uS; FM0 40KHz");
                    } else if ((m_curReaderSetting.btRfLinkProfile & 0xFF) == 0xD1) {
                        mHashMap.put("射频频谱", "Tari 25uS; Miller 4 250KHz");
                    } else if ((m_curReaderSetting.btRfLinkProfile & 0xFF) == 0xD2) {
                        mHashMap.put("射频频谱", "Tari 25uS; Miller 4 300KHz");
                    } else if ((m_curReaderSetting.btRfLinkProfile & 0xFF) == 0xD3) {
                        mHashMap.put("射频频谱", "Tari 6.25uS; FM0 400KHz");
                    }
                    Iterator iter = mHashMap.entrySet().iterator();
                    mLlBottom.removeAllViews();
                    while (iter.hasNext()) {
                        Map.Entry<String, String> entry = (Map.Entry) iter.next();
                        if (!TextUtils.isEmpty(entry.getKey()) && !TextUtils.isEmpty(entry.getValue())) {
                            TextView textView = new TextView(mActivity);
                            textView.setText(entry.getKey() + ":" + entry.getValue());
                            textView.setPadding(20, 10, 20, 10);
                            mLlBottom.addView(textView);
                        }
                    }
                }
            } else if (intent.getAction().equals(ReaderHelper.BROADCAST_WRITE_LOG)) {
                //mLogList.writeLog((String) intent.getStringExtra("log"), intent.getIntExtra("type", ERROR.SUCCESS));
            }
        }
    };

    @Override
    protected void setViewData() {

    }

    @Override
    protected void getData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (lbm != null)
            lbm.unregisterReceiver(mRecv);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_system_setting;
    }
}
