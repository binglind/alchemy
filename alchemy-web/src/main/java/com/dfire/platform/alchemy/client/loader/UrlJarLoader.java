package com.dfire.platform.alchemy.client.loader;

import com.dfire.platform.alchemy.common.Constants;
import com.dfire.platform.alchemy.common.MavenLoaderInfo;
import com.dfire.platform.alchemy.util.MavenJarUtil;
import io.github.jhipster.config.JHipsterConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UrlJarLoader implements JarLoader {

    private static final String USER_DIR = System.getProperty("user.dir");

    private final Map<String, File> files = new ConcurrentHashMap<>();

    private final Properties properties;

    @Value("${alchemy.repository.release}")
    private String releaseRepositoryUrl;

    @Value("${alchemy.repository.snapshot}")
    private String snapRepositoryUrl;

    public UrlJarLoader(Environment env) {
        if (env == null) {
            //just for test
            this.properties = createProperties("META-INF/alchemy-dev.properties");
        } else {
            if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))) {
                this.properties = createProperties("META-INF/alchemy-dev.properties");
            } else {
                this.properties = createProperties("META-INF/alchemy.properties");
            }
        }
    }

    private Properties createProperties(String path) {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed load alchemy properties");
        }
        return properties;
    }


    @Override
    public URL findByName(String name) throws Exception {
        String path = properties.getProperty(name);
        return find(path, true);
    }

    @Override
    public File downLoad(String path, boolean cache) throws Exception {
        if (path == null) {
            return null;
        }
        path = path.trim();
        if (cache) {
            File file = files.get(path);
            if (file == null) {
                file = toFile(path);
                files.put(path, file);
            }
            return file;
        }
        return toFile(path);
    }

    private File toFile(String path) throws FileNotFoundException {
        File file;
        if (path.startsWith("/")) {
            file = createProgramFile(path);
        } else if (path.startsWith("classpath:") || path.startsWith("file:")) {
            file = ResourceUtils.getFile(path);
        } else if (path.startsWith("hdfs:")) {
            throw new UnsupportedOperationException();
        } else if (path.startsWith("http:")) {
            throw new UnsupportedOperationException();
        } else if (path.startsWith("https:")) {
            throw new UnsupportedOperationException();
        } else {
            //default download from nexus
            MavenLoaderInfo mavenLoaderInfo = MavenJarUtil.forAvg(releaseRepositoryUrl, snapRepositoryUrl, path);
            file = mavenLoaderInfo.getJarFile();
        }
        return file;
    }

    private File createProgramFile(String path) {
        File programFile = new File(USER_DIR);
        if(USER_DIR.endsWith(Constants.WEB_NAME)){
            return new File(programFile.getParent(), path);
        }else{
            return new File(programFile, path);
        }
    }

    @Override
    public URL find(String pathString, boolean cache) throws Exception {
        if (pathString == null) {
            return null;
        }
        if (isLocal(pathString) || isAvg(pathString)) {
            File file = downLoad(pathString, cache);
            if (file == null) {
                return null;
            }
            return file.toURI().toURL();
        } else {
            return new URL(pathString);
        }

    }

    private boolean isLocal(String pathString) {
        return pathString.startsWith("/");
    }

    private boolean isAvg(String pathString) {
        String path = pathString.trim();
        return !(path.startsWith("/")
            || path.startsWith("classpath:")
            || path.startsWith("http:")
            || path.startsWith("https:")
            || path.startsWith("file:")
            || path.startsWith("hdfs:"));
    }

    public void setReleaseRepositoryUrl(String releaseRepositoryUrl) {
        this.releaseRepositoryUrl = releaseRepositoryUrl;
    }

    public void setSnapRepositoryUrl(String snapRepositoryUrl) {
        this.snapRepositoryUrl = snapRepositoryUrl;
    }
}


