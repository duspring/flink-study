package com.atguigu.naixue.lesson01;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.aggregation.SumFunction;

/**
 * @author: spring du
 * @description:
 *
 * 需求：
 *
 * @date: 2020/12/9 17:34
 */
public class TestKeyedStateMain2 {
    public static void main(String[] args) throws Exception {
        // 获取执行环境
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置并行度
        env.setParallelism(16);
        // 获取数据源
        DataStreamSource<Tuple2<Long, Long>> dataStreamSource =
                env.fromElements(
                        Tuple2.of(1L, 3L),
                        Tuple2.of(1L, 7L),
                        Tuple2.of(2L, 4L),
                        Tuple2.of(1L, 5L),
                        Tuple2.of(2L, 2L),
                        Tuple2.of(2L, 6L));


        /**
         *
         * 1,3
         * 1,7 -> 1,10
         * 1,5 -> 1,15
         *
         *
         */

        dataStreamSource
                .keyBy(0) // 前提是要经过keyBy操作
                .flatMap(new SumReducingStateFunction()) // 相同的key到肯定会到同一个state中
//                .sum(1)
                .print();

        env.execute("TestStatefulApi");
    }
}
