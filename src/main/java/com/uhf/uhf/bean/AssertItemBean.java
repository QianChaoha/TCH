package com.uhf.uhf.bean;

/**
 * Description:
 * Data: 2019/1/5
 *
 * @author: cqian
 */
public class AssertItemBean {
    public AssertItemBean(String epcData, String count, String rssi) {
        this.epcData = epcData;
        this.count = count;
        this.rssi = rssi;
    }

    public String epcData;
    public String count;
    public String rssi;
}
