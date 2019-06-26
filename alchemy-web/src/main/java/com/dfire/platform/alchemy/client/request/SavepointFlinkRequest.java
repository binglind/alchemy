package com.dfire.platform.alchemy.client.request;

/**
 * @author congbai
 * @date 2018/6/19
 */
public class SavepointFlinkRequest implements FlinkRequest {

    private String jobID;

    private String savepointDirectory;

    public SavepointFlinkRequest(String jobID, String savepointDirectory) {
        this.jobID = jobID;
        this.savepointDirectory = savepointDirectory;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getSavepointDirectory() {
        return savepointDirectory;
    }

    public void setSavepointDirectory(String savepointDirectory) {
        this.savepointDirectory = savepointDirectory;
    }

}
