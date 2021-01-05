package com.atguigu.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: spring du
 * @description: 温度传感器T实体
 * @date: 2021/1/5 14:13
 */
public class SensorT extends BaseEntity{

    // 传感器T的温度
    private Double sensorTemperature;

    public Double getSensorTemperature() {
        return sensorTemperature;
    }

    public void setSensorTemperature(Double sensorTemperature) {
        this.sensorTemperature = sensorTemperature;
    }
}
