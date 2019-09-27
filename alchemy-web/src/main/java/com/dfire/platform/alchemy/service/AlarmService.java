package com.dfire.platform.alchemy.service;

import io.github.jhipster.config.JHipsterConstants;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class AlarmService {

    private static final String DATE_FORMMAT = "yyyy-MM-dd HH:mm:ss";

    private static final ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        DateFormat df = new SimpleDateFormat(DATE_FORMMAT);
        df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return df;
    });

    private final DingTalkService dingTalkService;

    private final boolean prod;

    public AlarmService(Environment env,  DingTalkService dingTalkService) {
        this.dingTalkService = dingTalkService;
        if (env != null && env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_PRODUCTION))) {
            prod = true;
        } else {
            prod = false;
        }
    }

    public void alert(String businessName, String jobName) {
        if(!prod){
            return;
        }
        dingTalkService.sendMessage("flink任务失败", String.format("#### 状态：失败 \n #### 业务：%s \n #### 任务：%s \n #### 时间：%s", businessName, jobName, DATE_FORMAT_THREAD_LOCAL.get().format(new Date())));
    }

    public void recover(String businessName, String jobName) {
        if(!prod){
            return;
        }
        dingTalkService.sendMessage("flink任务恢复", String.format("#### 状态：恢复 \n #### 业务：%s \n #### 任务：%s \n #### 时间：%s", businessName, jobName, DATE_FORMAT_THREAD_LOCAL.get().format(new Date())));
    }

}
