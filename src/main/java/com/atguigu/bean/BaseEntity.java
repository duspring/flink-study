package com.atguigu.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: spring du
 * @description: 传感器基础实体
 * @date: 2021/1/5 14:22
 */
public class BaseEntity implements Serializable {

    // 传感器名称
    private String sensorName;
    // 传感器时间
    private Date sensorTime;

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public Date getSensorTime() {
        return sensorTime;
    }

    public void setSensorTime(Date sensorTime) {
        this.sensorTime = sensorTime;
    }
}
