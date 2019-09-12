package com.dfire.platform.alchemy.service;

import org.springframework.stereotype.Service;

@Service
public class AlarmService {

    private final DingTalkService dingTalkService;

    public AlarmService(DingTalkService dingTalkService) {
        this.dingTalkService = dingTalkService;
    }

    public void alert(String businessName, String jobName){
        dingTalkService.sendMessage("flink任务失败", String.format("#### 状态：失败 \n #### 业务：%s \n #### 任务：%s", businessName,  jobName));
    }

    public void recover(String businessName, String jobName){
        dingTalkService.sendMessage("flink任务恢复", String.format("#### 状态：恢复 \n ### 业务：%s \n #### 任务：%s", businessName,  jobName));
    }

}
