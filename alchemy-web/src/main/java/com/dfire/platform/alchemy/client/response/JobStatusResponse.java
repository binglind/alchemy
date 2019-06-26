package com.dfire.platform.alchemy.client.response;

import com.dfire.platform.alchemy.domain.enumeration.JobStatus;

/**
 * @author congbai
 * @date 2018/6/20
 */
public class JobStatusResponse extends StatusJobResponse {

    private JobStatus status;

    public JobStatusResponse(boolean success, JobStatus status) {
        super(success);
        this.status = status;
    }

    public JobStatusResponse(String message) {
        super(message);
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}
