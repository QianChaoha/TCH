package com.uhf.uhf.bean;

/**
 * Description:
 * Data: 2019/1/6
 *
 * @author: cqian
 */
public class UploadResultBean {

    /**
     * result : {"isSuccessful":true,"errMsg":""}
     * targetUrl : null
     * success : true
     * error : null
     * unAuthorizedRequest : false
     * __abp : true
     */

    public ResultBean result;
    public Object targetUrl;
    public boolean success;
    public Object error;
    public boolean unAuthorizedRequest;
    public boolean __abp;

    public static class ResultBean {
        /**
         * isSuccessful : true
         * errMsg :
         */

        public boolean isSuccessful;
        public String errMsg;
    }
}
