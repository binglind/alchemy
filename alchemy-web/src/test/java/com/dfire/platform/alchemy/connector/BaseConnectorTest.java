package com.dfire.platform.alchemy.connector;

import com.dfire.platform.alchemy.client.ClusterClientFactory;
import com.dfire.platform.alchemy.client.FlinkClient;
import com.dfire.platform.alchemy.client.StandaloneClusterInfo;
import com.dfire.platform.alchemy.client.loader.JarLoader;
import com.dfire.platform.alchemy.client.loader.UrlJarLoader;
import com.dfire.platform.alchemy.descriptor.SinkDescriptor;
import com.dfire.platform.alchemy.descriptor.SourceDescriptor;
import com.dfire.platform.alchemy.domain.Sink;
import com.dfire.platform.alchemy.domain.Source;
import com.dfire.platform.alchemy.domain.enumeration.SinkType;
import com.dfire.platform.alchemy.domain.enumeration.SourceType;
import com.dfire.platform.alchemy.domain.enumeration.TableType;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

import java.io.*;

public class BaseConnectorTest {

    protected FlinkClient client;

    @Before
    public void before() throws Exception {
        UrlJarLoader jarLoader = new UrlJarLoader(null);
        File file = ResourceUtils.getFile("classpath:yaml/cluster.yaml");
        StandaloneClusterInfo clusterInfo = BindPropertiesUtil.bindProperties(file, StandaloneClusterInfo.class);
        client = ClusterClientFactory.createRestClient(clusterInfo, jarLoader);
    }

    protected SourceDescriptor createSource(String name, String yaml, SourceType sourceType, TableType tableType) throws Exception {
        Source source = new Source();
        source.setName(name);
        source.setConfig(getYamlString(yaml));
        source.setSourceType(sourceType);
        source.setTableType(tableType);
        return SourceDescriptor.from(source);
    }

    protected SinkDescriptor createSink(String name, String yaml, SinkType sinkType) throws Exception {
        Sink sink = new Sink();
        sink.setConfig(getYamlString(yaml));
        sink.setType(sinkType);
        sink.setName(name);
        return SinkDescriptor.from(sink);
    }


    protected String getYamlString(String filePath) throws Exception {
        File file = ResourceUtils.getFile(filePath);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

    }
}
