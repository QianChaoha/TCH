package com.uhf.uhf.http;

import com.example.administrator.baselib.util.JsonParserUtil;
import com.tamic.novate.Throwable;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import okhttp3.ResponseBody;

/**
 * Description:
 * Data: 2018/6/30
 *
 * @author: cqian
 */

public abstract class CallBack<T> {
    public abstract void onSuccess(T data);

    public abstract void onFailed(Throwable e);

    public String mResponseData;

    public void parseData(String responseData) {
        mResponseData = responseData;
        LogUtils.debug("═══════════════════════════════════════返回值═════════════════════════════════════════════");
        LogUtils.debug(responseData);
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        T t = JsonParserUtil.deserializeByJson(responseData, entityClass);
        onSuccess(t);
    }

}
