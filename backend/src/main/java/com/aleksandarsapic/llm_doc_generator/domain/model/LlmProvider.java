package com.aleksandarsapic.llm_doc_generator.domain.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum LlmProvider {
    OPENAI, OLLAMA, ANTHROPIC;

    public static LlmProvider fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Provider must not be blank");
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            String valid = Arrays.stream(values())
                    .map(p -> p.name().toLowerCase())
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "Unsupported provider: " + value + ". Valid values are: " + valid);
        }
    }
}
