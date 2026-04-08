package com.aleksandarsapic.llm_doc_generator.infrastructure.persistence;

import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;
import com.aleksandarsapic.llm_doc_generator.domain.model.DocJobStatus;
import com.aleksandarsapic.llm_doc_generator.domain.port.JobRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Profile("no-db")
@Repository
public class InMemoryJobRepository implements JobRepository {

    private final ConcurrentHashMap<String, DocJob> store = new ConcurrentHashMap<>();

    @Override
    public DocJob save(DocJob job) {
        store.put(job.getJobId(), job);
        return job;
    }

    @Override
    public Optional<DocJob> findById(String jobId) {
        return Optional.ofNullable(store.get(jobId));
    }

    @Override
    public List<DocJob> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(String jobId) {
        store.remove(jobId);
    }

    @Override
    public Optional<DocJob> findCompletedByRepoAndSha(String repositoryUrl, String commitSha) {
        if (commitSha == null) return Optional.empty();
        return store.values().stream()
                .filter(j -> DocJobStatus.COMPLETED.equals(j.getStatus())
                        && repositoryUrl.equals(j.getRepositoryUrl())
                        && commitSha.equals(j.getGitCommitSha()))
                .findFirst();
    }

    @Override
    public Optional<DocJob> findActiveByRepositoryUrl(String repositoryUrl) {
        return store.values().stream()
                .filter(j -> repositoryUrl.equals(j.getRepositoryUrl())
                        && j.getStatus() != DocJobStatus.FAILED)
                .max(Comparator.comparing(DocJob::getCreatedAt));
    }

    @Override
    public List<String> deleteByCreatedAtBefore(Instant cutoff) {
        List<String> ids = store.values().stream()
                .filter(j -> j.getCreatedAt() != null && j.getCreatedAt().isBefore(cutoff))
                .map(DocJob::getJobId)
                .toList();
        ids.forEach(store::remove);
        return ids;
    }
}
