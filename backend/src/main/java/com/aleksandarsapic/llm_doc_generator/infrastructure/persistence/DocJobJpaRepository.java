package com.aleksandarsapic.llm_doc_generator.infrastructure.persistence;

import com.aleksandarsapic.llm_doc_generator.domain.model.DocJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface DocJobJpaRepository extends JpaRepository<DocJobEntity, String> {

    Optional<DocJobEntity> findByRepositoryUrlAndGitCommitShaAndStatus(
            String repositoryUrl,
            String gitCommitSha,
            DocJobStatus status);

    Optional<DocJobEntity> findFirstByRepositoryUrlAndStatusNotOrderByCreatedAtDesc(
            String repositoryUrl,
            DocJobStatus status);

    @Modifying
    @Query("DELETE FROM DocJobEntity d WHERE d.createdAt < :cutoff")
    void deleteByCreatedAtBefore(@Param("cutoff") Instant cutoff);
}
