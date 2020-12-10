package com.atguigu.naixue.lesson01;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: spring du
 * @description: 需求：每两个元素输出一次
 * @date: 2020/12/10 9:54
 */
public class TestOperatorStateMain {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStreamSource<Tuple2<String, Integer>> dataStreamSource = env.fromElements(
                Tuple2.of("Spark", 3),
                Tuple2.of("Flink", 5),
                Tuple2.of("Hadoop", 7),
                Tuple2.of("Spark", 4));

        // source operator -> Operator state -> offset

        dataStreamSource
                .addSink(new CustomSink(2)).setParallelism(1);

        env.execute("TestOperatorStateMain...");

    }
}
