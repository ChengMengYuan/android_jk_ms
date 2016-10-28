package com.grgbanking.ct.database;

/**
 * @author ：     cmy
 * @version :     2016/10/19.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class NetInfo {
    private String bankId;
    private String bankName;
    private String netTaskStatus;

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getNetTaskStatus() {
        return netTaskStatus;
    }

    public void setNetTaskStatus(String netTaskStatus) {
        this.netTaskStatus = netTaskStatus;
    }

    public NetInfo(String bankId,
                   String bankName,
                   String netTaskStatus) {

        this.bankId = bankId;
        this.bankName = bankName;
        this.netTaskStatus = netTaskStatus;
    }

    public NetInfo() {

    }
}
