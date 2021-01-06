package com.atguigu.thoughtworks;

import com.atguigu.bean.FileUtils;
import com.atguigu.bean.SensorInEntity;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

/**
 * @author: spring du
 * @description:
 * @date: 2021/1/5 14:30
 */
public class IoTHandler {
    public static void main(String[] args) throws Exception {

        List<String> inputs = FileUtils.readFile("ioT.txt");
        inputDisplay(inputs);

        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置并行度
        env.setParallelism(1);

        // 从文件读取数据、转换成 bean对象
        SingleOutputStreamOperator<SensorInEntity> sensorDS = env.readTextFile("input/ioT.txt")
                .map((MapFunction<String, SensorInEntity>) value -> new SensorInEntity(value));

        sensorDS.keyBy(new MyKeySelector())
        .flatMap(new ComputeAverageWithListState())
        .print();

        env.execute();

    }

    /**
     * MyKeySelector
     */
    public static class MyKeySelector implements KeySelector<SensorInEntity, String> {
        @Override
        public String getKey(SensorInEntity value) throws Exception {
            return value.getSensorType();
        }
    }

    /**
     * 显示输入内容
     * @param inputs
     */
    private static void inputDisplay(List<String> inputs) {
        System.out.println("输入:");
        inputs.forEach(line -> System.out.println(line));
        System.out.println();
    }
}
