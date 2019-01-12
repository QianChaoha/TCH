package com.uhf.uhf.util;

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
        try {
            SimpleDateFormat formatTZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            formatTZ.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date TZDate = formatTZ.parse(datetime);
            return format.format(TZDate);
        } catch (Exception e) {
            e.printStackTrace();
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
