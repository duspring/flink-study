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

    private Double avg;

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

                String warningTitle = "输出：\n异常检测结果：\n";
                avg = (double) sum / count;
                if ((Double.valueOf(element.getValue()) - avg) > 3 ) {

                    String printInfo = warningTitle + element.getSensorType()+","+element.getSensorTime()+","+element.getValue()+";温度过高";
                    out.collect(printInfo);
                    // 清除状态
                    elementsByKey.clear();
                }
            }
        } else if ("Q1".equals(element.getSensorType())) {
            // Q1,2020-01-30 19:30:10,AB:37.8,AE:100,CE:0.11;

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
                String sensorType = null;
                String sensorTime = null;
                List<String> sensorTimeList = new ArrayList<>();
                List<Double> metricListAB = new ArrayList<>();
                List<Double> metricListAE = new ArrayList<>();
                List<Double> metricListCE = new ArrayList<>();
                for (int i = 0; i < allElements.size(); i++) {
                    sensorType = allElements.get(i).getSensorType();
                    sensorTime = allElements.get(i).getSensorTime();
                    // AB:37.8,AE:100,CE:0.11
                    String[] values = allElements.get(i).getValue().split(",");
                    // [AB:37.8,AE:100,CE:0.11]
                    sensorTimeList.add(sensorTime);
                    for (int j = 0; j < values.length; j++) {
                        String[] metricStr = values[j].split(":");
                        if (MetricTypeEnum.AB.getCode().equals(metricStr[0])) {
                            // 取出AB指标值
                            Double metricValue = Double.valueOf(metricStr[1]);
                            metricListAB.add(metricValue);
                        }
                        if (MetricTypeEnum.AE.getCode().equals(metricStr[0])) {
                            // 取出AE指标值
                            Double metricValue = Double.valueOf(metricStr[1]);
                            metricListAE.add(metricValue);
                        }
                        if (MetricTypeEnum.CE.getCode().equals(metricStr[0])) {
                            // 取出CE指标值
                            Double metricValue = Double.valueOf(metricStr[1]);
                            metricListCE.add(metricValue);
                        }
                    }
                }
                // System.out.println(sensorTimeList);
                for (int i = 0; i < metricListAB.size(); i++) {
                    double dv = metricListAB.get(i) * 1.01;
                    if (i+2 == metricListAB.size()) {
                        break;
                    }
                    if (dv < metricListAB.get(i+1) && dv < metricListAB.get(i+2)) {
                        System.out.println(sensorType + "," + sensorTimeList.get(i+1) + "," + MetricTypeEnum.AB.getDesc()+":"+ metricListAB.get(i+1) + " 第一次"+MetricTypeEnum.AB.getDesc()+"过高");
                        System.out.println(sensorType + "," + sensorTimeList.get(i+2) + "," + MetricTypeEnum.AB.getDesc()+":"+ metricListAB.get(i+2) + " 第二次"+MetricTypeEnum.AB.getDesc()+"过高");
                    }
                }
                for (int i = 0; i < metricListAE.size(); i++) {
                    double dv = metricListAE.get(i) * 1.01;
                    if (i+2 == metricListAE.size()) {
                        break;
                    }
                    if (dv < metricListAE.get(i+1) && dv < metricListAE.get(i+2)) {
                        //System.out.println(i+1 + "->" + metricList01.get(i+1));
                        //System.out.println(i+2 + "->" + metricList01.get(i+2));
                        System.out.println(sensorType + "," + sensorTime + "," + MetricTypeEnum.AE.getDesc()+":"+ metricListAE.get(i+1) + " 第一次"+MetricTypeEnum.AE.getDesc()+"过高");
                        System.out.println(sensorType + "," + sensorTime + "," + MetricTypeEnum.AE.getDesc()+":"+ metricListAE.get(i+2) + " 第二次"+MetricTypeEnum.AE.getDesc()+"过高");
                    }
                }
                for (int i = 0; i < metricListCE.size(); i++) {
                    double dv = metricListCE.get(i) * 1.01;
                    if (i+2 == metricListCE.size()) {
                        break;
                    }
                    if (dv < metricListCE.get(i+1) && dv < metricListCE.get(i+2)) {
                        //System.out.println(i+1 + "->" + metricList02.get(i+1));
                        //System.out.println(i+2 + "->" + metricList02.get(i+2));
                        System.out.println(sensorType + "," + sensorTime + "," + MetricTypeEnum.CE.getDesc()+":"+ metricListCE.get(i+1) + " 第一次"+MetricTypeEnum.CE.getDesc()+"过高");
                        System.out.println(sensorType + "," + sensorTime + "," + MetricTypeEnum.CE.getDesc()+":"+ metricListCE.get(i+2) + " 第二次"+MetricTypeEnum.CE.getDesc()+"过高");
                    }
                }

                String printTitle = "报表结果：\n";
                DecimalFormat df = new DecimalFormat("#.00");
                String printInfo = printTitle + "温度：" + element.getSensorTime().substring(0,10) + " " + df.format(avg);;
                out.collect(printInfo);
                // 清除状态
                elementsByKey.clear();

            }

        }

    }
}
