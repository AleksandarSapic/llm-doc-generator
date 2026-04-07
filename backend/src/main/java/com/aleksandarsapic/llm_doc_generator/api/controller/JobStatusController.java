package com.aleksandarsapic.llm_doc_generator.api.controller;

import com.aleksandarsapic.llm_doc_generator.api.dto.response.JobStatusResponse;
import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;
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
    public ResponseEntity<JobStatusResponse> getStatus(
            @PathVariable String jobId) {
        DocJob job = orchestrator.getJob(jobId);
        return ResponseEntity.ok(toStatusResponse(job));
    }

    @GetMapping
    public ResponseEntity<List<JobStatusResponse>> listJobs() {
        List<JobStatusResponse> responses = orchestrator.getAllJobs().stream()
                .map(this::toStatusResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    private JobStatusResponse toStatusResponse(DocJob job) {
        return JobStatusResponse.builder()
                .jobId(job.getJobId())
                .status(job.getStatus())
                .statusMessage(job.getStatusMessage())
                .totalFiles(job.getTotalFiles())
                .processedFiles(job.getProcessedFiles())
                .progressPercent(job.getProgressPercent())
                .errorMessage(job.getErrorMessage())
                .llmProvider(job.getLlmProvider())
                .llmModel(job.getLlmModel())
                .build();
    }
}
