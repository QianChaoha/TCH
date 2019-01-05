package com.uhf.uhf.http;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.tamic.novate.Throwable;
import com.uhf.uhf.UHFApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Description:
 * Data: 2018/6/30
 *
 * @author: cqian
 */

public class HttpUtils {
    private static Context mContext;
    public static Map<String, String> headers = new HashMap<>();
    private static String mCurrentUrl;

    public static void init(Context context) {
        mContext = context;
        AndroidNetworking.initialize(mContext);
    }

    public static void clearToken() {
        HttpUtils.headers.remove("Authorization");
    }

    public static void doPost(String url, JSONObject jsonObject, final CallBack callBack) {
        mCurrentUrl = url;
        LogUtils.debug("═══════════════════════════════════════url═════════════════════════════════════════════");
        LogUtils.debug(HttpUrl.SERVER_URL + url);
        LogUtils.debug("═══════════════════════════════════════headers═════════════════════════════════════════════");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            entry.getKey();
            entry.getValue();
            LogUtils.debug(entry.getKey() + ":" + entry.getValue());
        }
        if (jsonObject != null) {
            LogUtils.debug("═══════════════════════════════════════请求参数═════════════════════════════════════════════");
            LogUtils.debug(jsonObject.toString());
        }


        AndroidNetworking.post(HttpUrl.SERVER_URL + url)
                .addHeaders(headers)
                .addJSONObjectBody(jsonObject)
                .setTag(url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        callBack.parseData(response);
                    }

                    @Override
                    public void onError(ANError e) {
                        if (e != null && e.getErrorDetail()!= null) {
                            LogUtils.debug("══════════════════════════════════════Novate.Throwable═══════════════════════════════════════════");
                            LogUtils.debug(e.getErrorDetail());
                            Throwable throwable = new Throwable(e.getCause(), e.getErrorCode() + "", e.getErrorDetail());
                            callBack.onFailed(throwable);
                        }
                    }
                });

    }
    public static void doGet(String url, Map<String, String> map, final CallBack callBack) {
        mCurrentUrl = url;
        LogUtils.debug("═══════════════════════════════════════url═════════════════════════════════════════════");
        LogUtils.debug(HttpUrl.SERVER_URL + url);
        LogUtils.debug("═══════════════════════════════════════headers═════════════════════════════════════════════");
        if (UHFApplication.mUserBean != null) {
            headers.put("Authorization", "Bearer " + UHFApplication.mUserBean.accessToken);
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            entry.getKey();
            entry.getValue();
            LogUtils.debug(entry.getKey() + ":" + entry.getValue());
        }

        if (map!=null){
            LogUtils.debug("═══════════════════════════════════════请求参数═════════════════════════════════════════════");
            for (Map.Entry<String, String> entry : map.entrySet()) {
                entry.getKey();
                entry.getValue();
                LogUtils.debug(entry.getKey() + ":" + entry.getValue());
            }
        }

        AndroidNetworking.get(HttpUrl.SERVER_URL + url)
                .addHeaders(headers)
                .addPathParameter(map)
                .setTag(url)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        callBack.parseData(response);
                    }

                    @Override
                    public void onError(ANError e) {
                        if (e != null && e.getErrorDetail()!= null) {
                            LogUtils.debug("══════════════════════════════════════Novate.Throwable═══════════════════════════════════════════");
                            LogUtils.debug(e.getErrorDetail());
                            Throwable throwable = new Throwable(e.getCause(), e.getErrorCode() + "", e.getErrorDetail());
                            callBack.onFailed(throwable);
                        }
                    }
                });

    }

    public static void cancle() {
        AndroidNetworking.cancel(mCurrentUrl);
    }

    public static void doPost(String url, final CallBack callBack) {
        doPost(url, new JSONObject(), callBack);
    }
}