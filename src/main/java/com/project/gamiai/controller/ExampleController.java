package com.project.gamiai.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamiai.service.ExampleService;

@RestController
@RequestMapping("/api/examples")
public class ExampleController {
    @Autowired
    private ExampleService exampleService;

    @GetMapping("/{exampleId}")
    public Map<String, String> getExecutableCode(@PathVariable Integer exampleId) {
        String code = exampleService.getExecutableCodeById(exampleId);
        Map<String, String> response = new HashMap<>();
        response.put("code", code);
        return response;
    }

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String jobId = exampleService.submitCodeJob(code);

        Map<String, Object> response = new HashMap<>();
        response.put("job_id", jobId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/result/{jobId}")
    public ResponseEntity<Map<String, Object>> getResult(@PathVariable String jobId) {
        Map<String, Object> result = exampleService.getJobResult(jobId);
        return ResponseEntity.ok(result);
    }
}