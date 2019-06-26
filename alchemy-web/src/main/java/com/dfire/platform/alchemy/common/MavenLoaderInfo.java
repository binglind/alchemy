package com.dfire.platform.alchemy.common;

import java.io.File;
import java.net.URLClassLoader;

public class MavenLoaderInfo {
    private URLClassLoader urlClassLoader;
    private File jarFile;

    public MavenLoaderInfo(URLClassLoader urlClassLoader, File targetFile) {
        this.urlClassLoader = urlClassLoader;
        this.jarFile = targetFile;
    }

    public URLClassLoader getUrlClassLoader() {
        return this.urlClassLoader;
    }

    public void setUrlClassLoader(URLClassLoader urlClassLoader) {
        this.urlClassLoader = urlClassLoader;
    }

    public File getJarFile() {
        return this.jarFile;
    }

    public void setJarFile(File jarFile) {
        this.jarFile = jarFile;
    }
}
