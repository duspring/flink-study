package com.atguigu.thoughtworks;

import com.atguigu.bean.SensorEntity;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author: spring du
 * @description:
 * @date: 2021/1/5 15:50
 */
public class ComputeAverageWithListState extends RichFlatMapFunction<SensorEntity, SensorEntity> {

    private ListState<SensorEntity> elementsByKey;

    /**
     *
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        // 注册状态
        ListStateDescriptor<SensorEntity> descriptor =
                new ListStateDescriptor<SensorEntity>(
                        "average", // 状态的名字
                        TypeInformation.of(SensorEntity.class)); // 状态存储的数据类型
        elementsByKey = getRuntimeContext().getListState(descriptor);
    }

    /**
     * 这个方法是一个初始化的方法，只会执行一次
     * 我们可以用来注册我们的状态
     * @param element
     * @param out
     * @throws Exception
     */
    @Override
    public void flatMap(SensorEntity element, Collector<SensorEntity> out) throws Exception {

        if ("T1".equals(element.getSensorType())) {

            // 拿到当前的 key 的状态值
            Iterable<SensorEntity> currentState = elementsByKey.get();

            // 如果状态值还没有初始化，则初始化
            if (currentState == null) {
                elementsByKey.addAll(Collections.emptyList());
            }

            // 更新状态
            elementsByKey.add(element);

            // 判断，如果当前的 key 出现了 3 次，则需要计算平均值，并且输出
            ArrayList<SensorEntity> allElements = Lists.newArrayList(elementsByKey.get());

            if (allElements.size() == 3) {
                long count = 0;
                long sum = 0;
                for (SensorEntity ele : allElements) {
                    count++;
                    sum += Long.valueOf(ele.getValue());
                }

                double avg = (double) sum / count;
                if ((Double.valueOf(element.getValue()) - avg) < 5 ) {
                    System.out.println(element.getSensorType()+","+element.getSensorTime()+element.getValue()+";温度过高");
                }

                out.collect(new SensorEntity(element.getSensorTime(), avg));
                // 清除状态
                elementsByKey.clear();
            }
        }

    }
}
