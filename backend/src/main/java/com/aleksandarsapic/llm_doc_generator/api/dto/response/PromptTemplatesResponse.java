package com.aleksandarsapic.llm_doc_generator.api.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PromptTemplatesResponse {
    String fileExplanationTemplate;
    String projectSummaryTemplate;
}
