package com.dfire.platform.alchemy.client.request;

import com.dfire.platform.alchemy.common.Resource;

/**
 * @author congbai
 * @date 2018/6/19
 */
public interface SubmitRequest extends Request {

    Resource getResource();

    boolean isYarn();
}
