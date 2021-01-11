package com.atguigu.thoughtworks;

import com.atguigu.bean.FileUtils;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: spring du
 * @description:
 *
 * 需求：当接收到的相同的 key 的元素个数等于 3个 就计算这些元素的value的平均值。
 * 计算keyed stream 中每3个元素的 value 的平均值
 *
 * @date: 2020/12/9 16:07
 */
public class TestKeyedStateMain02 {
    public static void main(String[] args) throws Exception {
        // 获取执行环境
        StreamExecutionEnvironment env =
                StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置并行度
        env.setParallelism(1);
        // 获取数据源
        List<String> inputs = FileUtils.readFile("ioT.txt");
        List<Tuple3<String, String, String>> sensorTList = new ArrayList<>();
        List<Tuple3<String, String, String>> sensorQList = new ArrayList<>();
        inputs.forEach(line -> {
            String newData = line.substring(0, line.length() - 1);
            String sensorFlag = newData.split(",")[0];
            String[] values = newData.split(",");
            if (line.startsWith("T1")) {
                sensorTList.add(Tuple3.of(sensorFlag, values[1], values[2]));
            } else {
                sensorQList.add(Tuple3.of(sensorFlag, values[1], values[2] + "," +values[3] + "," + values[4]));
            }
        });

        /*DataStreamSource<Tuple3<String, String, String>> dataStreamTSource = env.fromElements(
                Tuple3.of("T1", "2020-01-30 19:00:01", "25"),
                Tuple3.of("T1", "2020-01-30 19:00:01", "22"),
                Tuple3.of("T1", "2020-01-30 19:00:02", "29")

        );

        DataStreamSource<Tuple3<String, String, String>> dataStreamQSource = env.fromElements(
                Tuple3.of("Q1", "2020-01-30 19:30:10", "AB:37.8,AE:100,CE:0.11"),
                Tuple3.of("Q1", "2020-01-30 19:30:20", "AB:48.9,AE:100,CE:0.11"),
                Tuple3.of("Q1", "2020-01-30 19:30:25", "AB:37.8,AE:100,CE:0.11"),
                Tuple3.of("Q1", "2020-01-30 19:30:32", "AB:48.9,AE:101,CE:0.11"),
                Tuple3.of("Q1", "2020-01-30 19:30:40", "AB:49.9,AE:103,CE:0.11")

        );*/

        // T
        DataStreamSource<Tuple3<String, String, String>> dataStreamTSource = env.fromCollection(sensorTList);

        // Q
        DataStreamSource<Tuple3<String, String, String>> dataStreamQSource = env.fromCollection(sensorQList);


        ConnectedStreams<Tuple3<String, String, String>, Tuple3<String, String, String>> connectDataStreamSource =
                dataStreamTSource.connect(dataStreamQSource);

        ConnectedStreams<Tuple3<String, String, String>, Tuple3<String, String, String>> connectedStreams =
                connectDataStreamSource.keyBy(0, 0);

        connectedStreams.flatMap(new CountAverageWithListState02()).print();



//        dataStreamSource
//                .keyBy(0) // 前提是要经过keyBy操作
//                .flatMap(new CountAverageWithListState01()) // 相同的key到肯定会到同一个state中
//                .print();

        env.execute("TestStatefulApi");
    }
}
