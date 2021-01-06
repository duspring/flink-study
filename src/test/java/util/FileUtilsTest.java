package util;

import com.atguigu.bean.FileUtils;
import org.junit.Test;

import java.util.List;

/**
 * @Auther: spring du
 * @Date: 2021/1/5 14:30
 */
public class FileUtilsTest {

    @Test
    public void readFileTest() {
        List<String> inputs = FileUtils.readFile("ioT.txt");
        inputs.forEach(line -> System.out.println(line));
    }
}
