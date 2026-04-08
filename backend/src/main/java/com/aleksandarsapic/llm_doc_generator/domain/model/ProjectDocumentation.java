package com.aleksandarsapic.llm_doc_generator.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
@JsonDeserialize(builder = ProjectDocumentation.ProjectDocumentationBuilder.class)
public class ProjectDocumentation {
    String jobId;
    String repositoryUrl;
    String markdownContent;
    List<FileExplanation> fileExplanations;
    Instant generatedAt;

    @JsonPOJOBuilder(withPrefix = "")
    public static class ProjectDocumentationBuilder {}
}
