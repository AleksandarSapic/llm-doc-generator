package com.aleksandarsapic.llm_doc_generator.domain.model;

public record PromptConfig(
        String fileExplanationTemplate,
        String projectSummaryTemplate
) {
    public static PromptConfig defaults() {
        return new PromptConfig(null, null);
    }
}
