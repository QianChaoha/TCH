package com.uhf.uhf.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.com.tools.Beeper;
import com.example.administrator.baselib.base.BaseActivity;
import com.example.administrator.baselib.util.TopTitleUtils;
import com.nativec.tools.ModuleManager;
import com.reader.base.CMD;
import com.reader.base.ERROR;
import com.reader.base.ReaderBase;
import com.reader.base.StringTool;
import com.reader.code.helper.CodeReaderHelper;
import com.reader.code.helper.TDCodeList;
import com.reader.helper.ISO180006BOperateTagBuffer;
import com.reader.helper.InventoryBuffer;
import com.reader.helper.OperateTagBuffer;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;
import com.reader.helper.TDCodeTagBuffer;
import com.uhf.uhf.HexEditTextBox;
import com.uhf.uhf.LogList;
import com.uhf.uhf.R;
import com.uhf.uhf.TagRealList;


/**
 * Description:
 * Data: 2018/12/28
 *
 * @author: cqian
 */
public class OperateTagActivity extends BaseActivity {
    private static final int REQUEST_CODE_SCAN = 100;
    //fixed by lei.li 2016/11/09
    //private LogList mLogList;
    private LogList mLogList;
    //fixed by lei.li 2016/11/09

    private TextView mRead, mWrite;


    private HexEditTextBox mPasswordEditText;
    private EditText mStartAddrEditText;
    private EditText mDataLenEditText;
    private EditText mCodeText;
    private EditText mPasswordBottom;
    private EditText mCodeTextBroad;
    private HexEditTextBox mDataEditText;

    private RadioGroup mGroupAccessAreaType;

    private TagRealList mTagAccessList;

    private ReaderHelper mReaderHelper;
    private ReaderBase mReader;

    private int mPos = 0;

    private static ReaderSetting m_curReaderSetting;
    private static InventoryBuffer m_curInventoryBuffer;
    private static OperateTagBuffer m_curOperateTagBuffer;
    private static ISO180006BOperateTagBuffer m_curOperateTagISO18000Buffer;

    //二维码扫描
    private static TDCodeTagBuffer m_curOperateBinDCodeTagbuffer;
    private CodeReaderHelper mCodeReaderHelper;
    private TDCodeList mTagRealBCList = new TDCodeList();


    private LocalBroadcastManager lbm;

