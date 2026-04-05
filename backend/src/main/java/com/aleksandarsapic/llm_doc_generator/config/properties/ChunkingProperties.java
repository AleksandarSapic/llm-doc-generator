package com.aleksandarsapic.llm_doc_generator.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix = "app.chunking")
public class ChunkingProperties {
    @Min(100)
    private int chunkTokenTarget = 3000;

    @Min(0)
    private int chunkTokenOverlap = 200;

    @NotEmpty
    private List<String> allowedExtensions;

    @NotEmpty
    private List<String> ignoredDirectories;
}
