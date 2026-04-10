package com.aleksandarsapic.llm_doc_generator.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GenerateDocumentationRequest {

    @NotBlank(message = "Repository URL must not be blank")
    @Pattern(
        regexp = "^https?://.*",
        message = "Repository URL must use http or https scheme"
    )
    private String repositoryUrl;

    private String provider;

    private String model;

    private String fileExplanationTemplate;

    private String projectSummaryTemplate;
}
