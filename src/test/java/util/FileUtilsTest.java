package util;

import com.atguigu.bean.FileUtils;
import com.atguigu.bean.SensorQ;
import com.atguigu.bean.SensorT;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: spring du
 * @Date: 2021/1/5 14:30
 */
public class FileUtilsTest {

    @Test
    public void readFileTest() {
        List<String> inputs = FileUtils.readFile("ioT.txt");
//        inputs.forEach(line -> System.out.println(line));
        List<String> sensorTList = new ArrayList<>();
        List<String> sensorQList = new ArrayList<>();
        inputs.forEach(line -> {
            if (line.startsWith("T1")) {
                sensorTList.add(line.substring(0, line.length()-1));
            } else {
                sensorQList.add(line.substring(0, line.length()-1));
            }
        });

        System.out.println("T->" + sensorTList);
        System.out.println("Q->" + sensorQList);
    }
}
