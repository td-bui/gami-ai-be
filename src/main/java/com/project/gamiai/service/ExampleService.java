package com.project.gamiai.service;

import java.util.Map;

public interface ExampleService {
    String getExecutableCodeById(Integer exampleId);

    String submitCodeJob(String code);

    Map<String, Object> getJobResult(String jobId);
}