    private Context mContext;
    private boolean bTmpInventoryFlag = true;
    private String mStrRepeat = "1";
    private Handler mHandler = new Handler();
    private Handler mLoopHandler = new Handler();
    private Runnable mLoopRunnable = new Runnable() {
        public void run() {
            mReaderHelper.runLoopInventroy();
            mLoopHandler.postDelayed(this, 2000);
        }
    };
    boolean mNotBrocestEpc;
    private Handler mUpdateViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refreshText();
            refreshList();
            if (m_curInventoryBuffer.lsTagList != null && m_curInventoryBuffer.lsTagList.size() > 0 && m_curInventoryBuffer.lsTagList.get(0) != null) {

                startstop(false);
                selectTag(mPos);
                if (mNotBrocestEpc) {
                    operateData(mPasswordEditText.getText().toString().toUpperCase(), 1, "");
                } else {
                    operateData(mPasswordBottom.getText().toString().toUpperCase(), 1, "");
                }
            }
        }
    };

    private Runnable mRefreshRunnable = new Runnable() {
        public void run() {
            refreshList();
            mHandler.postDelayed(this, 2000);

        }
    };
    public static void startOperateTagActivity(Context context) {
        Intent intent = new Intent(context, OperateTagActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_operate_tag;
    }

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
        mContext = this;
        new TopTitleUtils(this).setTitle("标签读写").setLeft(null);;
        try {
            mReaderHelper = ReaderHelper.getDefaultHelper();
            mCodeReaderHelper = CodeReaderHelper.getDefaultHelper();
            mReader = mReaderHelper.getReader();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        m_curOperateBinDCodeTagbuffer = mCodeReaderHelper.getCurOperateTagBinDCodeBuffer();


        m_curReaderSetting = mReaderHelper.getCurReaderSetting();
        m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
        m_curOperateTagBuffer = mReaderHelper.getCurOperateTagBuffer();
        m_curOperateTagISO18000Buffer = mReaderHelper.getCurOperateTagISO18000Buffer();

        mPasswordBottom = findViewById(R.id.password_text_bottom);
        mCodeTextBroad = findViewById(R.id.codeTextBroad);
        mLogList = (LogList) findViewById(R.id.log_list);
        mRead = (TextView) findViewById(R.id.read);
        mWrite = (TextView) findViewById(R.id.write);
        mRead.setOnClickListener(setAccessOnClickListener);
        mWrite.setOnClickListener(setAccessOnClickListener);
        findViewById(R.id.codeRead).setOnClickListener(setAccessOnClickListener);
        findViewById(R.id.codeWrite).setOnClickListener(setAccessOnClickListener);
        findViewById(R.id.codeWriteBroad).setOnClickListener(setAccessOnClickListener);
        findViewById(R.id.readBroad).setOnClickListener(setAccessOnClickListener);
        findViewById(R.id.codeReadBroad).setOnClickListener(setAccessOnClickListener);

        mPasswordEditText = (HexEditTextBox) findViewById(R.id.password_text);
        mStartAddrEditText = (EditText) findViewById(R.id.start_addr_text);
        mDataLenEditText = (EditText) findViewById(R.id.data_length_text);
        mCodeText = (EditText) findViewById(R.id.codeText);
        mDataEditText = (HexEditTextBox) findViewById(R.id.data_write_text);

        mGroupAccessAreaType = (RadioGroup) findViewById(R.id.group_access_area_type);

        mTagAccessList = (TagRealList) findViewById(R.id.tag_access_list);

        lbm = LocalBroadcastManager.getInstance(mContext);

        IntentFilter itent = new IntentFilter();
        itent.addAction(ReaderHelper.BROADCAST_WRITE_LOG);
        itent.addAction(ReaderHelper.BROADCAST_REFRESH_OPERATE_TAG);
        itent.addAction(ReaderHelper.BROADCAST_REFRESH_INVENTORY_REAL);
        itent.addAction(CodeReaderHelper.BROADCAST_REFRESH_BAR_CODE);

        lbm.registerReceiver(mRecv, itent);

    }

    @Override
    protected void setViewData() {
    }

    @Override
    protected void getData() {

    }

    public void refresh() {
        mPos = 0;

        mDataLenEditText.setText("");
        mDataEditText.setText("");
        mPasswordEditText.setText("");

        mGroupAccessAreaType.check(R.id.set_access_area_epc);

        m_curOperateTagBuffer.clearBuffer();

        //add by lei.li 2017/1/16
        m_curInventoryBuffer.lsTagList.clear();
        //add by lei.li 2017/1/16
        refreshList();
        refreshText();
        clearText();
    }


    private void startstop(boolean start) {
        bTmpInventoryFlag = false;

        m_curInventoryBuffer.clearInventoryPar();

        m_curInventoryBuffer.lAntenna.add((byte) 0x00);

        if (m_curInventoryBuffer.lAntenna.size() <= 0) {
            Toast.makeText(mContext,
                    getResources().getString(R.string.antenna_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        m_curInventoryBuffer.bLoopInventoryReal = true;
        m_curInventoryBuffer.btRepeat = 0;

        if (mStrRepeat == null || mStrRepeat.length() <= 0) {
            Toast.makeText(mContext,
                    getResources().getString(R.string.repeat_empty),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        m_curInventoryBuffer.btRepeat = (byte) Integer.parseInt(mStrRepeat);

        if ((m_curInventoryBuffer.btRepeat & 0xFF) <= 0) {
            Toast.makeText(mContext,
                    getResources().getString(R.string.repeat_min),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        m_curInventoryBuffer.bLoopCustomizedSession = false;

        if (!start) {
            refreshText();
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

        // start_fixed by lei.li 2016/11/04 problem
        // m_curInventoryBuffer.clearInventoryRealResult();
        mReaderHelper.setInventoryFlag(true);
        // end_fixed by lei.li 2016/11/04

        mReaderHelper.clearInventoryTotal();
        refreshText();

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

    @SuppressWarnings("deprecation")
    private void refreshStartStop(boolean start) {
//        if (start) {
//            mStartStop.setBackgroundDrawable(getResources().getDrawable(
//                    R.drawable.button_disenabled_background));
//            mStartStop.setText(getResources()
//                    .getString(R.string.stop_inventory));
//        } else {
//            mStartStop.setBackgroundDrawable(getResources().getDrawable(
//                    R.drawable.button_background));
//            mStartStop.setText(getResources().getString(
//                    R.string.start_inventory));
//        }
    }

    private View.OnClickListener setAccessOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
                case R.id.read:
                    //operateData(mPasswordEditText.getText().toString().toUpperCase(), 1, "");
                    mNotBrocestEpc = true;
                    startstop(true);
                    setBtState(R.id.read, R.id.codeRead);
                    break;
                case R.id.codeRead:
                    setBtState(R.id.codeRead, R.id.read);
                    break;
                case R.id.write: {
                    operateData(mPasswordEditText.getText().toString().toUpperCase(), 2, mDataEditText.getText().toString().toUpperCase());
                    break;
                }
                case R.id.codeWrite:
                    operateData(mPasswordEditText.getText().toString().toUpperCase(), 2, mCodeText.getText().toString().toUpperCase());
                    break;
                case R.id.codeWriteBroad:

                    //循环写入
                    break;
                case R.id.readBroad:
                    mNotBrocestEpc = false;
                    startstop(true);
                    setBtState(R.id.readBroad, R.id.codeReadBroad);
                    break;
                case R.id.codeReadBroad:
                    setBtState(R.id.codeReadBroad, R.id.readBroad);
                    break;
            }
        }
    };

    private void setBtState(int enableId, int disableId) {
        findViewById(enableId).setBackgroundResource(R.drawable.button_press_background);
        findViewById(disableId).setBackgroundResource(R.drawable.button_disenabled_background);
    }

    private void selectTag(int pos) {
        if (pos <= 0) {
            mReader.cancelAccessEpcMatch(m_curReaderSetting.btReadId);
        } else {
            byte[] btAryEpc = null;

            try {
                String[] result = StringTool.stringToStringArray(m_curInventoryBuffer.lsTagList.get(0).strEPC.toUpperCase(), 2);
                btAryEpc = StringTool.stringArrayToByteArray(result, result.length);
            } catch (Exception e) {
                Toast.makeText(
                        mContext,
                        getResources().getString(R.string.param_unknown_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (btAryEpc == null) {
                Toast.makeText(
                        mContext,
                        getResources().getString(R.string.param_unknown_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            mReader.setAccessEpcMatch(m_curReaderSetting.btReadId, (byte) (btAryEpc.length & 0xFF), btAryEpc);

            mReader.getAccessEpcMatch(m_curReaderSetting.btReadId);


        }
    }

    /**
     * @param password  密码
     * @param type      1 读取  2写入
     * @param writeData 写入的数据
     */
    private void operateData(String password, int type, String writeData) {
        byte btMemBank = 0x00;
        byte btWordAdd = 0x00;
        byte btWordCnt = 0x00;
        byte[] btAryPassWord = null;
//                    if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_password) {
//                        btMemBank = 0x00;
//                    } else
        if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_epc) {
            btMemBank = 0x01;
        } else if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_tid) {
            btMemBank = 0x02;
        } else if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_user) {
            btMemBank = 0x03;
        }

        try {
            btWordAdd = (byte) Integer.parseInt(mStartAddrEditText.getText().toString());
        } catch (Exception e) {
            Toast.makeText(
                    mContext,
                    getResources().getString(R.string.param_start_addr_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String[] reslut = StringTool.stringToStringArray(password, 2);
            btAryPassWord = StringTool.stringArrayToByteArray(reslut, 4);
        } catch (Exception e) {
            Toast.makeText(
                    mContext,
                    getResources().getString(R.string.param_password_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (type == 1) {
            try {
                btWordCnt = (byte) (Integer.parseInt(mDataLenEditText.getText().toString()));
            } catch (Exception e) {
                Toast.makeText(
                        mContext,
                        getResources().getString(R.string.param_data_len_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if ((btWordCnt & 0xFF) <= 0) {
                Toast.makeText(
                        mContext,
                        getResources().getString(R.string.param_data_len_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            m_curOperateTagBuffer.clearBuffer();
            refreshList();
            // 1.标签id
            // 2. 0x00:RESERVED, 0x01:EPC, 0x02:TID, 0x03:USER
            // 3. 起始长度
            // 4.密码
            if (mReader != null) {
                mReader.readTag(m_curReaderSetting.btReadId, btMemBank, btWordAdd, btWordCnt, btAryPassWord);
            }

        } else {
            byte[] btAryData = null;
            String[] result = null;
            try {
                result = StringTool.stringToStringArray(writeData, 2);
                btAryData = StringTool.stringArrayToByteArray(result, result.length);
                btWordCnt = (byte) ((result.length / 2 + result.length % 2) & 0xFF);
            } catch (Exception e) {
                Toast.makeText(
                        mContext,
                        getResources().getString(R.string.param_data_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (btAryData == null || btAryData.length <= 0) {
                Toast.makeText(
                        mContext,
                        getResources().getString(R.string.param_data_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (btAryPassWord == null || btAryPassWord.length < 4) {
                Toast.makeText(
                        mContext,
                        getResources().getString(R.string.param_password_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            mDataLenEditText.setText(String.valueOf(btWordCnt & 0xFF));

            m_curOperateTagBuffer.clearBuffer();
            refreshList();
            mReader.writeTag(m_curReaderSetting.btReadId, btAryPassWord, btMemBank, btWordAdd, btWordCnt, btAryData);
        }
    }

    private void refreshList() {
        mTagAccessList.refreshList();
    }

    private void refreshText() {
        mTagAccessList.refreshText();
    }

    private void clearText() {
        mTagAccessList.clearText();
    }

    private final BroadcastReceiver mRecv = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ReaderHelper.BROADCAST_REFRESH_OPERATE_TAG)) {
                byte btCmd = intent.getByteExtra("cmd", (byte) 0x00);

                switch (btCmd) {
                    case CMD.GET_ACCESS_EPC_MATCH:
                        //mTagAccessListText.setText(m_curOperateTagBuffer.strAccessEpcMatch);
                        break;
                    case CMD.READ_TAG:
                        if (m_curOperateTagBuffer.lsTagList != null && m_curOperateTagBuffer.lsTagList.size() > 0) {
                            OperateTagBuffer.OperateTagMap operateTagMap = m_curOperateTagBuffer.lsTagList.get(0);
                            if (operateTagMap != null) {
                                //operateTagMap.strData   读取的数据
                                if (mNotBrocestEpc) {
                                    mDataEditText.setText(operateTagMap.strData);
                                } else {
                                    //广播EPC
                                    mCodeTextBroad.setText(operateTagMap.strData);
                                }
                            }
                        }
                    case CMD.WRITE_TAG:
                        break;
                }
            } else if (intent.getAction().equals(ReaderHelper.BROADCAST_WRITE_LOG)) {
                mLogList.writeLog((String) intent.getStringExtra("log"), intent.getIntExtra("type", ERROR.SUCCESS));
            } else if (intent.getAction().equals(
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
                        mUpdateViewHandler.sendEmptyMessageDelayed(0, 50);
                        mHandler.removeCallbacks(mRefreshRunnable);
                        mHandler.postDelayed(mRefreshRunnable, 2000);
                        mLoopHandler.removeCallbacks(mLoopRunnable);
                        mLoopHandler.postDelayed(mLoopRunnable, 2000);
                        refreshList();
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

                        mUpdateViewHandler.sendEmptyMessageDelayed(0, 50);
                        break;
                }

            } else if (intent.getAction().equals(
                    ReaderHelper.BROADCAST_WRITE_LOG)) {
                mLogList.writeLog((String) intent.getStringExtra("log"),
                        intent.getIntExtra("type", ERROR.SUCCESS));
            } else if (intent.getAction().equals(
                    CodeReaderHelper.BROADCAST_REFRESH_BAR_CODE)) {
                if (m_curOperateBinDCodeTagbuffer.getIsTagList() != null && m_curOperateBinDCodeTagbuffer.getIsTagList().size() > 0) {
                    mCodeText.setText(m_curOperateBinDCodeTagbuffer.getIsTagList().get(0).mBarCodeValue);
                }
            }
        }
    };


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mLogList.tryClose()) return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void doDestroy() {
        // TODO Auto-generated method stub
        if (lbm != null)
            lbm.unregisterReceiver(mRecv);
    }

    public boolean mInterceptTouchEvent = false;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View v = getCurrentFocus();
                if (isShouldHideInput(v, ev)) {

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return super.dispatchTouchEvent(ev);

        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return super.dispatchTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getRawX() > left) || !(event.getRawX() < right)
                    || !(event.getY() > top) || !(event.getY() < bottom);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lbm != null)
            lbm.unregisterReceiver(mRecv);

        m_curInventoryBuffer.clearInventoryRealResult();
        m_curOperateBinDCodeTagbuffer.clearBuffer();
        mLoopHandler.removeCallbacks(mLoopRunnable);
        mHandler.removeCallbacks(mRefreshRunnable);


        Beeper.release();

        ModuleManager.newInstance().setUHFStatus(false);
        ModuleManager.newInstance().setScanStatus(false);

    }
}
