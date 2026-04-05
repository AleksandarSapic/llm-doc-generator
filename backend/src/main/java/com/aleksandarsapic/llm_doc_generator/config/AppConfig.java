package com.aleksandarsapic.llm_doc_generator.config;

import com.aleksandarsapic.llm_doc_generator.config.properties.ChunkingProperties;
import com.aleksandarsapic.llm_doc_generator.config.properties.GitProperties;
import com.aleksandarsapic.llm_doc_generator.config.properties.LlmProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({LlmProperties.class, GitProperties.class, ChunkingProperties.class})
public class AppConfig {
}
