package com.uhf.uhf.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

import com.example.administrator.baselib.base.BaseDialog;
import com.example.administrator.baselib.view.dialog.CommenDialog;
import com.uhf.uhf.R;

/**
 * Description:
 * Data: 2019/1/12
 *
 * @author: cqian
 */
public class InputPanDialog extends BaseDialog {
    public InputPanDialog(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_input_pan;
    }

    @Override
    public void initView() {
        setBottomOneButton();
    }

    public String getInputName() {
        return ((EditText) mContentWrapper.findViewById(R.id.etInputName)).getText().toString();
    }
    public String getMark() {
        return ((EditText) mContentWrapper.findViewById(R.id.etInputMark)).getText().toString();
    }

    public boolean checkData() {
        return !TextUtils.isEmpty(getInputName());
    }
}
