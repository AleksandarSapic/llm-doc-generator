package com.aleksandarsapic.llm_doc_generator.domain.port;

import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JobRepository {
    DocJob save(DocJob job);
    Optional<DocJob> findById(String jobId);
    List<DocJob> findAll();
    void deleteById(String jobId);
    Optional<DocJob> findCompletedByRepoAndSha(String repositoryUrl, String commitSha);
    Optional<DocJob> findActiveByRepositoryUrl(String repositoryUrl);
    void deleteByCreatedAtBefore(Instant cutoff);
}
