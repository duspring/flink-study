package com.atguigu.bean;

/**
 * @author springdu
 * @create 2021/1/5 21:44
 * @description TODO
 */
public enum MetricTypeEnum {

    AB("AB", "酸度"),
    AE("AE", "粘稠度"),
    CE("CE", "含水量"),
    FIRST("FIRST","第一次"),
    SECOND("SECOND", "第二次");

    private String code;
    private String desc;

    MetricTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
