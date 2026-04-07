package com.aleksandarsapic.llm_doc_generator.config.properties;

import com.aleksandarsapic.llm_doc_generator.domain.model.LlmProvider;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

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

    private Map<String, String> defaultModels = new HashMap<>();

    public String resolveModel(LlmProvider provider, String requestedModel) {
        if (StringUtils.hasText(requestedModel)) {
            return requestedModel;
        }
        return defaultModels.getOrDefault(provider.name().toLowerCase(), provider.name().toLowerCase());
    }
}
