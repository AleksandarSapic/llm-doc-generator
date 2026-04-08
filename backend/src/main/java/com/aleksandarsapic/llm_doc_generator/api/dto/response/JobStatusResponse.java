package com.aleksandarsapic.llm_doc_generator.api.dto.response;

import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;
import com.aleksandarsapic.llm_doc_generator.domain.model.DocJobStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class JobStatusResponse {
    String jobId;
    DocJobStatus status;
    String statusMessage;
    int totalFiles;
    int processedFiles;
    int progressPercent;
    String errorMessage;
    String llmProvider;
    String llmModel;

    public static JobStatusResponse from(DocJob job) {
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
