package com.uhf.uhf.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TableRow;
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
import com.reader.helper.ISO180006BOperateTagBuffer;
import com.reader.helper.InventoryBuffer;
import com.reader.helper.OperateTagBuffer;
import com.reader.helper.ReaderHelper;
import com.reader.helper.ReaderSetting;
import com.uhf.uhf.HexEditTextBox;
import com.uhf.uhf.LogList;
import com.uhf.uhf.R;
import com.uhf.uhf.TagAccessList;
import com.uhf.uhf.TagRealList;
import com.uhf.uhf.spiner.AbstractSpinerAdapter;
import com.uhf.uhf.spiner.SpinerPopWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.com.tools.Beeper.BEEPER_SHORT;

/**
 * Description:
 * Data: 2018/12/28
 *
 * @author: cqian
 */
public class SettingActivity extends BaseActivity {
    //fixed by lei.li 2016/11/09
    //private LogList mLogList;
    private TextView mLogList;
    //fixed by lei.li 2016/11/09

    private TextView mGet, mWrite, mKill;

    //private TextView mRefreshButton;

    private TextView mTagAccessListText;
    private TableRow mDropDownRow;

    private List<String> mAccessList;
    private SpinerPopWindow mSpinerPopWindow;

    private HexEditTextBox mKillPasswordEditText;

    private TagRealList mTagAccessList;

    private ReaderHelper mReaderHelper;
    private ReaderBase mReader;
    EditText mDataLenEditText;
    private int mPos = -1;

    private static ReaderSetting m_curReaderSetting;
    private static InventoryBuffer m_curInventoryBuffer;
    private static OperateTagBuffer m_curOperateTagBuffer;
    private static ISO180006BOperateTagBuffer m_curOperateTagISO18000Buffer;

