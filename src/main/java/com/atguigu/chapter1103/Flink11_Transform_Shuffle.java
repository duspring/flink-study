package com.atguigu.chapter1103;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: spring du
 * @description:
 * @date: 2020/11/3 15:35
 */
public class Flink11_Transform_Shuffle {
    public static void main(String[] args) throws Exception {
        // 0. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        // 1. 从文件读取数据
        DataStreamSource<String> inputDS = env.readTextFile("input/sensor-data.log");
        inputDS.print("input");

        DataStream<String> resultDS = inputDS.shuffle();
        resultDS.print("shuffle");

        env.execute();

    }
}
