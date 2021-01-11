package com.atguigu.thoughtworks;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.util.Collector;

import java.text.DecimalFormat;
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
public class CountAverageWithListState01
        extends RichFlatMapFunction<Tuple3<String, String, String>, Tuple3<String, String, String>> {

    /**
     * ListState ： 里面可以存很多数据
     */
    private ListState<Tuple3<String, String, String>> listState;


    /**
     * 这个方法其实是一个初始化的方法，只会执行一次
     * 我们可以用来注册我们的状态
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        // 注册状态
        ListStateDescriptor<Tuple3<String, String, String>> descriptor =
                new ListStateDescriptor<>(
                        "average", // 状态的名字
                        Types.TUPLE(Types.STRING, Types.STRING, Types.STRING)); // 状态存储的数据类型
        listState = getRuntimeContext().getListState(descriptor);

    }


    @Override
    public void flatMap(Tuple3<String, String, String> value,
                        Collector<Tuple3<String, String, String>> out) throws Exception {

        // 拿到当前的 key 的状态值
        Iterable<Tuple3<String, String, String>> currentState = listState.get();

        // 如果状态值还没有初始化，则初始化
        if (currentState == null) {
            listState.addAll(Collections.emptyList());
        }

        // 更新状态
        listState.add(value);


        // 判断，如果当前的 key 出现了 3 次，则需要计算平均值，并且输出
        List<Tuple3<String, String, String>> allElements = Lists.newArrayList(listState.get());

        if (allElements.size() == 3) {
            long count = 0;
            long sum = 0;
            for (Tuple3<String, String, String> ele : allElements) {
                count++;
                sum += Long.valueOf(ele.f2);
            }
            double avg = (double) sum / count;
            DecimalFormat df = new DecimalFormat("#.00");
            String avgValue = df.format(avg);
            out.collect(Tuple3.of("温度：", value.f1, avgValue));
            // 清除状态
            listState.clear();
        }

    }
}
