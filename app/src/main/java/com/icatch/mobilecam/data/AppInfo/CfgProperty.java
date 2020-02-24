package com.icatch.mobilecam.data.AppInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhang yanhu C001012 on 2015/11/17 14:38.
 */
public class CfgProperty {

    private static Map<String, String> propertyMap = new HashMap<String, String>();
    private String fileName;
    public CfgProperty(String fileName) {
        this.fileName = fileName;
    }

    public String getProperty(String key) throws FileNotFoundException, IOException {
        String value = CfgProperty.propertyMap.get(key);
        Properties property = new Properties();
        FileInputStream inputFile = null;
        if (value == null) {
            // 实例化inputFile,如config.properties文件的位置
            inputFile = new FileInputStream(this.fileName);
            // 装载配置文件
            property.load(inputFile);

            value = property.getProperty(key);
            CfgProperty.propertyMap.put(key, value);
        }
        return value;
    }

    public Map<String, String> getProperty(List<String> propertyList) throws FileNotFoundException, IOException {
        // 定义Map用于存放结果
        Map<String, String> propertyMap = new HashMap<String, String>();
        // 定义Properties property = new Properties();
        Properties property = new Properties();
        // 定义FileInputStream inputFile = null;
        FileInputStream inputFile = null;

        try {
            // 实例化inputFile
            inputFile = new FileInputStream(this.fileName);
            // 装载配置文件
            property.load(inputFile);
            for (String name : propertyList) {
                // 从配置文件中获取属性存入map中
                String data = property.getProperty(name);
                propertyMap.put(name, data);
            }

        } finally {
            // 关闭输入流
            if (inputFile != null) {
                inputFile.close();
            }
        }

        return propertyMap;
    }

}
