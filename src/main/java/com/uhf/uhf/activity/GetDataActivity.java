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
import com.example.administrator.baselib.interfaces.BaseDialogInterface;
import com.example.administrator.baselib.util.JsonParserUtil;
import com.example.administrator.baselib.util.TopTitleUtils;
import com.example.administrator.baselib.view.dialog.CommenDialog;
import com.nativec.tools.ModuleManager;
import com.reader.base.CMD;
import com.reader.base.ReaderBase;
import com.reader.code.OpenTTFUtils;
import com.reader.helper.InventoryBuffer;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;
import com.tamic.novate.Throwable;
import com.uhf.uhf.R;
import com.uhf.uhf.TagRealList;
import com.uhf.uhf.adapter.GetPanAdapter;
import com.uhf.uhf.bean.AssertItemBean;
import com.uhf.uhf.bean.GetAssetCheckDetailsBean;
import com.uhf.uhf.bean.UploadJsPanDataBean;
import com.uhf.uhf.bean.UploadPanDataBean;
import com.uhf.uhf.bean.UploadResultBean;
import com.uhf.uhf.http.CallBack;
import com.uhf.uhf.http.HttpUrl;
import com.uhf.uhf.http.HttpUtils;
import com.uhf.uhf.util.InputPanDialog;
import com.uhf.uhf.util.LoaddingUtils;
import com.uhf.uhf.util.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.uhf.uhf.activity.HomeActivity.CLEAN_DATA_TIME;
import static com.uhf.uhf.util.SharedPreferencesUtils.GET_PAN_DATA;

/**
 * Description:
 * Data: 2019/1/2
 *
 * @author: cqian
 */
public class GetDataActivity extends BaseActivity {
    @Bind(R.id.recycleView)
    RecyclerView mRecyclerView;
    @Bind(R.id.btPd)
    TextView mStartStop;
    @Bind(R.id.tvLogDetail)
    TextView mTvLogDetail;
    List<AssertItemBean> mDatas = new ArrayList<AssertItemBean>();
    private GetPanAdapter mGetPanAdapter;
    LoaddingUtils mLoaddingUtils;
    private ReaderHelper mReaderHelper;
    private LocalBroadcastManager lbm;
    private boolean mStart;
    private GetAssetCheckDetailsBean mCheckDetailsBean;
    CommenDialog mCommenDialog;
    private UploadPanDataBean mUploadPanDataBean;
    private UploadJsPanDataBean mUploadJsPanDataBean;

    public static void startGetDataActivity(Context context) {
        Intent intent = new Intent(context, GetDataActivity.class);
        context.startActivity(intent);
    }

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
    boolean mHasData = false;//没有盘点计划数据

    @Override
    protected void onResume() {
        OpenTTFUtils.openUHF(mActivity);
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

        if (mReader != null) {
            if (!mReader.IsAlive())
                mReader.StartWait();
        }
        super.onResume();
    }

    InputPanDialog mInputPanDialog;

