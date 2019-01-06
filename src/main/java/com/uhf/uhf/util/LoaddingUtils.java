package com.uhf.uhf.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Description:
 * Data: 2019/1/5
 *
 * @author: cqian
 */
public class LoaddingUtils {
    private ProgressDialog mProgressDialog;

    public LoaddingUtils(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("获取数据中...");
    }

    public void show() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {

            mProgressDialog.show();
        }
    }

    public void dismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {

            mProgressDialog.dismiss();
        }
    }
}
