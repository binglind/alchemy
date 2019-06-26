package com.dfire.platform.alchemy.client.loader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public interface JarLoader {

    URL findByName(String name) throws MalformedURLException, Exception;

    default File downLoad(String path) throws Exception{
        return downLoad(path, false);
    }

    File downLoad(String path, boolean cache) throws Exception;

    default URL find(String path) throws Exception {
        return find(path, false);
    }


    URL find(String path, boolean cache) throws Exception;

}
