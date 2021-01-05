package com.atguigu.bean;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * @author: spring du
 * @description:
 * @date: 2021/1/5 14:44
 */
public class SensorInEntity {

    // T1 Q1
    private String sensorType;

    private String sensorTime;

    private String value;

    public SensorInEntity(String data) {
        String sensorFlag = data.split(",")[0];
        if (sensorFlag.equals("T1")) { // T1
            String[] values = data.split(",");
            sensorType = sensorFlag;
            sensorTime = values[1];
            value = values[2].split(";")[0];
        } else { // Q1
            String s = data.split(";")[0];
            String[] values = s.split(",");
            sensorType = sensorFlag;
            sensorTime = values[1];
            value = values[2] + "," +values[3] + "," + values[4];
        }
    }

    public SensorInEntity(String sensorType, String sensorTime, String value) {
        this.sensorType = sensorType;
        this.sensorTime = sensorTime;
        this.value = value;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getSensorTime() {
        return sensorTime;
    }

    public void setSensorTime(String sensorTime) {
        this.sensorTime = sensorTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
//        return "SensorEntity{" +
//                "sensorType='" + sensorType + '\'' +
//                ", sensorTime='" + sensorTime + '\'' +
//                ", value='" + value + '\'' +
//                '}';
        return sensorType + "," + sensorTime + "," + value;
    }
}
