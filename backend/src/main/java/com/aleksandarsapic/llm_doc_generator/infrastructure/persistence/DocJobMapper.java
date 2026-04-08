package com.aleksandarsapic.llm_doc_generator.infrastructure.persistence;

import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;
import com.aleksandarsapic.llm_doc_generator.domain.model.ProjectDocumentation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

@Component
public class DocJobMapper {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public DocJobEntity toEntity(DocJob domain) {
        return DocJobEntity.builder()
                .jobId(domain.getJobId())
                .repositoryUrl(domain.getRepositoryUrl())
                .gitCommitSha(domain.getGitCommitSha())
                .status(domain.getStatus())
                .statusMessage(domain.getStatusMessage())
                .totalFiles(domain.getTotalFiles())
                .processedFiles(domain.getProcessedFiles())
                .errorMessage(domain.getErrorMessage())
                .llmProvider(domain.getLlmProvider())
                .llmModel(domain.getLlmModel())
                .resultJson(serializeResult(domain.getResult()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public DocJob toDomain(DocJobEntity entity) {
        return DocJob.builder()
                .jobId(entity.getJobId())
                .repositoryUrl(entity.getRepositoryUrl())
                .gitCommitSha(entity.getGitCommitSha())
                .status(entity.getStatus())
                .statusMessage(entity.getStatusMessage())
                .totalFiles(entity.getTotalFiles())
                .processedFiles(entity.getProcessedFiles())
                .errorMessage(entity.getErrorMessage())
                .llmProvider(entity.getLlmProvider())
                .llmModel(entity.getLlmModel())
                .result(deserializeResult(entity.getResultJson()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private String serializeResult(ProjectDocumentation doc) {
        if (doc == null) return null;
        try {
            return objectMapper.writeValueAsString(doc);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize ProjectDocumentation for job " + doc.getJobId(), e);
        }
    }

    private ProjectDocumentation deserializeResult(String json) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, ProjectDocumentation.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize ProjectDocumentation", e);
        }
    }
}