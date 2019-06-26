package com.dfire.platform.alchemy.util;

public class ClassUtil {

    public static Class forName(String name) throws ClassNotFoundException {
        ClassLoader classLoader = ThreadLocalClassLoader.get();
        return Class.forName(name, false, classLoader);
    }

}
