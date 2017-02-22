package com.test;

import java.io.Serializable;

/**
 * @author ：     cmy
 * @version :     2017/2/21.
 * @e-mil ：      mengyuan.cheng.mier@gmail.com
 * @Description :
 */

public class ObjectTest implements Serializable {
    private String code;
    private String text;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
