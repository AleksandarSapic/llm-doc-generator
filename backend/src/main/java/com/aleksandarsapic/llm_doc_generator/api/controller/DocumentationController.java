package com.aleksandarsapic.llm_doc_generator.api.controller;

import com.aleksandarsapic.llm_doc_generator.api.dto.request.GenerateDocumentationRequest;
import com.aleksandarsapic.llm_doc_generator.api.dto.response.DocumentationResponse;
import com.aleksandarsapic.llm_doc_generator.api.dto.response.JobSubmittedResponse;
import com.aleksandarsapic.llm_doc_generator.api.dto.response.PromptTemplatesResponse;
import com.aleksandarsapic.llm_doc_generator.api.validation.PromptTemplateValidator;
import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;
import com.aleksandarsapic.llm_doc_generator.domain.model.DocJobStatus;
import com.aleksandarsapic.llm_doc_generator.domain.model.ProjectDocumentation;
import com.aleksandarsapic.llm_doc_generator.infrastructure.llm.PromptTemplates;
import com.aleksandarsapic.llm_doc_generator.service.DocumentationOrchestrator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/documentation")
@RequiredArgsConstructor
public class DocumentationController {

    private final DocumentationOrchestrator orchestrator;
    private final PromptTemplates promptTemplates;

    @GetMapping("/prompt-templates")
    public ResponseEntity<PromptTemplatesResponse> getDefaultPromptTemplates() {
        return ResponseEntity.ok(PromptTemplatesResponse.builder()
                .fileExplanationTemplate(promptTemplates.getDefaultFileExplanationTemplate())
                .projectSummaryTemplate(promptTemplates.getDefaultProjectSummaryTemplate())
                .build());
    }

    @PostMapping("/generate")
    public ResponseEntity<JobSubmittedResponse> generate(
            @Valid @RequestBody GenerateDocumentationRequest request) {

        PromptTemplateValidator.validateFileExplanationTemplate(request.getFileExplanationTemplate());
        PromptTemplateValidator.validateProjectSummaryTemplate(request.getProjectSummaryTemplate());

        DocJob job = orchestrator.submitJob(
                request.getRepositoryUrl(), request.getProvider(), request.getModel(),
                request.getFileExplanationTemplate(), request.getProjectSummaryTemplate());

        JobSubmittedResponse response = JobSubmittedResponse.builder()
                .jobId(job.getJobId())
                .statusUrl("/api/v1/jobs/" + job.getJobId() + "/status")
                .message("Job submitted successfully")
                .build();

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping(value = "/{jobId}/raw", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> getDocumentationRaw(@PathVariable String jobId) {
        DocJob job = orchestrator.getJob(jobId);

        if (job.getStatus() != DocJobStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Job is not completed yet. Current status: " + job.getStatus());
        }

        ProjectDocumentation doc = job.getResult();
        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Documentation not available");
        }

        return ResponseEntity.ok(doc.getMarkdownContent());
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<DocumentationResponse> getDocumentation(
            @PathVariable String jobId) {
        DocJob job = orchestrator.getJob(jobId);

        if (job.getStatus() != DocJobStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Job is not completed yet. Current status: " + job.getStatus());
        }

        ProjectDocumentation doc = job.getResult();
        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Documentation not available");
        }

        DocumentationResponse response = DocumentationResponse.builder()
                .jobId(doc.getJobId())
                .repositoryUrl(doc.getRepositoryUrl())
                .markdownContent(doc.getMarkdownContent())
                .generatedAt(doc.getGeneratedAt())
                .totalFilesDocumented(doc.getFileExplanations().size())
                .build();

        return ResponseEntity.ok(response);
    }
}
