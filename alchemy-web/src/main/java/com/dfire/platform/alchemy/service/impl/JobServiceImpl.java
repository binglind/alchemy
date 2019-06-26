package com.dfire.platform.alchemy.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dfire.platform.alchemy.client.FlinkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.dfire.platform.alchemy.client.ClientManager;
import com.dfire.platform.alchemy.client.request.CancelFlinkRequest;
import com.dfire.platform.alchemy.client.request.JarSubmitFlinkRequest;
import com.dfire.platform.alchemy.client.request.RescaleFlinkRequest;
import com.dfire.platform.alchemy.client.request.SavepointFlinkRequest;
import com.dfire.platform.alchemy.client.request.SqlSubmitFlinkRequest;
import com.dfire.platform.alchemy.client.request.SubmitRequest;
import com.dfire.platform.alchemy.client.response.Response;
import com.dfire.platform.alchemy.client.response.SubmitFlinkResponse;
import com.dfire.platform.alchemy.descriptor.SinkDescriptor;
import com.dfire.platform.alchemy.descriptor.SourceDescriptor;
import com.dfire.platform.alchemy.descriptor.UdfDescriptor;
import com.dfire.platform.alchemy.domain.Job;
import com.dfire.platform.alchemy.domain.JobSql;
import com.dfire.platform.alchemy.domain.Sink;
import com.dfire.platform.alchemy.domain.Source;
import com.dfire.platform.alchemy.domain.Udf;
import com.dfire.platform.alchemy.domain.enumeration.JobStatus;
import com.dfire.platform.alchemy.domain.enumeration.JobType;
import com.dfire.platform.alchemy.domain.enumeration.TableType;
import com.dfire.platform.alchemy.repository.JobRepository;
import com.dfire.platform.alchemy.repository.JobSqlRepository;
import com.dfire.platform.alchemy.repository.SinkRepository;
import com.dfire.platform.alchemy.repository.SourceRepository;
import com.dfire.platform.alchemy.repository.UdfRepository;
import com.dfire.platform.alchemy.service.JobService;
import com.dfire.platform.alchemy.service.dto.JobDTO;
import com.dfire.platform.alchemy.service.mapper.JobMapper;
import com.dfire.platform.alchemy.service.util.SqlParseUtil;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import com.google.common.collect.Lists;

/**
 * Service Implementation for managing {@link Job}.
 */
@Service
@Transactional
public class JobServiceImpl implements JobService {

    private final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    private final JobRepository jobRepository;

    private final JobSqlRepository jobSqlRepository;

    private final SourceRepository sourceRepository;

    private final UdfRepository udfRepository;

    private final SinkRepository sinkRepository;

    private final JobMapper jobMapper;

    private final ClientManager clientManager;

    public JobServiceImpl(JobRepository jobRepository, JobSqlRepository jobSqlRepository,
        SourceRepository sourceRepository, UdfRepository udfRepository, SinkRepository sinkRepository,
        JobMapper jobMapper, ClientManager clientManager) {
        this.jobRepository = jobRepository;
        this.jobSqlRepository = jobSqlRepository;
        this.sourceRepository = sourceRepository;
        this.udfRepository = udfRepository;
        this.sinkRepository = sinkRepository;
        this.jobMapper = jobMapper;
        this.clientManager = clientManager;
    }

