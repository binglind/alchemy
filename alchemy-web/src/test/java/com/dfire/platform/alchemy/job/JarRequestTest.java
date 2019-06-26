package com.dfire.platform.alchemy.job;

import com.dfire.platform.alchemy.client.request.JarSubmitFlinkRequest;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class JarRequestTest{

    @Test
    public void bind() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/jar.yaml");
        JarSubmitFlinkRequest flinkRequest = BindPropertiesUtil.bindProperties(file, JarSubmitFlinkRequest.class);
        assertThat(flinkRequest.isCache()).isTrue();
        assertThat(flinkRequest.getDependency()).isNotNull();
        assertThat(flinkRequest.getEntryClass()).isNotNull();
        assertThat(flinkRequest.getAllowNonRestoredState()).isNotNull();
        assertThat(flinkRequest.getParallelism()).isEqualTo(2);
        assertThat(flinkRequest.getProgramArgs()).isNotNull();
        assertThat(flinkRequest.getSavepointPath()).isNotNull();
    }


}
