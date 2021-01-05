package com.atguigu.thoughtworks;

import com.atguigu.bean.MetricTypeEnum;
import com.atguigu.bean.SensorInEntity;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.util.Collector;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author: spring du
 * @description:
 * @date: 2021/1/5 15:50
 */
public class ComputeAverageWithListState extends RichFlatMapFunction<SensorInEntity, String> {

    private ListState<SensorInEntity> elementsByKey;

    /**
     *
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        // 注册状态
        ListStateDescriptor<SensorInEntity> descriptor =
                new ListStateDescriptor<>(
                        "average", // 状态的名字
                        TypeInformation.of(SensorInEntity.class)); // 状态存储的数据类型
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
    public void flatMap(SensorInEntity element, Collector<String> out) throws Exception {

        if ("T1".equals(element.getSensorType())) {

            // 拿到当前的 key 的状态值
            Iterable<SensorInEntity> currentState = elementsByKey.get();

            // 如果状态值还没有初始化，则初始化
            if (currentState == null) {
                elementsByKey.addAll(Collections.emptyList());
            }

            // 更新状态
            elementsByKey.add(element);

            // 判断，如果当前的 key 出现了 3 次，则需要计算平均值，并且输出
            ArrayList<SensorInEntity> allElements = Lists.newArrayList(elementsByKey.get());

            if (allElements.size() == 3) {
                long count = 0;
                long sum = 0;
                for (SensorInEntity ele : allElements) {
                    count++;
                    sum += Long.valueOf(ele.getValue());
                }

                String warningTitle = "异常检测结果：\n";
                double avg = (double) sum / count;
                if ((Double.valueOf(element.getValue()) - avg) > 3 ) {
                    System.out.println(warningTitle + element.getSensorType()+","+element.getSensorTime()+element.getValue()+";温度过高");
                }

                String printTitle = "报表结果：\n";
                DecimalFormat df = new DecimalFormat("#.00");
                String printInfo = printTitle + "温度：" + element.getSensorTime().substring(0,10) + " " + df.format(avg);
                out.collect(printInfo);
                // 清除状态
                elementsByKey.clear();
            }
        } else if ("Q1".equals(element.getSensorType())) {

            // 拿到当前的 key 的状态值
            Iterable<SensorInEntity> currentState = elementsByKey.get();

            // 如果状态值还没有初始化，则初始化
            if (currentState == null) {
                elementsByKey.addAll(Collections.emptyList());
            }

            // 更新状态
            elementsByKey.add(element);

            //
            ArrayList<SensorInEntity> allElements = Lists.newArrayList(elementsByKey.get());

            if (allElements.size() == 5) {
                List<Double> metricList = new ArrayList<>();
                List<Double> metricList01 = new ArrayList<>();
                for (int i = 0; i < allElements.size(); i++) {
                    // AB:37.8,AE:100,CE:0.11
//                    System.out.println(allElements.get(i).getValue());
                    String[] values = allElements.get(i).getValue().split(",");
                    for (int j = 0; j < values.length; j++) {
//                        System.out.println(values[j]);
                        String[] metricStr = values[j].split(":");
//                        System.out.println(Arrays.toString(metricStr));
                        if (MetricTypeEnum.AB.getCode().equals(metricStr[0])) {
                            // 取出指标值
                            Double metricValue = Double.valueOf(metricStr[1]);
                            Double metricValue01 = Double.valueOf(metricStr[1]);
//                            System.out.println(metricValue);
                            metricList.add(metricValue);
                            DecimalFormat df = new DecimalFormat("#.00");
                            Double aDouble = Double.valueOf(df.format(metricValue01 * 1.1));
                            metricList01.add(aDouble);
                        }
                    }
                }
                System.out.println(metricList);
                System.out.println(metricList01);
            }


//            String value = element.getValue();
//            System.out.println(value);
        }

    }
}
