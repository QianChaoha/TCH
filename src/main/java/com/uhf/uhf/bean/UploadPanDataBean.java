package com.uhf.uhf.bean;

import java.util.List;

/**
 * Description:
 * Data: 2019/1/6
 *
 * @author: cqian
 */
public class UploadPanDataBean {

    public int id;
    public List<AssetDetailsBean> assetCheckDetail;

    public static class AssetDetailsBean {
        public AssetDetailsBean(String assetCode) {
            this.assetCode = assetCode;
        }

        public String assetCode;
    }
}
