package com.uhf.uhf.bean;

/**
 * Description:
 * Data: 2019/1/5
 *
 * @author: cqian
 */
public class AssertItemBean {

    public String epcData;
    public String count;
    public String rssi;
    //扫描标签时,服务器有数据,没有对应标签--盘亏(默认)-3-白色
    //  匹配成功的背景色是绿色1；
    // 多出的标签的是红色,盘盈--2
    public int state;

    public int id;
    public int assetId;

    public AssertItemBean(String epcData, String count, String rssi, int state, int id, int assetId) {
        this.epcData = epcData;
        this.count = count;
        this.rssi = rssi;
        this.state = state;
        this.id = id;
        this.assetId = assetId;
    }
}