    private LocalBroadcastManager lbm;
    private boolean bTmpInventoryFlag = true;
    private String mStrRepeat = "1";
    private Handler mHandler = new Handler();
    private Handler mLoopHandler = new Handler();
    private Runnable mLoopRunnable = new Runnable() {
        public void run() {
            mReaderHelper.runLoopInventroy();
            mLoopHandler.postDelayed(this, 100);
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
            }
        }
    };

    private Runnable mRefreshRunnable = new Runnable() {
        public void run() {
            refreshList();
            mHandler.postDelayed(this, 100);

        }
    };
    boolean start = true;
    private Context mContext;

    public static void startSettingActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView() {
        new TopTitleUtils(this).setTitle("高级设置").setLeft(null);
        mContext = this;

        try {
            mReaderHelper = ReaderHelper.getDefaultHelper();
            mReader = mReaderHelper.getReader();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mAccessList = new ArrayList<String>();

        m_curReaderSetting = mReaderHelper.getCurReaderSetting();
        m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
        m_curOperateTagBuffer = mReaderHelper.getCurOperateTagBuffer();
        m_curOperateTagISO18000Buffer = mReaderHelper.getCurOperateTagISO18000Buffer();


        mLogList = findViewById(R.id.log_list);
        mGet = (TextView) findViewById(R.id.get);
        mWrite = (TextView) findViewById(R.id.write);
        mKill = (TextView) findViewById(R.id.kill);
        mDataLenEditText = findViewById(R.id.data_length_text);
        mGet.setOnClickListener(setAccessOnClickListener);
        mWrite.setOnClickListener(setAccessOnClickListener);
        mKill.setOnClickListener(setAccessOnClickListener);

        mKillPasswordEditText = (HexEditTextBox) findViewById(R.id.kill_password_text);


        mTagAccessListText = (TextView) findViewById(R.id.tag_access_list_text);
        mDropDownRow = (TableRow) findViewById(R.id.table_row_tag_access_list);

        mTagAccessList = findViewById(R.id.tag_access_list);

        lbm = LocalBroadcastManager.getInstance(mContext);

        IntentFilter itent = new IntentFilter();
        itent.addAction(ReaderHelper.BROADCAST_WRITE_LOG);
        itent.addAction(ReaderHelper.BROADCAST_REFRESH_OPERATE_TAG);
        lbm.registerReceiver(mRecv, itent);

        mDropDownRow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSpinWindow();
            }
        });

        mAccessList.clear();
        for (int i = 0; i < m_curInventoryBuffer.lsTagList.size(); i++) {
            mAccessList.add(m_curInventoryBuffer.lsTagList.get(i).strEPC);
        }

        mSpinerPopWindow = new SpinerPopWindow(mContext);
        mSpinerPopWindow.refreshData(mAccessList, 0);
        mSpinerPopWindow.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
            public void onItemClick(int pos) {
                setAccessSelectText(pos);
            }
        });

        updateView();
    }

    @Override
    protected void setViewData() {

    }

    @Override
    protected void getData() {

    }

    public void refresh() {
        mTagAccessListText.setText("");
        mPos = -1;

        mKillPasswordEditText.setText("");


        m_curOperateTagBuffer.clearBuffer();

        //add by lei.li 2017/1/16
        mAccessList.clear();
        m_curInventoryBuffer.lsTagList.clear();
        mAccessList.add("cancel");
        //add by lei.li 2017/1/16
        refreshList();
        refreshText();
        clearText();
    }

    private void setAccessSelectText(int pos) {
        if (pos >= 0 && pos < mAccessList.size()) {
            String value = mAccessList.get(pos);
            mTagAccessListText.setText(value);
            mPos = pos;
            selectTag(mPos);

        }
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

    private void showSpinWindow() {
        //show tags readed by uhf
        for (int i = 0; i < m_curInventoryBuffer.lsTagList.size(); i++) {
            if (!mAccessList.contains(m_curInventoryBuffer.lsTagList.get(i).strEPC))
                mAccessList.add(m_curInventoryBuffer.lsTagList.get(i).strEPC);
        }
        mSpinerPopWindow.refreshData(mAccessList, 0);
        mSpinerPopWindow.setWidth(mDropDownRow.getWidth());
        mSpinerPopWindow.showAsDropDown(mDropDownRow);
    }

    private void updateView() {
        if (mPos < 0) mPos = 0;
        setAccessSelectText(mPos);
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

        mReaderHelper.setInventoryFlag(true);

        mReaderHelper.clearInventoryTotal();
        refreshText();

        byte btWorkAntenna = m_curInventoryBuffer.lAntenna
                .get(m_curInventoryBuffer.nIndexAntenna);
        if (btWorkAntenna < 0)
            btWorkAntenna = 0;
        mReaderHelper.runLoopInventroy();
        m_curReaderSetting.btWorkAntenna = btWorkAntenna;
        refreshStartStop(true);
        mLoopHandler.removeCallbacks(mLoopRunnable);
        mLoopHandler.postDelayed(mLoopRunnable, 10);
        mHandler.removeCallbacks(mRefreshRunnable);
        mHandler.postDelayed(mRefreshRunnable, 10);
    }

    private void refreshStartStop(boolean start) {
        if (start) {
            mGet.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.button_disenabled_background));
            mGet.setText(getResources()
                    .getString(R.string.stop_inventory));
        } else {
            mGet.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.button_background));
            mGet.setText(getResources().getString(
                    R.string.start_inventory));
        }
        this.start = !start;

    }

    /**
     * @param startLength 起始长度
     * @param dataLength  数据长度
     * @param password    密码
     * @param type        1 读取  2写入
     * @param writeData   写入的数据
     */
    private void operateData(String startLength, String dataLength, String password, int type, String writeData) {
        byte btMemBank = 0x00;
        byte btWordAdd = 0x00;
        byte btWordCnt = 0x00;
        byte[] btAryPassWord = null;


        btMemBank = 0x00;
//                    if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_password) {
//                        btMemBank = 0x00;
//                    } else
//        if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_epc) {
//            btMemBank = 0x01;
//        } else if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_tid) {
//            btMemBank = 0x02;
//        } else if (mGroupAccessAreaType.getCheckedRadioButtonId() == R.id.set_access_area_user) {
//            btMemBank = 0x03;
//        }

        try {
            btWordAdd = (byte) Integer.parseInt(startLength);
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
                btWordCnt = (byte) (Integer.parseInt(dataLength));
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

    private View.OnClickListener setAccessOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
                case R.id.get:
                    startstop(start);
                    break;
                case R.id.write:
                    //operateData();
                    break;
                case R.id.kill: {
                    byte[] btAryPassWord = null;
                    try {
                        String[] reslut = StringTool.stringToStringArray(mKillPasswordEditText.getText().toString().toUpperCase(), 2);
                        btAryPassWord = StringTool.stringArrayToByteArray(reslut, 4);
                    } catch (Exception e) {
                        Toast.makeText(
                                mContext,
                                getResources().getString(R.string.param_killpassword_error),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (btAryPassWord == null || btAryPassWord.length < 4) {
                        Toast.makeText(
                                mContext,
                                getResources().getString(R.string.param_killpassword_error),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    m_curOperateTagBuffer.clearBuffer();
                    refreshList();
                    mReader.killTag(m_curReaderSetting.btReadId, btAryPassWord);
                    break;
                }
            }
        }
    };

    private void refreshList() {
        if (isDestroyed() || mTagAccessList == null) {
            return;
        }
        mTagAccessList.refreshList();
        if (mTagAccessList.data!=null && mTagAccessList.data.size()>0){
            Beeper.beep(BEEPER_SHORT);
        }
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
                        mTagAccessListText.setText(m_curOperateTagBuffer.strAccessEpcMatch);
                        break;
                    case CMD.READ_TAG:
                        if (m_curOperateTagBuffer.lsTagList != null && m_curOperateTagBuffer.lsTagList.size() > 0) {
                            OperateTagBuffer.OperateTagMap operateTagMap = m_curOperateTagBuffer.lsTagList.get(0);
                            if (operateTagMap != null) {
                                //operateTagMap.strData   读取的数据
                            }
                        }
                    case CMD.WRITE_TAG:
                    case CMD.LOCK_TAG:
                    case CMD.KILL_TAG:
                        refreshList();
                        refreshText();
                        break;
                }
            } else if (intent.getAction().equals(ReaderHelper.BROADCAST_WRITE_LOG)) {
                setLog(intent);
            }
        }
    };

    private void setLog(Intent intent) {
        String log = (String) intent.getStringExtra("log");
        int type = intent.getIntExtra("type", ERROR.SUCCESS);
        Date now = new Date();
        SimpleDateFormat temp = new SimpleDateFormat("kk:mm:ss");
        mLogList.setText(temp.format(now) + ": " + log);
        mLogList.setTextColor(type == ERROR.SUCCESS ? Color.BLACK : Color.RED);
        if (type == ERROR.SUCCESS && "写标签".equals(log)) {
            Toast.makeText(mContext, "写入成功", Toast.LENGTH_SHORT).show();
        }
    }

    public void doDestroy() {
        // TODO Auto-generated method stub
        if (lbm != null)
            lbm.unregisterReceiver(mRecv);
    }

    public boolean mInterceptTouchEvent = false;


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    View v = activity.getCurrentFocus();
                    if (isShouldHideInput(v, ev)) {

                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                    return super.dispatchTouchEvent(ev);

            }
            // 必不可少，否则所有的组件都不会有TouchEvent了
            return super.dispatchTouchEvent(ev);
        }
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
        mLoopHandler.removeCallbacks(mLoopRunnable);
        mHandler.removeCallbacks(mRefreshRunnable);


        ModuleManager.newInstance().setUHFStatus(false);
        ModuleManager.newInstance().setScanStatus(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }
}
