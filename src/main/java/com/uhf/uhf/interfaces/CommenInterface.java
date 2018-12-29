package com.uhf.uhf.interfaces;

/**
 * Created by cqian on 2018/5/25.
 * 通用的回调接口,只有成功和失败两种情况,此接口多处使用,谨慎修改
 */

public interface CommenInterface {
    void onSuccess();
    void onFailed();
}
