package com.atguigu.thoughtworks;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.functions.co.RichCoFlatMapFunction;
import org.apache.flink.util.Collector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class CountAverageWithListState02
        extends RichCoFlatMapFunction<Tuple3<String, String, String>, Tuple3<String, String, String>,String> {

    /**
     * ListState ： 里面可以存很多数据
     */
    private ListState<Tuple3<String, String, String>> listTState;
    private ListState<Tuple3<String, String, String>> listQState;

    private String avgValue;

    /**
     * 这个方法其实是一个初始化的方法，只会执行一次
     * 我们可以用来注册我们的状态
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        // 注册状态
        ListStateDescriptor<Tuple3<String, String, String>> descriptorT =
                new ListStateDescriptor<>(
                        "sensorT", // 状态的名字
                        Types.TUPLE(Types.STRING, Types.STRING, Types.STRING)); // 状态存储的数据类型

        listTState = getRuntimeContext().getListState(descriptorT);

        // 注册状态
        ListStateDescriptor<Tuple3<String, String, String>> descriptorQ =
                new ListStateDescriptor<>(
                        "sensorQ", // 状态的名字
                        Types.TUPLE(Types.STRING, Types.STRING, Types.STRING)); // 状态存储的数据类型

        listQState = getRuntimeContext().getListState(descriptorQ);

    }


    /**
     * 处理T传感器数据
     * @param value
     * @param out
     * @throws Exception
     */
    @Override
    public void flatMap1(Tuple3<String, String, String> value, Collector<String> out) throws Exception {

        // 拿到当前的 key 的状态值
        Iterable<Tuple3<String, String, String>> currentState = listTState.get();

        // 如果状态值还没有初始化，则初始化
        if (currentState == null) {
            listTState.addAll(Collections.emptyList());
        }

        // 更新状态
        listTState.add(value);


        // 判断，如果当前的 key 出现了 3 次，则需要计算平均值，并且输出
        List<Tuple3<String, String, String>> allElements = Lists.newArrayList(listTState.get());

        if (allElements.size() == 3) {
            long count = 0;
            long sum = 0;
            for (Tuple3<String, String, String> ele : allElements) {
                count++;
                sum += Long.valueOf(ele.f2);
            }
            double avg = (double) sum / count;
             DecimalFormat df = new DecimalFormat("#.00");
            avgValue = df.format(avg);
            // out.collect("报表结果：\n" + "温度：" +value.f1 + " " + avgValue);

            String warningTitle = "输出："+"\n"+"异常检测结果："+"\n";
            if ((Double.valueOf(value.f2) - avg) > 3 ) {

                String printInfo = warningTitle + value.f0+","+value.f1+","+value.f2+";"+"温度"+"过高";
                out.collect(printInfo);
                // 清除状态
                listTState.clear();
            }
            // 清除状态
            //listTState.clear();
        }
    }

    /**
     * 处理Q传感器数据
     * @param value
     * @param out
     * @throws Exception
     */
    @Override
    public void flatMap2(Tuple3<String, String, String> value, Collector<String> out) throws Exception {

        // 拿到当前的 key 的状态值
        Iterable<Tuple3<String, String, String>> currentState = listQState.get();

        // 如果状态值还没有初始化，则初始化
        if (currentState == null) {
            listQState.addAll(Collections.emptyList());
        }

        // 更新状态
        listQState.add(value);

        // 取到Q1数据
        ArrayList<Tuple3<String, String, String>> allQElements = Lists.newArrayList(listQState.get());

        if (allQElements.size() == 5) {
            StringBuilder printInfo = new StringBuilder();
            String sensorType = null;
            String sensorTime;
            List<String> sensorTimeList = new ArrayList<>();
            List<Double> metricListAB = new ArrayList<>();
            List<Double> metricListAE = new ArrayList<>();
            List<Double> metricListCE = new ArrayList<>();
            for (int i = 0; i < allQElements.size(); i++) {
                sensorType = allQElements.get(i).f0;
                sensorTime = allQElements.get(i).f1;
                String[] values = allQElements.get(i).f2.split(",");
                sensorTimeList.add(sensorTime);
                for (int j = 0; j < values.length; j++) {
                    String[] metricStr = values[j].split(",");
                    String key = metricStr[0].split(":")[0];
                    String val = metricStr[0].split(":")[1];
                    if ("AB".equals(key)) {
                        // 取出AB指标值
                        Double metricValue = Double.valueOf(val);
                        metricListAB.add(metricValue);
                    }
                    if ("AE".equals(key)) {
                        // 取出AE指标值
                        Double metricValue = Double.valueOf(val);
                        metricListAE.add(metricValue);
                    }
                    if ("CE".equals(key)) {
                        // 取出CE指标值
                        Double metricValue = Double.valueOf(val);
                        metricListCE.add(metricValue);
                    }
                }
            }

            handleMetricValue(printInfo, sensorType, sensorTimeList, metricListAB, metricListAE, metricListCE);

            String printTitle = "报表结果：" + "\n";
            printInfo.append(printTitle + "温度：" + value.f1.substring(0,10) + " " + avgValue);
            out.collect(printInfo.toString());
            // 清除状态
            listQState.clear();

        }
    }

    /**
     * 处理指标值
     * @param printInfo 打印的信息
     * @param sensorType 传感器类型
     * @param sensorTimeList 传感器采集时间列表
     * @param metricListAB AB指标列表
     * @param metricListAE AE指标列表
     * @param metricListCE CE指标列表
     */
    private void handleMetricValue(StringBuilder printInfo, String sensorType, List<String> sensorTimeList, List<Double> metricListAB, List<Double> metricListAE, List<Double> metricListCE) {
        for (int i = 0; i < metricListAB.size(); i++) {
            double dv = metricListAB.get(i) * 1.01;
            if (i+2 == metricListAB.size()) {
                break;
            }
            if (dv < metricListAB.get(i+1) && dv < metricListAB.get(i+2)) {
                printInfo.append(sensorType + "," + sensorTimeList.get(i+1) + "," + "酸度"+":"+ metricListAB.get(i+1) + " " + "第一次" + "酸度" + "过高" + "\n");
                printInfo.append(sensorType + "," + sensorTimeList.get(i+2) + "," + "酸度"+":"+ metricListAB.get(i+2) + " " + "第二次" + "酸度" + "过高" + "\n");
            }
        }
        for (int i = 0; i < metricListAE.size(); i++) {
            double dv = metricListAE.get(i) * 1.01;
            if (i+2 == metricListAE.size()) {
                break;
            }
            if (dv < metricListAE.get(i+1) && dv < metricListAE.get(i+2)) {
                printInfo.append(sensorType + "," + sensorTimeList.get(i+1) + "," + "粘稠度"+":"+ metricListAE.get(i+1) + " " + "第一次" + "粘稠度" + "过高" + "\n");
                printInfo.append(sensorType + "," + sensorTimeList.get(i+2) + "," + "粘稠度"+":"+ metricListAE.get(i+2) + " " + "第二次" + "粘稠度" + "过高" + "\n");
            }
        }
        for (int i = 0; i < metricListCE.size(); i++) {
            double dv = metricListCE.get(i) * 1.01;
            if (i+2 == metricListCE.size()) {
                break;
            }
            if (dv < metricListCE.get(i+1) && dv < metricListCE.get(i+2)) {
                printInfo.append(sensorType + "," + sensorTimeList.get(i+1) + "," + "含水量"+":"+ metricListCE.get(i+1) + " " + "第一次" + "含水量" + "过高" + "\n");
                printInfo.append(sensorType + "," + sensorTimeList.get(i+2) + "," + "含水量"+":"+ metricListCE.get(i+2) + " " + "第二次" + "含水量" + "过高" + "\n");
            }
        }
    }
}
