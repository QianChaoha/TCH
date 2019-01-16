package com.uhf.uhf.util;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Description:
 * Data: 2019/1/14
 *
 * @author: cqian
 */
public class FourUtils {
    public static final String[] pcArr = new String[]{"0800", "1400", "1800", "2400", "2800", "3400", "3800", "4400", "4800", "5400", "5800", "6400", "6800"
            , "7400", "7800", "8400", "8800", "9400", "9800", "9E00", "A200", "A800", "AC00", "B200", "B600","BC00","C000","C600"};

    public static boolean jgdgeFour(EditText editText) {
        String text = editText.getText().toString().replace(" ", "");
        int length = text.length();
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(editText.getContext(), "请输入数据", Toast.LENGTH_SHORT).show();
            return false;
        } else if (length % 4 != 0) {
            Toast.makeText(editText.getContext(), "输入的数据长度必须是4的倍数", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static String getPc(EditText editText) {
        String text = editText.getText().toString().replace(" ", "");
        int length = text.length() / 4 - 1;
        if (length > pcArr.length || length < 0) {
            return "-1";
        } else {
            return pcArr[length];
        }
    }
}
