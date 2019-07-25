package com.dfire.platform.alchemy.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dfire.platform.alchemy.client.OpenshiftClusterInfo;
import com.dfire.platform.alchemy.client.openshift.OpenshiftWebUrlCache;
import com.dfire.platform.alchemy.config.OpenshiftProperties;
import com.dfire.platform.alchemy.domain.enumeration.ClusterType;
import com.dfire.platform.alchemy.service.dto.ClusterDTO;
import com.dfire.platform.alchemy.util.BindPropertiesUtil;
import com.dfire.platform.alchemy.util.JsonUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OpenshiftService {

    private final Logger log = LoggerFactory.getLogger(OpenshiftService.class);

    public static final String SELECTOR_APP = "flink";

    public static final String FLINK_ADD_CONFIG = "FLINK_ADD_CONFIG";

    public static final String HADOOP_USER_NAME = "HADOOP_USER_NAME";

    public static final String JOB_MANAGER_RPC_ADDRESS = "JOB_MANAGER_RPC_ADDRESS";

    public static final String JOB_MANAGER_NAME_PREFIX = "jobmanager-";

    public static final String TASK_MANAGER_NAME_PREFIX = "taskmanager-";

    public static final String DEPLOYMENTS_CREATE_URL = "%s/apis/apps/v1/namespaces/%s/deployments";

    public static final String DEPLOYMENTS_SPECIFY_URL = "%s/apis/apps/v1/namespaces/%s/deployments/%s";

    public static final String SERVICE_CREATE_URL = "%s/api/v1/namespaces/%s/services";

    public static final String SERVICE_SPECIFY_URL = "%s/api/v1/namespaces/%s/services/%s";

    public static final String ROUTER_CREATE_URL = "%s/apis/route.openshift.io/v1/namespaces/%s/routes";

    public static final String ROUTER_SPECIFY_URL = "%s/apis/route.openshift.io/v1/namespaces/%s/routes/%s";

    private final RestTemplate restTemplate;

    private final OpenshiftProperties openshiftProperties;

    private final String jobManager;

    private final String taskManager;

    private final String service;

    private final String router;

    private final ScheduledExecutorService tokenService;

    private volatile String token;

    public OpenshiftService(RestTemplate restTemplate, OpenshiftProperties openshiftProperties) throws IOException {
        this.restTemplate = restTemplate;
        this.openshiftProperties = openshiftProperties;
        this.jobManager = loadTemplate("node.json");
        this.taskManager = loadTemplate("node.json");
        this.service = loadTemplate("service.json");
        this.router = loadTemplate("router.json");
        this.tokenService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("token-service-%d").build());
        this.tokenService.scheduleAtFixedRate(new TokenTask(), 0, 86300, TimeUnit.SECONDS);
    }

    private String loadTemplate(String fileName) throws IOException {
        ByteArrayOutputStream out = null;
        InputStream inputStream = null;
        try {
            ClassPathResource resource = new ClassPathResource("templates/openshift/" + fileName);
            out = new ByteArrayOutputStream();
            inputStream = resource.getInputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toString();
        } catch (IOException e) {
            throw e;
        } finally {
            if (out != null) {
                out.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 创建jobmanager、taskmanager、service、router
     * @param openshiftClusterInfo
     * @return 返回router的host，作为jobmanager的weburl
     */
    public void create(OpenshiftClusterInfo openshiftClusterInfo) {
        HttpHeaders headers = createHeader(getToken());
        String deploymentsUrl = getDeploymentsCreateUrl();
        restTemplate.postForEntity(deploymentsUrl, new HttpEntity<>(createDeployments(openshiftClusterInfo, true), headers), JSONObject.class);
        restTemplate.postForEntity(deploymentsUrl, new HttpEntity<>(createDeployments(openshiftClusterInfo, false), headers), JSONObject.class);
        restTemplate.postForEntity(getServiceCreateUrl(), new HttpEntity<>(createService(openshiftClusterInfo), headers), JSONObject.class);
        restTemplate.postForEntity(getRouterCreateUrl(), new HttpEntity<>(createRouter(openshiftClusterInfo), headers), JSONObject.class);
    }

    /**
     * 只更新jobmanager和taskmanager的deployments
     *
     * @param openshiftClusterInfo
     */
    public void update(OpenshiftClusterInfo openshiftClusterInfo) {
        HttpHeaders headers = createHeader(getToken());
        restTemplate.put(getDeploymentsSpecifyUrl(openshiftClusterInfo, true), new HttpEntity<>(createDeployments(openshiftClusterInfo, true), headers));
        restTemplate.put(getDeploymentsSpecifyUrl(openshiftClusterInfo, false), new HttpEntity<>(createDeployments(openshiftClusterInfo, false), headers));
    }

    public void delete(OpenshiftClusterInfo openshiftClusterInfo) {
        HttpHeaders headers = createHeader(getToken());
        restTemplate.exchange(getDeploymentsSpecifyUrl(openshiftClusterInfo, true), HttpMethod.DELETE, new HttpEntity<>(null, headers), String.class);
        restTemplate.exchange(getDeploymentsSpecifyUrl(openshiftClusterInfo, false), HttpMethod.DELETE, new HttpEntity<>(null, headers), String.class);
        restTemplate.exchange(getServiceSpecifyUrl(openshiftClusterInfo), HttpMethod.DELETE, new HttpEntity<>(null, headers), String.class);
        restTemplate.exchange(getRouterSpecifyUrl(openshiftClusterInfo), HttpMethod.DELETE, new HttpEntity<>(null, headers), String.class);
    }

    public String queryWebUrl(ClusterDTO clusterDTO) throws Exception {
        OpenshiftClusterInfo openshiftClusterInfo = BindPropertiesUtil.bindProperties(clusterDTO.getConfig(), OpenshiftClusterInfo.class);
        HttpHeaders headers = createHeader(getToken());
        ResponseEntity<JSONObject> routerEntity = restTemplate.exchange(getRouterSpecifyUrl(openshiftClusterInfo), HttpMethod.GET, new HttpEntity<>(null, headers), JSONObject.class);
        if (routerEntity.getStatusCode() == HttpStatus.OK) {
            return "http://" + routerEntity.getBody().getJSONObject("spec").getString("host");
        }
        return null;
    }

    private HttpHeaders createHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        return headers;
    }

    private String createRouter(OpenshiftClusterInfo openshiftClusterInfo) {
        JSONObject jsonObject = JSON.parseObject(router);
        jsonObject.getJSONObject("metadata").put("name", openshiftClusterInfo.getJobManagerAddress());
        jsonObject.getJSONObject("metadata").put("namespace", openshiftProperties.getNamespace());
        jsonObject.getJSONObject("spec").getJSONObject("to").put("name", openshiftClusterInfo.getJobManagerAddress());
        return JsonUtil.toJson(jsonObject);
    }

    private String createService(OpenshiftClusterInfo openshiftClusterInfo) {
        JSONObject jsonObject = JSON.parseObject(service);
        jsonObject.getJSONObject("metadata").put("name", openshiftClusterInfo.getJobManagerAddress());
        jsonObject.getJSONObject("metadata").put("namespace", openshiftProperties.getNamespace());
        jsonObject.getJSONObject("spec").put("selector", new OpenshiftClusterInfo.Label(SELECTOR_APP, createJobManagerName(openshiftClusterInfo.getName())));
        return JsonUtil.toJson(jsonObject);
    }

    private String createDeployments(OpenshiftClusterInfo openshiftClusterInfo, boolean isJobManager) {
        JSONObject jsonObject;
        String name;
        OpenshiftClusterInfo.Resources resources;
        int replicas;
        if (isJobManager) {
            jsonObject = JSON.parseObject(jobManager);
            name = createJobManagerName(openshiftClusterInfo.getName());
            jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers").getJSONObject(0).getJSONArray("args").add("jobmanager");
            resources = openshiftClusterInfo.getJobManagerResources();
            replicas = 1;
        } else {
            jsonObject = JSON.parseObject(taskManager);
            name = createTaskManagerName(openshiftClusterInfo.getName());
            jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers").getJSONObject(0).getJSONArray("args").add("taskmanager");
            resources = openshiftClusterInfo.getTaskManagerResources();
            replicas = openshiftClusterInfo.getReplicas();
        }
        OpenshiftClusterInfo.Label label = new OpenshiftClusterInfo.Label(SELECTOR_APP, name);
        //name + namespace
        jsonObject.getJSONObject("metadata").put("name", name);
        jsonObject.getJSONObject("metadata").put("namespace", openshiftProperties.getNamespace());
        //label
        jsonObject.getJSONObject("metadata").put("labels", label);
        jsonObject.getJSONObject("spec").getJSONObject("selector").put("matchLabels", label);
        jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("metadata").put("labels", label);
        //replicas
        jsonObject.getJSONObject("spec").put("replicas", replicas);
        //hadoopUserName and jobManagerAddress
        JSONObject jobManagerAddress = new JSONObject();
        jobManagerAddress.put("name", JOB_MANAGER_RPC_ADDRESS);
        jobManagerAddress.put("value", openshiftClusterInfo.getJobManagerAddress());
        jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers").getJSONObject(0).getJSONArray("env").add(jobManagerAddress);
        if (openshiftProperties.getHadoopUserName() != null) {
            JSONObject object = new JSONObject();
            object.put("name", HADOOP_USER_NAME);
            object.put("value", openshiftProperties.getHadoopUserName());
            jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers").getJSONObject(0).getJSONArray("env").add(object);
        }
        if (openshiftClusterInfo.getConfigs() != null) {
            JSONObject object = new JSONObject();
            object.put("name", FLINK_ADD_CONFIG);
            object.put("value", createConfigs(openshiftClusterInfo.getConfigs()));
            jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers").getJSONObject(0).getJSONArray("env").add(object);
        }
        if (openshiftClusterInfo.getEnvs() != null) {
            openshiftClusterInfo.getEnvs().entrySet().forEach(env -> {
                JSONObject object = new JSONObject();
                object.put("name", env.getKey());
                object.put("value", env.getValue());
                jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers").getJSONObject(0).getJSONArray("env").add(object);
            });
        }
        jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers").getJSONObject(0).put("name", name);
        // hadoop path
        jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("volumes").getJSONObject(0).getJSONObject("configMap").put("name", openshiftProperties.getHadoopVolumeName());
        // image
        if (openshiftClusterInfo.getImage() != null) {
            jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers").getJSONObject(0).put("image", openshiftClusterInfo.getImage());
        }
        // resources
        if (resources != null && resources.getRequests() != null) {
            jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers").getJSONObject(0).getJSONObject("resources").put("requests", resources.getRequests());
        }
        if (resources != null && resources.getLimits() != null) {
            jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").getJSONArray("containers").getJSONObject(0).getJSONObject("resources").put("limits", resources.getLimits());
        }
        // account
        if (openshiftProperties.getServiceAccount() != null) {
            jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").put("serviceAccount", openshiftProperties.getServiceAccount());
        }
        if (openshiftProperties.getServiceAccountName() != null) {
            jsonObject.getJSONObject("spec").getJSONObject("template").getJSONObject("spec").put("serviceAccountName", openshiftProperties.getServiceAccountName());
        }
        return JsonUtil.toJson(jsonObject);
    }

    private String createConfigs(Map<String, Object> configs) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Object> config : configs.entrySet()) {
            stringBuilder.append(config.getKey()).append(" ").append(config.getValue()).append(" ");
        }
        return stringBuilder.toString();
    }

    private String createJobManagerName(String name) {
        return JOB_MANAGER_NAME_PREFIX + name;
    }

    private String createTaskManagerName(String name) {
        return TASK_MANAGER_NAME_PREFIX + name;
    }

    private String getDeploymentsCreateUrl() {
        return String.format(DEPLOYMENTS_CREATE_URL, openshiftProperties.getUrl(), openshiftProperties.getNamespace());
    }

    private String getDeploymentsSpecifyUrl(OpenshiftClusterInfo openshiftClusterInfo, boolean jobManager) {
        if (jobManager) {
            return String.format(DEPLOYMENTS_SPECIFY_URL, openshiftProperties.getUrl(), openshiftProperties.getNamespace(), createJobManagerName(openshiftClusterInfo.getName()));
        } else {
            return String.format(DEPLOYMENTS_SPECIFY_URL, openshiftProperties.getUrl(), openshiftProperties.getNamespace(), createTaskManagerName(openshiftClusterInfo.getName()));
        }
    }

    private String getServiceCreateUrl() {
        return String.format(SERVICE_CREATE_URL, openshiftProperties.getUrl(), openshiftProperties.getNamespace());
    }

    private String getServiceSpecifyUrl(OpenshiftClusterInfo openshiftClusterInfo) {
        return String.format(SERVICE_SPECIFY_URL, openshiftProperties.getUrl(), openshiftProperties.getNamespace(), openshiftClusterInfo.getJobManagerAddress());
    }

    private String getRouterCreateUrl() {
        return String.format(ROUTER_CREATE_URL, openshiftProperties.getUrl(), openshiftProperties.getNamespace());
    }

    private String getRouterSpecifyUrl(OpenshiftClusterInfo openshiftClusterInfo) {
        return String.format(ROUTER_SPECIFY_URL, openshiftProperties.getUrl(), openshiftProperties.getNamespace(), openshiftClusterInfo.getJobManagerAddress());
    }

    public class TokenTask implements Runnable {

        private static final String TOKEN_KEY = "access_token";

        private final HttpHeaders httpHeaders;

        public TokenTask() {
            this.httpHeaders = create();
        }

        @Override
        public void run() {
            try {
                URI uri = restTemplate.postForLocation(openshiftProperties.getUrl() + "/oauth/authorize?client_id=openshift-challenging-client&response_type=token", new HttpEntity<>(null, httpHeaders));
                String token = parseToken(uri);
                if (token == null) {
                    log.error("Failed to get token, uri:{},fragment:{} ", uri);
                } else {
                    setToken(token);
                }
            } catch (Exception e) {
                log.error("Failed to get token", e);
            }
        }

        private HttpHeaders create() {
            HttpHeaders httpHeaders = new HttpHeaders();
            String auth = openshiftProperties.getUsername() + ":" + openshiftProperties.getPassword();
            httpHeaders.set("X-CSRF-Token", "1");
            httpHeaders.set("Authorization", "Basic " + new String(Base64.encodeBase64(auth.getBytes())));
            return httpHeaders;
        }


        private String parseToken(URI uri) {
            if(uri == null){
                return null;
            }
            String fragment = uri.getFragment();
            if (fragment == null || fragment.length() == 0) {
                return null;
            }
            String[] fragments = fragment.split("&");
            for (String value : fragments) {
                String[] params = value.split("=");
                if (params.length == 2 && TOKEN_KEY.equals(params[0]) ) {
                    return params[1];
                }
            }
            return null;
        }

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
