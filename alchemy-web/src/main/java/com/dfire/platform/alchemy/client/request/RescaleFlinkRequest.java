package com.dfire.platform.alchemy.client.request;

/**
 * @author congbai
 * @date 2018/6/19
 */
public class RescaleFlinkRequest implements FlinkRequest {

    private String jobID;

    private int newParallelism;

    public RescaleFlinkRequest(String jobID, int newParallelism) {
        this.jobID = jobID;
        this.newParallelism = newParallelism;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public int getNewParallelism() {
        return newParallelism;
    }

    public void setNewParallelism(int newParallelism) {
        this.newParallelism = newParallelism;
    }
}
