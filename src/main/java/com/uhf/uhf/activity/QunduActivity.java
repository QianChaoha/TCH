package com.uhf.uhf.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.com.tools.Beeper;
import com.example.administrator.baselib.base.BaseActivity;
import com.example.administrator.baselib.util.JsonParserUtil;
import com.example.administrator.baselib.util.TopTitleUtils;
import com.nativec.tools.ModuleManager;
import com.reader.base.CMD;
import com.reader.base.ReaderBase;
import com.reader.helper.InventoryBuffer;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;
import com.uhf.uhf.R;
import com.uhf.uhf.TagRealList;
import com.uhf.uhf.adapter.GetPanAdapter;
import com.uhf.uhf.adapter.QunAdapter;
import com.uhf.uhf.bean.AssertItemBean;
import com.uhf.uhf.bean.GetAssetCheckDetailsBean;
import com.uhf.uhf.bean.UploadPanDataBean;
import com.uhf.uhf.util.LoaddingUtils;
import com.uhf.uhf.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.com.tools.Beeper.BEEPER_SHORT;
import static com.uhf.uhf.activity.HomeActivity.CLEAN_DATA_TIME;
import static com.uhf.uhf.util.SharedPreferencesUtils.GET_PAN_DATA;

/**
 * Description:
 * Data: 2019/1/7
 *
 * @author: cqian
 */
