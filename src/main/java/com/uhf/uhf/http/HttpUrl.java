package com.uhf.uhf.http;

/**
 * Description:
 * Data: 2018/7/2
 *
 * @author: cqian
 */

public class HttpUrl {
    public static final String SERVER_URL = "http://ams.digihorns.com/";

    public static final String TOKEN_AUTH = "api/TokenAuth/Authenticate";

    //获取所有下发的的盘点
    public static final String GET_ASSERT = "api/services/app/assetCheck/GetUnfinishedAssetChecks";
    //获取所有下发的的盘点计划列表
    public static final String FilterUnfinishedAssetChecks = "api/services/app/AssetCheck/FilterUnfinishedAssetChecks";
    //上传盘点
    public static final String UPLOAD_ASSERT = "api/services/app/AssetCheck/UploadAssetChecksResult";
    //即时上传
    public static final String UploadRealTimeAssetCheck = "api/services/app/AssetCheck/UploadRealTimeAssetCheck";
    //根据盘点计划Id获取盘点计划清单
    public static final String GetAssetCheckDetails = "api/services/app/AssetCheck/GetAssetCheckDetails";
}
