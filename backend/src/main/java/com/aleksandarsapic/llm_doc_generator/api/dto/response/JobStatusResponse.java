package com.aleksandarsapic.llm_doc_generator.api.dto.response;

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
}
