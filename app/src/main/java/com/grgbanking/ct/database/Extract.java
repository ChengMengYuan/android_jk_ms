package com.grgbanking.ct.database;

import com.grgbanking.ct.entity.PdaNetPersonInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：     cmy
 * @version :     2016/11/4.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class Extract implements Serializable {
    private String bankId;
    private String bankName;
    private String netTaskStatus;
    private String lineSn;
    private String lineId;
    private List<PdaNetPersonInfo> netPersonInfoList;

    public Extract() {

    }

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

    public String getLineSn() {
        return lineSn;
    }

    public void setLineSn(String lineSn) {
        this.lineSn = lineSn;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public List<PdaNetPersonInfo> getNetPersonInfoList() {
        return netPersonInfoList;
    }

    public void setNetPersonInfoList(List<PdaNetPersonInfo> netPersonInfoList) {
        this.netPersonInfoList = netPersonInfoList;
    }
}
