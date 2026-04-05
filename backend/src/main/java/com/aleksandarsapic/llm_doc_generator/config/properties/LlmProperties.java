package com.aleksandarsapic.llm_doc_generator.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "app.llm")
public class LlmProperties {
    @NotBlank
    private String provider = "openai";

    @Min(1)
    private int maxRetries = 3;

    @Positive
    private long requestTimeoutMs = 90000;
}
