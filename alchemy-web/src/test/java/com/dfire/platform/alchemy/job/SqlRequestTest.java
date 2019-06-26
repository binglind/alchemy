package com.dfire.platform.alchemy.job;

import com.dfire.platform.alchemy.client.request.SqlSubmitFlinkRequest;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class SqlRequestTest {

    @Test
    public void bind() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/sql.yaml");
        SqlSubmitFlinkRequest flinkRequest = BindPropertiesUtil.bindProperties(file, SqlSubmitFlinkRequest.class);
        assertThat(flinkRequest.getParallelism()).isEqualTo(2);
        assertThat(flinkRequest.getRestartStrategies()).isNotNull();
        assertThat(flinkRequest.getTimeCharacteristic()).isNotNull();
        assertThat(flinkRequest.getRestartParams().get("restartAttempts")).isEqualTo(10);
        assertThat(flinkRequest.getRestartParams().get("delayBetweenAttempts")).isEqualTo(10000);
        assertThat(flinkRequest.getDependencies().size()).isEqualTo(1);
    }
}
