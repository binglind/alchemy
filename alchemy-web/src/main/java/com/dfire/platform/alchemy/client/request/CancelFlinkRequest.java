package com.dfire.platform.alchemy.client.request;

/**
 * @author congbai
 * @date 2018/6/19
 */
public class CancelFlinkRequest implements FlinkRequest {

    private String jobID;

    private Boolean savePoint;

    private String savepointDirectory;

    public CancelFlinkRequest(String jobID) {
        this.jobID = jobID;
    }

    public CancelFlinkRequest(String jobID, Boolean savePoint, String savepointDirectory) {
        this.jobID = jobID;
        this.savePoint = savePoint;
        this.savepointDirectory = savepointDirectory;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public Boolean getSavePoint() {
        return savePoint;
    }

    public void setSavePoint(Boolean savePoint) {
        this.savePoint = savePoint;
    }

    public String getSavepointDirectory() {
        return savepointDirectory;
    }

    public void setSavepointDirectory(String savepointDirectory) {
        this.savepointDirectory = savepointDirectory;
    }

}
