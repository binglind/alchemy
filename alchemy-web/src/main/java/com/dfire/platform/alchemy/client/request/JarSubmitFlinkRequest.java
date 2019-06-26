package com.dfire.platform.alchemy.client.request;

import org.springframework.util.Assert;

/**
 * @author congbai
 * @date 04/06/2018
 */
public class JarSubmitFlinkRequest extends AbstractSubmitRequest {

    /**
     *  是否需要cache 下载好的jar包
     */
    private boolean cache;

    private String dependency;

    private Integer parallelism;

    private String programArgs;

    private String entryClass;

    private String savepointPath;

    private Boolean allowNonRestoredState;

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public String getDependency() {
        return dependency;
    }

    public void setDependency(String dependency) {
        this.dependency = dependency;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public String getProgramArgs() {
        return programArgs;
    }

    public void setProgramArgs(String programArgs) {
        this.programArgs = programArgs;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public void setEntryClass(String entryClass) {
        this.entryClass = entryClass;
    }

    public String getSavepointPath() {
        return savepointPath;
    }

    public void setSavepointPath(String savepointPath) {
        this.savepointPath = savepointPath;
    }

    public Boolean getAllowNonRestoredState() {
        return allowNonRestoredState;
    }

    public void setAllowNonRestoredState(Boolean allowNonRestoredState) {
        this.allowNonRestoredState = allowNonRestoredState;
    }

    @Override
    public void validate() throws Exception {
        Assert.notNull(dependency, "依赖包不能为空");
        Assert.notNull(parallelism, "并发数不能为空");
        Assert.notNull(entryClass, "main函数不能为空");
    }
}
