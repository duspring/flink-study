package com.atguigu.naixue.lesson01;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @author: spring du
 * @description:
 * @date: 2020/12/9 11:18
 */
public class WordCount {
    public static void main(String[] args) throws Exception {
        // TODO step1: 初始化程序入口
        StreamExecutionEnvironment env = StreamExecutionEnvironment
                .createLocalEnvironmentWithWebUI(new Configuration());
        // TODO 设置任务的并行度为2
        env.setParallelism(2);

        // step2: 数据的输入
        // socket支持的并行度就是1
        String hostname = "10.40.70.176";
        int port = 9999;
        DataStreamSource<String> dataStream = env.socketTextStream(hostname, port); // 1 task
        // step3: 数据的处理
        SingleOutputStreamOperator<WordAndCount> result = dataStream
                .flatMap(new SplitWordFunction()) // 2 task(subtask)
                .keyBy("word")
                .sum("count"); // 2 task subtask
        // step4: 数据的输出
        result.print(); // 2 task sink

        // step5: 启动程序
        env.execute("test word count...");
    }

    /**
     * 分隔单词
     */
    public static class SplitWordFunction implements FlatMapFunction<String, WordAndCount> {

        @Override
        public void flatMap(String line, Collector<WordAndCount> out) throws Exception {
            String[] fields = line.split(",");
            for (String word : fields) {
                out.collect(new WordAndCount(word, 1));
            }
        }
    }

    public static class WordAndCount {
        private String word;
        private Integer count;

        public WordAndCount() {
        }

        public WordAndCount(String word, Integer count) {
            this.word = word;
            this.count = count;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

    /**
     * 数据传输策略
     * 1. forward strategy  -> 满足 1>一个task的输出只发送给一个task作为输入 2> 如果两个task都在一个JVM中的话，那么就可以避免网络开销
     * 2. key based strategy (keyBY)
     * 3. broadcast strategy
     * 4. random strategy
     *
     * 发送Operator Chain的条件：
     * 1. 数据传输策略是 forward strategy
     * 2. 在同一个 TaskManager中运行
     *
     */

}
