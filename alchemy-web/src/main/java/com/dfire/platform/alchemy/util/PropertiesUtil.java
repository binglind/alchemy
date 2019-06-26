package com.dfire.platform.alchemy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.dfire.platform.alchemy.common.Pair;

public class PropertiesUtil {

    private PropertiesUtil() {}

    public static Properties create(File file) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        return properties;
    }

    public static Properties getProperties(Map<String, String> prop) {
        Properties p = new Properties();
        for (Map.Entry<String, String> e : prop.entrySet()) {
            p.put(e.getKey(), e.getValue());
        }
        return p;
    }

    public static Properties createProperties(Map<String, Object> prop) {
        Properties p = new Properties();
        for (Map.Entry<String, Object> e : prop.entrySet()) {
            p.put(e.getKey(), e.getValue());
        }
        return p;
    }

    public static Properties fromYamlMap(Map<String, Object> properties) {
        return createProperties(properties);
    }

    public static Properties getProperties(List<Pair<String, String>> prop) {
        Properties p = new Properties();
        for (Pair<String, String> e : prop) {
            p.put(e.getKey(), e.getValue());
        }
        return p;
    }

    public static Integer getProperty(Integer value, Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public static Long getProperty(Long value, Long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}
