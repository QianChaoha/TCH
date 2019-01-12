package com.uhf.uhf.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Description:
 * Data: 2018/6/14
 *
 * @author: cqian
 */

public class DateUtil {

    public static String formateStr(String str) {
        return String.format("%02d", str);
    }

    /**
     * 使用RFC3339格式处理时间
     *
     * @param datetime
     * @return
     */
    public static String DateTZ2Normal(String datetime) {
        if (!TextUtils.isEmpty(datetime)) {
            int index = datetime.indexOf("T");
            if (index != -1) {
                return datetime.substring(0, index);
            }
        }
        return "";
    }

    public static long dateToMillionSecond(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long millionSeconds = 0;//毫秒
        try {
            millionSeconds = sdf.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millionSeconds;
    }

    // 毫秒转日期
    public static String millionSecondToDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        Date date = c.getTime();
        return sdf.format(date);
    }

    // 毫秒转日期
    public static String millionSecondToDateNoYear(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        Date date = c.getTime();
        return sdf.format(date);
    }

}
