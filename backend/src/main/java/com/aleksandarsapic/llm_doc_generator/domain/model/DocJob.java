package com.aleksandarsapic.llm_doc_generator.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DocJob {
    private String jobId;
    private String repositoryUrl;
    private DocJobStatus status;
    private String statusMessage;
    private int totalFiles;
    private int processedFiles;
    private String errorMessage;
    private String llmProvider;
    private String llmModel;
    private ProjectDocumentation result;
    private Instant createdAt;
    private Instant updatedAt;

    public int getProgressPercent() {
        if (totalFiles == 0) return 0;
        return (int) ((processedFiles * 100L) / totalFiles);
    }
}
