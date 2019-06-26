package com.dfire.platform.alchemy.util;

public class ThreadLocalClassLoader {

    private static final ThreadLocal<ClassLoader> LOADER = new ThreadLocal<ClassLoader>(){
        @Override
        protected ClassLoader initialValue() {
            return Thread.currentThread().getContextClassLoader();
        }
    };

    public static void set(ClassLoader classLoader){
        LOADER.set(classLoader);
    }

    public static ClassLoader get(){
        return LOADER.get();
    }

    public static void clear(){
        LOADER.remove();
    }

}
