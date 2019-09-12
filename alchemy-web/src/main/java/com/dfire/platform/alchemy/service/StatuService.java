package com.dfire.platform.alchemy.service;

import com.dfire.platform.alchemy.client.ClientManager;
import com.dfire.platform.alchemy.client.FlinkClient;
import com.dfire.platform.alchemy.client.request.JobStatusRequest;
import com.dfire.platform.alchemy.client.response.JobStatusResponse;
import com.dfire.platform.alchemy.domain.Business;
import com.dfire.platform.alchemy.domain.Job;
import com.dfire.platform.alchemy.domain.enumeration.JobStatus;
import com.dfire.platform.alchemy.repository.BusinessRepository;
import com.dfire.platform.alchemy.repository.JobRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.hadoop.shaded.com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class StatuService implements InitializingBean {

    private final Logger log = LoggerFactory.getLogger(StatuService.class);

    private final JobRepository jobRepository;

    private final BusinessRepository businessRepository;

    private final ClientManager clientManager;

    private final AlarmService alarmService;

    private final ScheduledExecutorService executorService ;

    public StatuService(JobRepository jobRepository, BusinessRepository businessRepository, ClientManager clientManager, AlarmService alarmService) {
        this.jobRepository = jobRepository;
        this.businessRepository = businessRepository;
        this.clientManager = clientManager;
        this.alarmService = alarmService;
        this.executorService = new ScheduledThreadPoolExecutor(4, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("job-status-%s").build());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.executorService.scheduleAtFixedRate(new Executor(), 10, 60, TimeUnit.SECONDS);
    }

    public class Executor implements Runnable{

        @Override
        public void run() {
            List<Business> businesses = businessRepository.findAll();
            if(CollectionUtils.isEmpty(businesses)){
                return;
            }
            try {
                for(Business business : businesses){
                    List<Job> jobs = jobRepository.findAllByBusinessId(business.getId());
                    if(CollectionUtils.isEmpty(jobs)){
                        continue;
                    }
                    for(Job job : jobs){
                        if(JobStatus.SUBMIT != job.getStatus() && JobStatus.RUNNING != job.getStatus() && JobStatus.FAILED != job.getStatus()){
                            continue;
                        }
                        try {
                            FlinkClient client = clientManager.getClient(job.getCluster().getId());
                            JobStatusResponse jobStatusResponse = client.status(new JobStatusRequest(job.getClusterJobId()));
                            if (jobStatusResponse.isSuccess()) {
                                JobStatus jobStatus = jobStatusResponse.getStatus();
                                if (jobStatus != job.getStatus()) {
                                    if (job.getStatus() != JobStatus.FAILED && JobStatus.FAILED == jobStatus) {
                                        alarmService.alert(business.getName(), job.getName());
                                    }else if(job.getStatus() == JobStatus.FAILED && jobStatus== JobStatus.RUNNING){
                                        alarmService.recover(business.getName(), job.getName());
                                    }
                                    job.setStatus(jobStatus);
                                    jobRepository.save(job);
                                }
                            } else {
                                log.warn("request status failed, msg:{}", jobStatusResponse.getMessage());
                            }
                        }catch (Exception e){
                            log.warn("request status faild,msg:{}", e.getMessage());
                        }
                    }
                }
            }catch (Exception e){
                log.warn("update status faild,msg:{}", e.getMessage());
            }
        }
    }
}
