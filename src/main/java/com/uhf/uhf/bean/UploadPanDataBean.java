package com.uhf.uhf.bean;

import java.util.List;

/**
 * Description:
 * Data: 2019/1/6
 *
 * @author: cqian
 */
public class UploadPanDataBean {

    /**
     * id : 0
     * name : “”
     * AssetCheckDetails : [{"id":0,"assetId":0,"assetCode":"","state":0}]
     */

    public int id;
    public String name;
    public List<AssetDetailsBean> AssetCheckDetails;

    public static class AssetDetailsBean {
        public AssetDetailsBean(int id, int assetId, String assetCode, int state) {
            this.id = id;
            this.assetId = assetId;
            this.assetCode = assetCode;
            this.state = state;
        }

        /**
         * id : 0
         * assetId : 0
         * assetCode :
         * state : 0
         */

        public int id;
        public int assetId;
        public String assetCode;
        public int state;
    }
}
