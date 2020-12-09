package com.atguigu.chapter1103;

import com.atguigu.bean.WaterSensor;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: spring du
 * @description:
 * @date: 2020/11/3 13:50
 */
public class Flink07_Transform_RichMapFunction {
    public static void main(String[] args) throws Exception {
        // 0. 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
//        env.setParallelism(2);
        // 1. 从文件读取数据
        DataStreamSource<String> inputDS = env.readTextFile("input/sensor-data.log");
//        DataStreamSource<String> inputDS = env.socketTextStream("10.40.70.176", 9999);

        // 2. Transform: Map转换成实体对象
        SingleOutputStreamOperator<WaterSensor> sensorDS = inputDS.map(new MyRichMapFunction());

        sensorDS.print();

        env.execute();
    }

    /**
     * 继承RichMapFunction，指定输入的类型，返回的类型
     * 重写map方法
     * 提供了open() 和 close() 生命周期管理方法
     * 能够获取 运行时上下文对象 -> 可以获取 状态、任务信息 等环境信息
     *
     */
    public static class MyRichMapFunction extends RichMapFunction<String, WaterSensor> {

        @Override
        public WaterSensor map(String value) throws Exception {
            String[] datas = value.split(",");
            return new WaterSensor(getRuntimeContext().getTaskName() + datas[0], Long.valueOf(datas[1]), Integer.valueOf(datas[2]));
        }

        @Override
        public void open(Configuration parameters) throws Exception {
            System.out.println("open....");
        }

        @Override
        public void close() throws Exception {
            System.out.println("close....");
        }
    }
}
