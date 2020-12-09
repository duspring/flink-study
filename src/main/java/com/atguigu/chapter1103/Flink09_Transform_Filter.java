package com.atguigu.chapter1103;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

/**
 * @author: spring du
 * @description:
 * @date: 2020/11/3 14:43
 */
public class Flink09_Transform_Filter {
    public static void main(String[] args) throws Exception {
        // 0. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        // 1. 从文件读取数据
        DataStreamSource<Integer> inputDS = env.fromCollection(
                Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)
        );

        // 2. TODO Transform：filter => 为true保留，为false丢弃
        inputDS.filter(new MyFilterFunction()).print();

        env.execute();
    }

    public static class MyFilterFunction implements FilterFunction<Integer> {

        @Override
        public boolean filter(Integer value) throws Exception {
            return value % 2 == 0;
        }
    }
}
