package com.atguigu.chapter1103;

import com.atguigu.bean.WaterSensor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: spring du
 * @description:
 * @date: 2020/11/3 15:00
 */
public class Flink10_Transform_KeyBy {
    public static void main(String[] args) throws Exception {
        // 0. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 1. 从文件读取数据
        DataStreamSource<String> inputDS = env.readTextFile("input/sensor-data.log");

        // 2. Transform: Map转换成实体对象
        SingleOutputStreamOperator<WaterSensor> sensorDS = inputDS.map(new Flink06_Transform_Map.MyMapFunction());

        // 3. TODO Keyby:分组
        // 通过 位置索引 或 字段名称 ，返回Key的类型，无法确定，所以返回 Tuple，后续使用key的时候，很麻烦
        // 通过 明确的指定 key 的方式，获取到的key就是具体的类型 => 实现KeySelector 或 lambda
//        sensorDS.keyBy("id").print();
//        KeyedStream<WaterSensor, Tuple> sensorKSByFieldName = sensorDS.keyBy("id");
        KeyedStream<WaterSensor, String> sensorKSByKeySelector = sensorDS.keyBy(new MyKeySelector());
//        KeyedStream<WaterSensor, String> waterSensorStringKeyedStream = sensorDS.keyBy(r -> r.getId());


        sensorKSByKeySelector.print();

        env.execute();
    }

    public static class MyKeySelector implements KeySelector<WaterSensor, String> {

        @Override
        public String getKey(WaterSensor value) throws Exception {
            return value.getId();
        }
    }
}
