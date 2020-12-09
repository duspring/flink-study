package com.atguigu.naixue.lesson01;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.util.Collector;

import java.util.Collections;
import java.util.List;

/**
 * @author: spring du
 * @description:
 *
 * ListState<T> : 这个状态为每一个 key 保存集合的值
 *
 *   get() 获取状态值
 *   add() / addAll() 更新状态值, 将数据放到状态中
 *   clear() 清除状态
 *
 *
 * @date: 2020/12/9 17:12
 */
public class CountAverageWithMapState
        extends RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, Double>> {

    // managed keyed state
    // 1. ListState 保存的是对应的一个 key 的出现的所有的元素

    /**
     * ValueState: 里面只能存一条元素
     * ListState: 里面可以存很多数据
     */
    private ListState<Tuple2<Long, Long>> elementsByKey;


    /**
     * 这个方法其实是一个初始化的方法，只会执行一次
     * 我们可以用来注册我们的状态
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        // 注册状态
        ListStateDescriptor<Tuple2<Long, Long>> descriptor =
                new ListStateDescriptor<>(
                        "average", // 状态的名字
                        Types.TUPLE(Types.LONG, Types.LONG)); // 状态存储的数据类型
        elementsByKey = getRuntimeContext().getListState(descriptor);

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
                        Collector<Tuple2<Long, Double>> out) throws Exception {
        // 获取当前的 key 的状态值
        Iterable<Tuple2<Long, Long>> currentState = elementsByKey.get();

        // 如果状态值还没有初始化，则初始化
        if (currentState == null) {
            elementsByKey.addAll(Collections.emptyList());
        }

        // 更新到状态里面
        elementsByKey.add(element);

        // 判断，如果当前的 key 出现了 3次，则需要计算平均值，并且输出

        List<Tuple2<Long, Long>> allElements = Lists.newArrayList(elementsByKey.get());

        if (allElements.size() == 3) {
            long count = 0;
            long sum = 0;
            for (Tuple2<Long, Long> ele : allElements) {
                count++;
                sum += ele.f1;
            }
            double avg = (double) sum / count;
            out.collect(Tuple2.of(element.f0, avg));

            // 清除状态
            elementsByKey.clear();

        }

    }
}
