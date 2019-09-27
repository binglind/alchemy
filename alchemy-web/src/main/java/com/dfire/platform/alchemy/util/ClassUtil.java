package com.dfire.platform.alchemy.util;

public class ClassUtil {

    public static Class forName(String name) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return Class.forName(name, false, classLoader);
    }

}
