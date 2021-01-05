package com.atguigu.bean;

/**
 * @author: spring du
 * @description: 温度传感器Q实体
 * @date: 2021/1/5 14:24
 */
public class SensorQ extends BaseEntity {

    // 传感器Q的温度
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
