package com.dfire.platform.alchemy.util;


import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author congbai
 * @date 2018/6/30
 */
public class BindPropertiesToTargetTest {

    @Test
    public void bind() throws Exception {
        File file = ResourceUtils.getFile("classpath:yaml/bind.yaml");
        BindPoJo bindPoJo = BindPropertiesUtil.bindProperties(file, BindPoJo.class);
        assertThat(bindPoJo.getName()).isEqualTo("bindTest");
        assertThat(bindPoJo.getCamelCase()).isEqualTo("camelCase");
        assertThat(bindPoJo.getInvalidCamelCase()).isEqualTo("invalidCamelCase");
        assertThat(bindPoJo.getMap()).isNotNull();
        assertThat(bindPoJo.getMap().get("age")).isEqualTo(20);
        assertThat(bindPoJo.getMap().get("height")).isEqualTo(172);
        assertThat(bindPoJo.getMap().get("high-availability.cluster-id")).isEqualTo("daily");
        assertThat(bindPoJo.getList().get(0)).isEqualTo("one");
        assertThat(bindPoJo.getList().get(1)).isEqualTo("two");
        assertThat(bindPoJo.getArray()[0]).isEqualTo("one");
        assertThat(bindPoJo.getArray()[1]).isEqualTo("two");
        assertThat(bindPoJo.getCheckpointConfig().getCheckpointInterval()).isEqualTo(10000);
        assertThat(bindPoJo.getCheckpointConfig().getCheckpointTimeout()).isEqualTo(60000);
    }



    public static class  BindPoJo{

        private String name;

        private String camelCase;

        private String invalidCamelCase;

        private Map<String, Object> map;

        private List<String> list;

        private String[] array;

        private CheckpointConfig checkpointConfig ;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCamelCase() {
            return camelCase;
        }

        public void setCamelCase(String camelCase) {
            this.camelCase = camelCase;
        }

        public String getInvalidCamelCase() {
            return invalidCamelCase;
        }

        public void setInvalidCamelCase(String invalidCamelCase) {
            this.invalidCamelCase = invalidCamelCase;
        }

        public Map<String, Object> getMap() {
            return map;
        }

        public void setMap(Map<String, Object> map) {
            this.map = map;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public String[] getArray() {
            return array;
        }

        public void setArray(String[] array) {
            this.array = array;
        }

        public CheckpointConfig getCheckpointConfig() {
            return checkpointConfig;
        }

        public void setCheckpointConfig(CheckpointConfig checkpointConfig) {
            this.checkpointConfig = checkpointConfig;
        }
    }
}
