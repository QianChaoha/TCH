package com.uhf.uhf.bean;

import java.util.List;

/**
 * Description:
 * Data: 2019/1/6
 *
 * @author: cqian
 */
public class UploadJsPanDataBean {

    public String name;
    public String note;
    public List<AssetDetailsBean> assetCheckDetails;

    public static class AssetDetailsBean {
        public AssetDetailsBean(String assetCode) {
            this.assetCode = assetCode;
        }

        public String assetCode;
    }
}
