package com.dfire.platform.alchemy.service;

import org.springframework.stereotype.Service;

@Service
public class AlarmService {

    private final DingTalkService dingTalkService;

    public AlarmService(DingTalkService dingTalkService) {
        this.dingTalkService = dingTalkService;
    }

    public void alert(String businessName, String jobName){
        dingTalkService.sendMessage("####任务失败", String.format("####业务：%s ##### 任务：%s", businessName,  jobName));
    }

    public void recover(String businessName, String jobName){
        dingTalkService.sendMessage("####任务恢复", String.format("####业务：%s ##### 任务：%s", businessName,  jobName));
    }

}
