package com.atguigu.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 * @Auther: spring du
 * @Date: 2021/1/5 14:30
 */
public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     *
     * @param filename
     * @return
     */
    public static List<String> readFile(String filename) {
        List<String> list = new ArrayList<>();
        InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(filename);
        if (null == inputStream) {
            String msg = "未找到文件：" + filename;
            if (log.isErrorEnabled()) {
                log.error("未找到文件：" + filename);
            }
            throw new FileException(msg);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String lineStr;
        try {
            while (null != (lineStr = br.readLine())) {
                list.add(lineStr);
            }
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("文件读取异常");
            }
            e.printStackTrace();
        }

        return list;
    }
}
