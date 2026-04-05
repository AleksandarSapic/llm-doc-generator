package com.aleksandarsapic.llm_doc_generator.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "app.git")
public class GitProperties {
    @Min(1)
    private int cloneDepth = 1;

    @Positive
    private int cloneTimeoutSeconds = 120;

    @NotBlank
    private String tempBaseDir;
}
