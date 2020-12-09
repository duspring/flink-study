package com.atguigu.naixue.lesson01;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author: spring du
 * @description:
 *
 * MapState<K, V> : 这个状态为每一个 key 保存一个 Map 集合
 *
 *   put() 将对应的 key 的键值对放到状态中
 *   values() 拿到 MapState 中所有的 value
 *   clear() 清除状态
 *
 *
 * @date: 2020/12/9 17:19
 */
public class CountAverageWithListState
        extends RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, Double>> {

    // managed keyed state
    // 1. MapState: key是一个唯一的值，value 是接收到的相同的 key 对应的 value的值

    /**
     * ValueState: 里面只能存一条元素
     * ListState: 里面可以存很多数据
     *
     * MapState:
     *      Map集合的特点：相同key，会覆盖数据
     *      1,2
     *      1,4
     */
    private MapState<String, Long> mapState;


    /**
     * 这个方法其实是一个初始化的方法，只会执行一次
     * 我们可以用来注册我们的状态
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        // 注册状态
        MapStateDescriptor<String, Long> descriptor =
                new MapStateDescriptor<>(
                        "average", // 状态的名字
                        String.class, Long.class); // 状态存储的数据类型
        mapState = getRuntimeContext().getMapState(descriptor);

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

        mapState.put(UUID.randomUUID().toString(), element.f1);

        // 判断，如果当前的 key 出现了 3次，则需要计算平均值，并且输出
        List<Long> allElements = Lists.newArrayList(mapState.values());

        if (allElements.size() == 3) {
            long count = 0;
            long sum = 0;
            for (Long ele : allElements) {
                count++;
                sum += ele;
            }
            double avg = (double) sum / count;
            //
            out.collect(Tuple2.of(element.f0, avg));

            // 清除状态
            mapState.clear();

        }

    }
}
