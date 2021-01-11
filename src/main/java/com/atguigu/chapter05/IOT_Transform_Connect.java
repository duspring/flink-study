//package com.atguigu.chapter05;
//
//import com.atguigu.bean.SensorInEntity;
//import com.atguigu.bean.SensorInEntity01;
//import com.atguigu.bean.WaterSensor;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
//import org.apache.flink.streaming.api.datastream.ConnectedStreams;
//import org.apache.flink.streaming.api.datastream.DataStreamSource;
//import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.functions.co.CoMapFunction;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * TODO
// *
// * @author cjp
// * @version 1.0
// * @date 2020/9/16 16:55
// */
//public class IOT_Transform_Connect {
//    public static void main(String[] args) throws Exception {
//        // 0.创建执行环境
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
//
//        // 1.从文件读取数据
//        SingleOutputStreamOperator<SensorInEntity> sensorTDS = env
//                .readTextFile("input/ioT.txt")
//                .map(new MapFunction<String, SensorInEntity>() {
//                    @Override
//                    public SensorInEntity map(String value) throws Exception {
//                        return new SensorInEntity(value);
//                    }
//                });
//
//        // 再获取一条流
//        SingleOutputStreamOperator<SensorInEntity01> sensorQDS = env
//                .readTextFile("input/ioT.txt")
//                .map(new MapFunction<String, SensorInEntity01>() {
//                    @Override
//                    public SensorInEntity01 map(String value) throws Exception {
//                        return new SensorInEntity01(value);
//                    }
//                });
//
//        // TODO 使用connect连接两条流
//        // 两条流 数据类型 可以不一样
//        // 只能两条流进行连接
//        // 处理数据的时候，也是分开处理
//        ConnectedStreams<SensorInEntity, SensorInEntity01> sensorNumTQDS = sensorTDS.connect(sensorQDS);
//
//        // 调用其他算子
//        SingleOutputStreamOperator<String> resultDS = sensorNumTQDS.map(
//                new CoMapFunction<SensorInEntity, SensorInEntity01, String>() {
//
//                    List<SensorInEntity> TList = new ArrayList<>();
//                    List<SensorInEntity> QList = new ArrayList<>();
//                    String printInfo = "";
//                    @Override
//                    public String map1(SensorInEntity value) throws Exception {
//                        if ("T1".equals(value.getSensorType())) {
//                            TList.add(value);
//                            long count = 0;
//                            long sum = 0;
//                            for (SensorInEntity ele : TList) {
//                                count++;
//                                sum += Long.valueOf(ele.getValue());
//                            }
//                            String warningTitle = "输出："+"\n"+"异常检测结果："+"\n";
//                            double avg = (double) sum / count;
//                            if ((Double.valueOf(value.getValue()) - avg) > 3 ) {
//                                printInfo = warningTitle + value.getSensorType()+","+value.getSensorTime()+","+value.getValue()+";温度过高";
//                                System.out.println(printInfo);
//                            }
//                            return printInfo;
//                        } else {
//                            return "";
//                        }
//                    }
//
//                    @Override
//                    public String map2(SensorInEntity01 value) throws Exception {
//                        if ("Q1".equals(value.getSensorType())) {
//                            return value.toString();
//                        } else {
//                            return "";
//                        }
//                    }
//                });
//
//        resultDS.print();
//
//        env.execute();
//    }
//}
