package com.dfire.platform.alchemy.client.openshift;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OpenshiftWebUrlCache {

    private final static Map<Long, String> URLS = new ConcurrentHashMap<>();

    public static void put(Long id, String url) {
        URLS.put(id, url);
    }

    public static void delete(Long id) {
        URLS.remove(id);
    }

    public static String get(Long id) {
        return URLS.get(id);
    }


}
