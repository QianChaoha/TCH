package com.uhf.uhf.bean;

import java.util.List;

/**
 * Description:
 * Data: 2019/1/9
 *
 * @author: cqian
 */
public class FilterUnfinishedAssetChecksBean {

    /**
     * __abp : true
     * result : [{"assetCheckDetails":[],"checkEntityId":0,"checkType":1,"creationTime":"2019-01-10T14:12:43.2316678","deadDate":"2019-01-10T00:00:00","id":43,"mapLocation":"images/scene.png","name":"121212","note":"","state":0}]
     * success : true
     * unAuthorizedRequest : false
     */

    public boolean __abp;
    public boolean success;
    public boolean unAuthorizedRequest;
    public List<ResultBean> result;

    public class ResultBean {
        /**
         * assetCheckDetails : []
         * checkEntityId : 0
         * checkType : 1
         * creationTime : 2019-01-10T14:12:43.2316678
         * deadDate : 2019-01-10T00:00:00
         * id : 43
         * mapLocation : images/scene.png
         * name : 121212
         * note :
         * state : 0
         */

        public int checkEntityId;
        public int checkType;
        public String creationTime;
        public String deadDate;
        public int id;
        public String mapLocation;
        public String name;
        public String note;
        public int state;
        public List<?> assetCheckDetails;
    }
}
