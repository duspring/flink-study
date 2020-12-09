package com.atguigu.naixue.lesson01;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

/**
 * @author: spring du
 * @description:
 *
 * ReducingState<T> : 这个状态为每一个 key 保存一个 聚合之后的值
 *
 *   get() 获取状态值
 *   add() 更新状态值，将数据放到状态中
 *   clear() 清除状态
 *
 *
 * @date: 2020/12/9 17:19
 */
public class SumReducingStateFunction
        extends RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, Long>> {

    // managed keyed state
    // 用于保存每一个key 对应的 value的总值
    private ReducingState<Long> sumState;


    /**
     * 这个方法其实是一个初始化的方法，只会执行一次
     * 我们可以用来注册我们的状态
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        // 注册状态
        ReducingStateDescriptor descriptor = new ReducingStateDescriptor<>(
                "sum", // 状态的名字
                new ReduceFunction<Long>() { // 聚合函数
                    @Override
                    public Long reduce(Long value1, Long value2) throws Exception {
                        return value1 + value2;
                    }
                }, Long.class);// 状态存储的数据类型


        sumState = getRuntimeContext().getReducingState(descriptor);

    }

    /**
     * 每来一条数据，都会调用这个方法
     * key相同
     * @param element
     * @param out
     * @throws Exception
     */
    @Override
    public void flatMap(Tuple2<Long, Long> element,
                        Collector<Tuple2<Long, Long>> out) throws Exception {
            // 将数据放到状态中
            sumState.add(element.f1);
            out.collect(Tuple2.of(element.f0, sumState.get()));

        }
}
