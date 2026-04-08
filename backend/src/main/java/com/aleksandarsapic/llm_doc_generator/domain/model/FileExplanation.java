package com.aleksandarsapic.llm_doc_generator.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonDeserialize(builder = FileExplanation.FileExplanationBuilder.class)
public class FileExplanation {
    String filePath;
    String explanation;

    @JsonPOJOBuilder(withPrefix = "")
    public static class FileExplanationBuilder {}
}
