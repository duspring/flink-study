package com.atguigu.naixue.lesson01;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.AggregatingState;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

/**
 * @author: spring du
 * @description:
 *
 * AggregatingState<T> :
 *
 *
 *
 * @date: 2020/12/9 17:19
 */
public class ContainsValueFunction
        extends RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, String>> {

    // managed keyed state
    private AggregatingState<Long, String> totalStr;


    /**
     * 这个方法其实是一个初始化的方法，只会执行一次
     * 我们可以用来注册我们的状态
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        // 注册状态
        AggregatingStateDescriptor<Long, String, String> descriptor =
                new AggregatingStateDescriptor<>(
                        "totalStr", // 状态的名字
                        //  Spark
                        new AggregateFunction<Long, String, String>() {
                            @Override
                            public String createAccumulator() {
                                return null;
                            }

                            @Override
                            public String add(Long value, String accumulator) {
                                return null;
                            }

                            @Override
                            public String getResult(String accumulator) {
                                return null;
                            }

                            @Override
                            public String merge(String a, String b) {
                                return null;
                            }
                        }, String.class); // 状态存储的数据类型

        totalStr = getRuntimeContext().getAggregatingState(descriptor);

    }

    /**
     * 每来一条数据，都会调用这个方法
     * key相同
     * @param element
     * @param out
     * @throws Exception
     */
    @Override
    public void flatMap(Tuple2<Long, Long> element, Collector<Tuple2<Long, String>> out) throws Exception {
        totalStr.add(element.f1);
        out.collect(Tuple2.of(element.f0, totalStr.get()));
    }

}