    @Override
    protected void initView() {
        new TopTitleUtils(this).setTitle("批量盘点").setLeft(null);
        mCommenDialog = new CommenDialog(mActivity);
        mCommenDialog.setDialogInterface(new BaseDialogInterface() {
            @Override
            public void ok(View view) {
                uploadData();
                mCommenDialog.dismiss();
            }

            @Override
            public void cancle(View view) {
                mCommenDialog.dismiss();
            }
        });
        mCommenDialog.setTitle("提示");


        mInputPanDialog = new InputPanDialog(mActivity);
        mInputPanDialog.setBottomOneButton();
        mInputPanDialog.setDialogInterface(new BaseDialogInterface() {
            @Override
            public void ok(View view) {
                if (mInputPanDialog.checkData()) {
                    mUploadJsPanDataBean = new UploadJsPanDataBean();
                    mUploadJsPanDataBean.name = mInputPanDialog.getInputName();
                    mUploadJsPanDataBean.note = mInputPanDialog.getMark();
                    mUploadJsPanDataBean.assetCheckDetails = new ArrayList<>();
                    for (int i = 0; i < mDatas.size(); i++) {
                        mUploadJsPanDataBean.assetCheckDetails.add(new UploadJsPanDataBean.AssetDetailsBean(mDatas.get(i).epcData));
                    }
                    uploadJsData();
                } else {
                    Toast.makeText(mActivity, "请输入盘点名称", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void cancle(View view) {
                mInputPanDialog.dismiss();
            }
        });
        mInputPanDialog.setTitle("提示");

        mGetPanAdapter = new GetPanAdapter(mActivity, mDatas);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mGetPanAdapter);
        mLoaddingUtils = new LoaddingUtils(mActivity);

    }

    private void uploadJsData() {
        mLoaddingUtils.show();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JsonParserUtil.serializeToJson(mUploadJsPanDataBean));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.doPost(HttpUrl.UploadRealTimeAssetCheck, jsonObject, new CallBack<UploadResultBean>() {
            @Override
            public void onSuccess(UploadResultBean data) {
                mLoaddingUtils.dismiss();
                if (mInputPanDialog != null) {
                    mInputPanDialog.dismiss();
                }
                if (data != null && data.result != null && data.result.isSuccessful) {
                    Toast.makeText(mActivity, "上传成功", Toast.LENGTH_SHORT).show();
                }
                cleanData();
            }

            @Override
            public void onFailed(Throwable e) {
                mLoaddingUtils.dismiss();
                if (mInputPanDialog != null) {
                    mInputPanDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void setViewData() {

    }

    @Override
    protected void getData() {
        mDatas.clear();
        String data = SharedPreferencesUtils.getString(mActivity, GET_PAN_DATA, "");
        mCheckDetailsBean = null;
        if (!TextUtils.isEmpty(data)) {
            try {
                mCheckDetailsBean = JsonParserUtil.deserializeByJson(data, GetAssetCheckDetailsBean.class);
                //============================CheckDetailsBean.id默认为0吗==============================
                if (mCheckDetailsBean != null && mCheckDetailsBean.id != 0) {
                    mHasData = true;
                }
            } catch (Exception e) {
            }
        }
        if (mCheckDetailsBean != null && mCheckDetailsBean.result != null && mCheckDetailsBean.result.size() > 0) {

            for (int i = 0; i < mCheckDetailsBean.result.size() && mCheckDetailsBean.result.get(i) != null; i++) {
                mDatas.add(new AssertItemBean(mCheckDetailsBean.result.get(i).assetCode, "", "", mCheckDetailsBean.result.get(i).state,
                        mCheckDetailsBean.id, mCheckDetailsBean.result.get(i).assetId));
            }
        }
        mGetPanAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.btPd, R.id.tvSave, R.id.tvUpload, R.id.btGetPlan, R.id.btClean})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.tvSave:

                GetAssetCheckDetailsBean detailsBean = new GetAssetCheckDetailsBean();
                detailsBean.result = new ArrayList<>();
                if (mCheckDetailsBean != null) {
                    detailsBean.id = mCheckDetailsBean.id;
                }
                for (int i = 0; i < mDatas.size() && mDatas.get(i) != null; i++) {
                    AssertItemBean assertItemBean = mDatas.get(i);
                    detailsBean.result.add(new GetAssetCheckDetailsBean.ResultBean(assertItemBean.epcData, assertItemBean.count, assertItemBean.rssi, assertItemBean.state, assertItemBean.assetId));
                }
                mCheckDetailsBean = detailsBean;
                String string = JsonParserUtil.serializeToJson(detailsBean);
                SharedPreferencesUtils.putString(mActivity, GET_PAN_DATA, string);
                Toast.makeText(mActivity, "保存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btClean:
                cleanData();

                break;
            case R.id.btGetPlan:
                startActivityForResult(new Intent(mActivity, GetPlanActivity.class), 1);
                break;
            case R.id.btPd:
                startstop();
                break;
            case R.id.tvUpload:
                // 0 待盘点,1 正常,2 盘盈,3 盘亏
                if (mStart) {
                    //正在盘点
                    Toast.makeText(mActivity, "尚未开始盘点或者盘点未结束", Toast.LENGTH_LONG).show();
                } else {
                    if (mCheckDetailsBean != null && mCheckDetailsBean.id != 0) {
                        mUploadPanDataBean = new UploadPanDataBean();
                        mUploadPanDataBean.id = mCheckDetailsBean.id;
                        mUploadPanDataBean.assetCheckDetails = new ArrayList<>();
                        int count = 0;
                        for (int i = 0; i < mDatas.size(); i++) {
                            mUploadPanDataBean.assetCheckDetails.add(new UploadPanDataBean.AssetDetailsBean(mDatas.get(i).epcData));
                            if (TextUtils.isEmpty(mDatas.get(i).count)) {
                                count = count + 1;
                            }
                        }
                        if (mUploadPanDataBean.assetCheckDetails.size() == 0) {
                            Toast.makeText(mActivity, "没有数据可以上传", Toast.LENGTH_SHORT).show();
                        } else {
                            if (count > 0) {
                                mCommenDialog.setContent("还有" + count + "条计划未盘点,确定上传吗?");
                            } else {
                                mCommenDialog.setContent("确定上传吗?");
                            }
                            mCommenDialog.show();
                        }

                    } else {
                        //即时盘点
                        if (mDatas.size() == 0) {
                            Toast.makeText(mActivity, "没有数据可以上传", Toast.LENGTH_SHORT).show();
                        } else {
                            mInputPanDialog.show();
                        }
                    }
                }
                break;
        }
    }

    private void cleanData() {
        if (mStart) {
            startstop();
            SharedPreferencesUtils.putString(mActivity, GET_PAN_DATA, "");
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getData();
                }
            }, CLEAN_DATA_TIME);
        }else {
            SharedPreferencesUtils.putString(mActivity, GET_PAN_DATA, "");
            getData();
        }
    }

    private void uploadData() {
        mLoaddingUtils.show();

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JsonParserUtil.serializeToJson(mUploadPanDataBean));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpUtils.doPost(HttpUrl.UPLOAD_ASSERT, jsonObject, new CallBack<UploadResultBean>() {
            @Override
            public void onSuccess(UploadResultBean data) {
                mLoaddingUtils.dismiss();
                if (data != null && data.result != null && data.result.isSuccessful) {
                    Toast.makeText(mActivity, "上传成功", Toast.LENGTH_SHORT).show();
                }
                cleanData();
            }

            @Override
            public void onFailed(Throwable e) {
                mLoaddingUtils.dismiss();
            }
        });
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
                    .equals("盘点")) {
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
            mStartStop.setText("停止盘点");
            mTvLogDetail.setText("盘点中...");
        } else {
            mStartStop.setText("盘点");
            mTvLogDetail.setText("盘点中结束");
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
                                if (mDatas.get(i).state != 2) {
                                    //多出的标签--盘盈。state不能置为1
                                    mDatas.get(i).state = 1;
                                }
                                temp = true;
                            }

                        }
                        if (!temp) {
                            //有服务器id  多出的标签--盘盈
                            if (mHasData) {
                                mDatas.add(new AssertItemBean(inventoryTagMap.strEPC.replace(" ", ""), inventoryTagMap.nReadCount + "", (Integer.parseInt(inventoryTagMap.strRSSI) - 129) + "dBm", 2, 0, 0));
                            } else {
                                mDatas.add(new AssertItemBean(inventoryTagMap.strEPC.replace(" ", ""), inventoryTagMap.nReadCount + "", (Integer.parseInt(inventoryTagMap.strRSSI) - 129) + "dBm", 0, 0, 0));
                            }

                        }
                    }

                }
            }
        }
//        else if (mTagAccessList.data != null && mTagAccessList.data.size() > 0){
//            for (int i = 0; i < mTagAccessList.data.size(); i++) {
//                InventoryBuffer.InventoryTagMap inventoryTagMap = mTagAccessList.data.get(i);
//                if (inventoryTagMap != null) {
//                    //多出的标签--盘盈
//                    mDatas.add(new AssertItemBean(inventoryTagMap.strEPC, inventoryTagMap.nReadCount + "", inventoryTagMap.strRSSI, 2, 0, 0));
//                }
//            }
//
//        }

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
    protected void onStop() {
        super.onStop();
        if (lbm != null)
            lbm.unregisterReceiver(mRecv);

        m_curInventoryBuffer.clearInventoryRealResult();
        mLoopHandler.removeCallbacks(mLoopRunnable);
        mHandler.removeCallbacks(mRefreshRunnable);


        Beeper.release();

        ModuleManager.newInstance().setUHFStatus(false);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_get_data;
    }
}
