package com.dfire.platform.alchemy.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class FailedSubmitJobException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public FailedSubmitJobException(String defaultMessage) {
        this(ErrorConstants.DEFAULT_TYPE, defaultMessage);
    }

    public FailedSubmitJobException(URI type, String defaultMessage) {
        super(type, defaultMessage, Status.INTERNAL_SERVER_ERROR, null, null, null, null);

    }
}
