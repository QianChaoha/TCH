package com.reader.code.helper;

/**
 * Created by Administrator on 2016-10-13.
 * This class is used to show the information return from 2D scan engine;
 */

import android.content.Context;
import android.widget.ListView;

import com.reader.helper.ReaderHelper;
import com.reader.helper.TDCodeTagBuffer;

import java.util.ArrayList;
import java.util.List;


public class TDCodeList {

    private static final String TAG = "TagRealBCList";
    private static final boolean DEBUG = true;

    private Context mContext;

    private CodeReaderHelper mReaderHelper;

    public List<TDCodeTagBuffer.BinDCodeTagMap> mData_BD;
    //private TDCodeListAdapter mRealBDListAdapter;
    private ListView mTagRealBCList;

    private static TDCodeTagBuffer m_curOperateBinDTagBuffer;

    public TDCodeList() {
        try {
            mReaderHelper = CodeReaderHelper.getDefaultHelper();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mData_BD = new ArrayList<TDCodeTagBuffer.BinDCodeTagMap>();
        m_curOperateBinDTagBuffer = mReaderHelper.getCurOperateTagBinDCodeBuffer();

    }

    public final void refreshList() {
        mData_BD.clear();
        mData_BD.addAll(m_curOperateBinDTagBuffer.getIsTagList());
    }
}