    /**
     * Save a job.
     *
     * @param jobDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public JobDTO save(JobDTO jobDTO) {
        log.debug("Request to save Job : {}", jobDTO);
        Job job = jobMapper.toEntity(jobDTO);
        job = jobRepository.save(job);
        return jobMapper.toDto(job);
    }

    /**
     * Get all the jobs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<JobDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Jobs");
        return jobRepository.findAll(pageable).map(jobMapper::toDto);
    }

    /**
     * Get one job by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<JobDTO> findOne(Long id) {
        log.debug("Request to get Job : {}", id);
        return jobRepository.findById(id).map(jobMapper::toDto);
    }

    /**
     * Delete the job by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) throws Exception {
        log.debug("Request to delete Job : {}", id);
        Optional<Job> jobOptional = jobRepository.findById(id);
        Job job = jobOptional.get();
        if(JobStatus.RUNNING == job.getStatus()){
            cancel(id);
        }
        jobRepository.deleteById(id);
    }

    @Override
    public Response submit(Long id) throws Exception {
        Optional<Job> jobOptional = jobRepository.findById(id);
        Job job = jobOptional.get();
        if(JobStatus.RUNNING.equals(job.getStatus())){
            return new Response(false, "the job is running ");
        }
        if (job.getCluster() == null) {
            return new Response(false, "the job's cluster is null ");
        }
        final FlinkClient client = clientManager.getClient(job.getCluster().getId());
        SubmitRequest submitRequest;
        if (JobType.JAR == job.getType()) {
            submitRequest = createJarSubmitRequest(job);
        } else {
            List<JobSql> jobSqls = jobSqlRepository.findByJobId(id);
            if (CollectionUtils.isEmpty(jobSqls)) {
                return new Response(false, "the job's sql is null");
            }
            submitRequest = createSqlSubmitRequest(job, jobSqls);
        }
        SubmitFlinkResponse response =client.submit(submitRequest);
        if (response.isSuccess()) {
            job.setStatus(JobStatus.SUBMIT);
            job.setClusterJobId(response.getJobId());
        } else {
            job.setStatus(JobStatus.FAILED);
        }
        jobRepository.save(job);
        return response;
    }

    private SubmitRequest createSqlSubmitRequest(Job job, List<JobSql> jobSqls) throws Exception {
        List<String> sqlList
            = jobSqls.stream().map(jobSql -> jobSql.getSql()).collect(Collectors.toCollection(ArrayList::new));
        SqlSubmitFlinkRequest sqlSubmitFlinkRequest
            = BindPropertiesUtil.bindProperties(job.getConfig(), SqlSubmitFlinkRequest.class);;
        List<String> sourceNames = Lists.newArrayList();
        List<String> tableNames = Lists.newArrayList();
        List<String> udfNames = Lists.newArrayList();
        List<String> sinkNames = Lists.newArrayList();
        SqlParseUtil.parse(sqlList, sourceNames, udfNames, sinkNames);
        parseView(job, tableNames, sourceNames, udfNames, sinkNames);
        /**
         *  先注册视图中的表
         */
        Collections.reverse(sourceNames);
        List<SourceDescriptor> sourceDescriptors = findSources(job, sourceNames);
        List<UdfDescriptor> udfDescriptors = findUdfs(job, udfNames);
        List<SinkDescriptor> sinkDescriptors = findSinks(job, sinkNames);
        sqlSubmitFlinkRequest.setJobName(job.getName());
        sqlSubmitFlinkRequest.setSources(sourceDescriptors);
        sqlSubmitFlinkRequest.setUdfs(udfDescriptors);
        sqlSubmitFlinkRequest.setSinks(sinkDescriptors);
        sqlSubmitFlinkRequest.setSqls(SqlParseUtil.findQuerySql(sqlList));
        return sqlSubmitFlinkRequest;
    }

    private void parseView(Job job, List<String> tableNames, List<String> sourceNames, List<String> udfNames,
        List<String> sinkNames) throws Exception {
        List<String> sources = Lists.newArrayList(sourceNames);
        for (String name : sources) {
            if (tableNames.contains(name)) {
                continue;
            }
            Source source = findSource(job, name);
            if (TableType.VIEW != source.getTableType()) {
                continue;
            } else {
                tableNames.add(name);
            }
            String sql = source.getConfig();
            SqlParseUtil.parse(Lists.newArrayList(sql), sourceNames, udfNames, sinkNames);
            parseView(job, tableNames, sourceNames, udfNames, sinkNames);
        }
    }

    private List<SinkDescriptor> findSinks(Job job, List<String> sinkNames) throws Exception {
        List<SinkDescriptor> sinkDescriptors = new ArrayList<>(sinkNames.size());
        for (String name : sinkNames) {
            Optional<Sink> sinkOptional = sinkRepository.findOneByBusinessIdAndName(job.getBusiness().getId(), name);
            if (!sinkOptional.isPresent()) {
                throw new IllegalArgumentException("table sink：" + name + "doesn't exist");
            }
            sinkDescriptors.add(SinkDescriptor.from(sinkOptional.get()));
        }
        return sinkDescriptors;
    }

    private List<UdfDescriptor> findUdfs(Job job, List<String> udfNames) {
        List<UdfDescriptor> udfDescriptors = new ArrayList<>(udfNames.size());
        for (String name : udfNames) {
            Optional<Udf> udfOptional = udfRepository.findOneByBusinessIdAndName(job.getBusiness().getId(), name);
            if (udfOptional.isPresent()) {
                udfDescriptors.add(UdfDescriptor.from(udfOptional.get()));
            }else{
                //flink自定义的函数也属于table udf
                log.warn("table udf：{} doesn't exist in alchemy", name);
            }

        }
        return udfDescriptors;
    }

    private List<SourceDescriptor> findSources(Job job, List<String> sourceNames) throws Exception {
        List<SourceDescriptor> sourceDescriptors = new ArrayList<>(sourceNames.size());
        for (String name : sourceNames) {
            Source source = findSource(job, name);
            sourceDescriptors.add(SourceDescriptor.from(source));
        }
        return sourceDescriptors;
    }

    private Source findSource(Job job, String name) throws Exception {
        Optional<Source> sourceOptional = sourceRepository.findOneByBusinessIdAndName(job.getBusiness().getId(), name);
        if (!sourceOptional.isPresent()) {
            throw new IllegalArgumentException("table source：" + name + " doesn't exist");
        }
        return sourceOptional.get();
    }

    private SubmitRequest createJarSubmitRequest(Job job) throws Exception {
        JarSubmitFlinkRequest jarSubmitFlinkRequest
            = BindPropertiesUtil.bindProperties(job.getConfig(), JarSubmitFlinkRequest.class);
        jarSubmitFlinkRequest.setJobName(job.getName());
        return jarSubmitFlinkRequest;
    }

    @Override
    public Response cancel(Long id) throws Exception {
        return cancelWithSavepoint(id, false, null);
    }

    @Override
    public Response cancelWithSavepoint(Long id, String savepointDirectory) throws Exception {
       return cancelWithSavepoint(id, true, savepointDirectory);
    }

    private Response cancelWithSavepoint(Long id, boolean savepoint,  String savepointDirectory) throws Exception {
        Optional<Job> jobOptional = jobRepository.findById(id);
        Job job = jobOptional.get();
        if (job.getCluster() == null) {
            return new Response(false, "the job's cluster is null ");
        }
        final FlinkClient client = clientManager.getClient(job.getCluster().getId());
        CancelFlinkRequest cancelFlinkRequest
            = new CancelFlinkRequest(job.getClusterJobId(), savepoint, savepointDirectory);
        Response response = client.cancel(cancelFlinkRequest);
        if(response.isSuccess()){
            job.setStatus(JobStatus.CANCELED);
            jobRepository.save(job);
        }
        return response;
    }

    @Override
    public Response rescale(Long id, int newParallelism) throws Exception {
        Optional<Job> jobOptional = jobRepository.findById(id);
        Job job = jobOptional.get();
        if (job.getCluster() == null) {
            return new Response(false, "the job's cluster is null ");
        }
        final FlinkClient client = clientManager.getClient(job.getCluster().getId());
        RescaleFlinkRequest flinkRequest
            = new RescaleFlinkRequest(job.getClusterJobId(), newParallelism);
        return client.rescale(flinkRequest);
    }

    @Override
    public Response triggerSavepoint(Long id, String savepointDirectory) throws Exception {
        Optional<Job> jobOptional = jobRepository.findById(id);
        Job job = jobOptional.get();
        if (job.getCluster() == null) {
            return new Response(false, "the job's cluster is null ");
        }
        final FlinkClient client = clientManager.getClient(job.getCluster().getId());
        SavepointFlinkRequest flinkRequest
            = new SavepointFlinkRequest(job.getClusterJobId(), savepointDirectory);
        return client.savepoint(flinkRequest);
    }
}
