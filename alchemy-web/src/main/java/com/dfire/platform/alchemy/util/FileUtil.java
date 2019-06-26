package com.dfire.platform.alchemy.util;

import org.apache.flink.client.program.JobWithJars;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author congbai
 * @date 2018/7/1
 */
public class FileUtil {

    public static List<URL> createPath(File file) {
        List<URL> jarFiles = new ArrayList<>(1);
        if (file == null) {
            return jarFiles;
        }
        try {

            URL jarFileUrl = file.getAbsoluteFile().toURI().toURL();
            jarFiles.add(jarFileUrl);
            JobWithJars.checkJarFile(jarFileUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("JAR file is invalid '" + file.getAbsolutePath() + "'", e);
        } catch (IOException e) {
            throw new RuntimeException("Problem with jar file " + file.getAbsolutePath(), e);
        }
        return jarFiles;
    }
}
