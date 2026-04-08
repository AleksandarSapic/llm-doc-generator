package com.aleksandarsapic.llm_doc_generator.api.controller;

import com.aleksandarsapic.llm_doc_generator.api.dto.response.JobStatusResponse;
import com.aleksandarsapic.llm_doc_generator.service.DocumentationOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobStatusController {

    private final DocumentationOrchestrator orchestrator;

    @GetMapping("/{jobId}/status")
    public ResponseEntity<JobStatusResponse> getStatus(@PathVariable String jobId) {
        return ResponseEntity.ok(JobStatusResponse.from(orchestrator.getJob(jobId)));
    }

    @GetMapping
    public ResponseEntity<List<JobStatusResponse>> listJobs() {
        List<JobStatusResponse> responses = orchestrator.getAllJobs().stream()
                .map(JobStatusResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
