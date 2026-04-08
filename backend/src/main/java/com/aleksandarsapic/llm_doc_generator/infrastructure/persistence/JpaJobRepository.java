package com.aleksandarsapic.llm_doc_generator.infrastructure.persistence;

import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;
import com.aleksandarsapic.llm_doc_generator.domain.model.DocJobStatus;
import com.aleksandarsapic.llm_doc_generator.domain.port.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
@RequiredArgsConstructor
public class JpaJobRepository implements JobRepository {

    private final DocJobJpaRepository jpaRepository;
    private final DocJobMapper mapper;

    @Override
    @Transactional
    public DocJob save(DocJob job) {
        DocJobEntity saved = jpaRepository.save(mapper.toEntity(job));
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocJob> findById(String jobId) {
        return jpaRepository.findById(jobId).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocJob> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(String jobId) {
        jpaRepository.deleteById(jobId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocJob> findCompletedByRepoAndSha(String repositoryUrl, String commitSha) {
        if (commitSha == null) return Optional.empty();
        return jpaRepository.findByRepositoryUrlAndGitCommitShaAndStatus(
                        repositoryUrl, commitSha, DocJobStatus.COMPLETED)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DocJob> findActiveByRepositoryUrl(String repositoryUrl) {
        return jpaRepository.findFirstByRepositoryUrlAndStatusNotOrderByCreatedAtDesc(
                        repositoryUrl, DocJobStatus.FAILED)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteByCreatedAtBefore(Instant cutoff) {
        jpaRepository.deleteByCreatedAtBefore(cutoff);
    }
}
