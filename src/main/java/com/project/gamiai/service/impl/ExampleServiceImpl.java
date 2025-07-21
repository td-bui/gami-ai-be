package com.project.gamiai.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.gamiai.repository.ExampleRepository;
import com.project.gamiai.service.ExampleService;

@Service
public class ExampleServiceImpl implements ExampleService {

    @Value("${execution.server.url}")
    private String executionServerUrl;

    @Autowired
    private ExampleRepository exampleRepository;

    @Override
    public String getExecutableCodeById(Integer exampleId) {
        return exampleRepository.findExecutableCodeById(exampleId)
                .orElseThrow(() -> new RuntimeException("Example not found with id: " + exampleId));
    }

    @Override
    public String submitCodeJob(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String execUrl = executionServerUrl + "/execute";

        Map<String, String> payload = new HashMap<>();
        payload.put("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> execResponse = restTemplate.postForEntity(execUrl, entity, Map.class);
        return (String) execResponse.getBody().get("job_id");
    }

    @Override
    public Map<String, Object> getJobResult(String jobId) {
        RestTemplate restTemplate = new RestTemplate();
        String resultUrl = executionServerUrl + "/result/" + jobId;
        ResponseEntity<Map> resultResponse = restTemplate.getForEntity(resultUrl, Map.class);
        return resultResponse.getBody();
    }
}