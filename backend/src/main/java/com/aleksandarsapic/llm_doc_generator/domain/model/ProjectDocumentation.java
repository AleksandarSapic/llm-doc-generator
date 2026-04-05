package com.aleksandarsapic.llm_doc_generator.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class ProjectDocumentation {
    String jobId;
    String repositoryUrl;
    String markdownContent;
    List<FileExplanation> fileExplanations;
    Instant generatedAt;
}
