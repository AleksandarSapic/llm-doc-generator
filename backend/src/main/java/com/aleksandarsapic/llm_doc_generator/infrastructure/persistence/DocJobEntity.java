package com.aleksandarsapic.llm_doc_generator.infrastructure.persistence;

import com.aleksandarsapic.llm_doc_generator.domain.model.DocJobStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "doc_jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocJobEntity {

    @Id
    @Column(name = "job_id", length = 36, nullable = false)
    private String jobId;

    @Column(name = "repository_url", length = 2048, nullable = false)
    private String repositoryUrl;

    @Column(name = "git_commit_sha", length = 40)
    private String gitCommitSha;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32, nullable = false)
    private DocJobStatus status;

    @Column(name = "status_message", columnDefinition = "TEXT")
    private String statusMessage;

    @Column(name = "total_files", nullable = false)
    private int totalFiles;

    @Column(name = "processed_files", nullable = false)
    private int processedFiles;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "llm_provider", length = 64)
    private String llmProvider;

    @Column(name = "llm_model", length = 128)
    private String llmModel;

    @Column(name = "result_json", columnDefinition = "TEXT")
    private String resultJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