public class QunduActivity extends BaseActivity {
    @Bind(R.id.recycleView)
    RecyclerView mRecyclerView;
    @Bind(R.id.btPd)
    TextView mStartStop;
    @Bind(R.id.tvLogDetail)
    TextView mTvLogDetail;
    List<AssertItemBean> mDatas = new ArrayList<AssertItemBean>();
    private QunAdapter mGetPanAdapter;
    private ReaderHelper mReaderHelper;
    private LocalBroadcastManager lbm;
    private Handler mUpdateViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refreshList();
        }
    };

    private Handler mHandler = new Handler();
    private Runnable mRefreshRunnable = new Runnable() {
        public void run() {
            refreshList();
            mHandler.postDelayed(this, 2000);
        }
    };
    private Handler mLoopHandler = new Handler();
    private Runnable mLoopRunnable = new Runnable() {
        public void run() {
            mReaderHelper.runLoopInventroy();
            mLoopHandler.postDelayed(this, 2000);
        }
    };
    private boolean bTmpInventoryFlag = true;
    @Bind(R.id.tag_access_list)
    TagRealList mTagAccessList;
    private static InventoryBuffer m_curInventoryBuffer;
    private String mStrRepeat = "1";
    private static ReaderSetting m_curReaderSetting;
    private ReaderBase mReader;
    private boolean mStart;

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
        new TopTitleUtils(this).setTitle("群读").setLeft(null);
        ;
        mGetPanAdapter = new QunAdapter(mActivity, mDatas);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mGetPanAdapter);
        try {
            mReaderHelper = ReaderHelper.getDefaultHelper();
            mReader = mReaderHelper.getReader();
        } catch (Exception e) {
        }
        m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
        m_curReaderSetting = mReaderHelper.getCurReaderSetting();


        lbm = LocalBroadcastManager.getInstance(mActivity);

        IntentFilter itent = new IntentFilter();
        itent.addAction(ReaderHelper.BROADCAST_WRITE_LOG);
        itent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL);
        lbm.registerReceiver(mRecv, itent);
    }

    @Override
    protected void setViewData() {

    }

    @Override
    protected void getData() {

    }

    @OnClick({R.id.btPd, R.id.btClean})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btClean:
                if (mStart) {
                    startstop();
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDatas.clear();
                            mGetPanAdapter.notifyDataSetChanged();
                        }
                    }, CLEAN_DATA_TIME);
                } else {
                    mDatas.clear();
                    mGetPanAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btPd:
                startstop();
                break;
        }
    }

    private void startstop() {
        bTmpInventoryFlag = false;

        m_curInventoryBuffer.clearInventoryPar();

        m_curInventoryBuffer.lAntenna.add((byte) 0x00);

        if (m_curInventoryBuffer.lAntenna.size() <= 0) {
            Toast.makeText(mActivity,
                    getResources().getString(R.string.antenna_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        m_curInventoryBuffer.bLoopInventoryReal = true;
        m_curInventoryBuffer.btRepeat = 0;

        if (mStrRepeat == null || mStrRepeat.length() <= 0) {
            Toast.makeText(mActivity,
                    getResources().getString(R.string.repeat_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        m_curInventoryBuffer.btRepeat = (byte) Integer.parseInt(mStrRepeat);

        if ((m_curInventoryBuffer.btRepeat & 0xFF) <= 0) {
            Toast.makeText(mActivity,
                    getResources().getString(R.string.repeat_min),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        m_curInventoryBuffer.bLoopCustomizedSession = false;

        {
            if (!mStartStop.getText().toString()
                    .equals("群读")) {
                mReaderHelper.setInventoryFlag(false);
                m_curInventoryBuffer.bLoopInventoryReal = false;

                refreshStartStop(false);
                mLoopHandler.removeCallbacks(mLoopRunnable);
                mHandler.removeCallbacks(mRefreshRunnable);
                refreshList();
                return;
            } else {
                refreshStartStop(true);
            }
        }

        // start_fixed by lei.li 2016/11/04 problem
        // m_curInventoryBuffer.clearInventoryRealResult();
        mReaderHelper.setInventoryFlag(true);
        // end_fixed by lei.li 2016/11/04

        mReaderHelper.clearInventoryTotal();

        byte btWorkAntenna = m_curInventoryBuffer.lAntenna
                .get(m_curInventoryBuffer.nIndexAntenna);
        if (btWorkAntenna < 0)
            btWorkAntenna = 0;
        // mReader.setWorkAntenna(m_curReaderSetting.btReadId, btWorkAntenna);
        mReaderHelper.runLoopInventroy();
        m_curReaderSetting.btWorkAntenna = btWorkAntenna;
        refreshStartStop(true);
        mLoopHandler.removeCallbacks(mLoopRunnable);
        mLoopHandler.postDelayed(mLoopRunnable, 2000);
        mHandler.removeCallbacks(mRefreshRunnable);
        mHandler.postDelayed(mRefreshRunnable, 2000);
    }

    private void refreshStartStop(boolean start) {
        mStart = start;
        if (start) {
            mStartStop.setText("停止群读");
            mTvLogDetail.setText("群读中...");
        } else {
            mStartStop.setText("群读");
            mTvLogDetail.setText("群读结束");
        }
    }

    private final BroadcastReceiver mRecv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(
                    ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL)) {
                byte btCmd = intent.getByteExtra("cmd", (byte) 0x00);
                switch (btCmd) {
                    case CMD.REAL_TIME_INVENTORY:
                    case CMD.CUSTOMIZED_SESSION_TARGET_INVENTORY:
                        if (!mReaderHelper.getInventoryFlag()) {
                            if (!bTmpInventoryFlag) {
                                bTmpInventoryFlag = true;
                                mHandler.removeCallbacks(mRefreshRunnable);
                                mHandler.postDelayed(mRefreshRunnable, 2000);

                            }
                        }
                        mUpdateViewHandler.sendEmptyMessage(0);

                        mHandler.removeCallbacks(mRefreshRunnable);
                        mHandler.postDelayed(mRefreshRunnable, 2000);
                        mLoopHandler.removeCallbacks(mLoopRunnable);
                        mLoopHandler.postDelayed(mLoopRunnable, 2000);
                        break;
                    case ReaderHelper.INVENTORY_ERR:
                    case ReaderHelper.INVENTORY_ERR_END:
                    case ReaderHelper.INVENTORY_END:
                        if (mReaderHelper.getInventoryFlag() /* || bTmpInventoryFlag */) {
                            mLoopHandler.removeCallbacks(mLoopRunnable);
                            mLoopHandler.postDelayed(mLoopRunnable, 2000);

                        } else {
                            mLoopHandler.removeCallbacks(mLoopRunnable);
                            mHandler.removeCallbacks(mRefreshRunnable);
                        }

                        mUpdateViewHandler.sendEmptyMessage(0);
                        break;
                }

            } else if (intent.getAction().equals(
                    ReaderHelper.BROADCAST_WRITE_LOG)) {
            }
        }
    };

    private void refreshList() {
        //可以得出盘点到和盘盈的数据
        if (isDestroyed() || mTagAccessList == null) {
            return;
        }
        mTagAccessList.refreshList();
        if (mDatas != null) {
            if (mTagAccessList.data != null && mTagAccessList.data.size() > 0) {

                for (int j = 0; j < mTagAccessList.data.size(); j++) {
                    InventoryBuffer.InventoryTagMap inventoryTagMap = mTagAccessList.data.get(j);
                    if (inventoryTagMap != null) {
                        boolean temp = false;
                        for (int i = 0; i < mDatas.size(); i++) {
                            if (mDatas.get(i).epcData != null && mDatas.get(i).epcData.equals(inventoryTagMap.strEPC.replace(" ", ""))) {
                                mDatas.get(i).count = inventoryTagMap.nReadCount + "";
                                mDatas.get(i).rssi =
                                        (Integer.parseInt(inventoryTagMap.strRSSI) - 129) + "dBm";
                                temp = true;
                            }
                        }
                        if (!temp) {
                            mDatas.add(new AssertItemBean(inventoryTagMap.strEPC.replace(" ", ""), inventoryTagMap.nReadCount + "", (Integer.parseInt(inventoryTagMap.strRSSI) - 129) + "dBm", 0, 0, 0));
                        }
                    }
                }
            }
            if (mDatas.size() > 0) {
                Beeper.beep(BEEPER_SHORT);
            }
        }
        mGetPanAdapter.notifyDataSetChanged();

    }

    private void clearText() {
        mTagAccessList.clearText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            getData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (lbm != null)
            lbm.unregisterReceiver(mRecv);

        m_curInventoryBuffer.clearInventoryRealResult();
        mLoopHandler.removeCallbacks(mLoopRunnable);
        mHandler.removeCallbacks(mRefreshRunnable);


        ModuleManager.newInstance().setUHFStatus(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_qun;
    }
}
