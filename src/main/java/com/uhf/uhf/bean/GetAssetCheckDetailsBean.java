package com.uhf.uhf.bean;

import java.util.List;

/**
 * Description:
 * Data: 2019/1/11
 *
 * @author: cqian
 */
public class GetAssetCheckDetailsBean {


    /**
     * result : [{"assetId":28,"assetName":"笔记本03","assetCode":"A10003","imgPath":"Upload\\20181224214036_timg.jpg","note":"","supplier":null,"price":3000,"monopolized":false,"assetLocationId":3,"assetLocationName":"维修工位3","inDate":"2018-12-24T00:00:00","expireMonth":0},{"assetId":30,"assetName":"MES笔记本","assetCode":"A10005","imgPath":"Upload\\20181224214248_3.jpg","note":"","supplier":null,"price":5000,"monopolized":false,"assetLocationId":3,"assetLocationName":"维修工位3","inDate":"2018-12-24T00:00:00","expireMonth":10},{"assetId":36,"assetName":"测试笔记本","assetCode":"A10006","imgPath":null,"note":"","supplier":null,"price":1000,"monopolized":false,"assetLocationId":3,"assetLocationName":"维修工位3","inDate":"2018-12-24T00:00:00","expireMonth":0},{"assetId":43,"assetName":"成品升降机施耐德件","assetCode":"9000002","imgPath":null,"note":"备注3","supplier":"江苏三厂","price":200.44,"monopolized":true,"assetLocationId":3,"assetLocationName":"维修工位3","inDate":"2017-09-30T00:00:00","expireMonth":200},{"assetId":44,"assetName":"2017年IQC购买镀层测试设备","assetCode":"9000003","imgPath":null,"note":"备注4","supplier":"江苏四厂","price":200.44,"monopolized":true,"assetLocationId":3,"assetLocationName":"维修工位3","inDate":"2017-12-31T00:00:00","expireMonth":500}]
     * targetUrl : null
     * success : true
     * error : null
     * unAuthorizedRequest : false
     * __abp : true
     */

    public String targetUrl;
    public boolean success;
    public String error;
    public boolean unAuthorizedRequest;
    public boolean __abp;
    public List<ResultBean> result;
    public int id;

    public static class ResultBean {
        public String count;
        public String rssi;
        //扫描标签时,服务器有数据,没有对应标签--盘亏(默认)-3-白色
        //  匹配成功的背景色是绿色1；
        // 多出的标签的是红色,盘盈--2
        public int state;

        public ResultBean(String assetCode, String count, String rssi, int state, int assetId) {
            this.assetCode = assetCode;
            this.count = count;
            this.rssi = rssi;
            this.state = state;
            this.assetId = assetId;
        }

        public int assetId;
        public String assetName;
        public String assetCode;
        public String imgPath;
        public String note;
        public String supplier;
        public double price;
        public boolean monopolized;
        public int assetLocationId;
        public String assetLocationName;
        public String inDate;
        public int expireMonth;
    }
}
