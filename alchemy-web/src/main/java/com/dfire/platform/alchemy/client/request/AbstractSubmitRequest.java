package com.dfire.platform.alchemy.client.request;

import com.dfire.platform.alchemy.common.Resource;

/**
 * @author congbai
 * @date 01/06/2018
 */
public abstract class AbstractSubmitRequest implements FlinkRequest, SubmitRequest {

    private String jobName;

    private Resource resource;

    private boolean yarn;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public boolean isYarn() {
        return yarn;
    }

    public void setYarn(boolean yarn) {
        this.yarn = yarn;
    }

    public abstract void validate() throws Exception;

}
