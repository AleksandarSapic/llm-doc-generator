package com.aleksandarsapic.llm_doc_generator.service;

import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;
import com.aleksandarsapic.llm_doc_generator.domain.model.DocJobStatus;
import com.aleksandarsapic.llm_doc_generator.domain.port.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobProgressTracker {

    private final JobRepository jobRepository;

    public void updateStatus(DocJob job, DocJobStatus status, String statusMessage) {
        job.setStatus(status);
        job.setStatusMessage(statusMessage);
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);
        log.debug("Job {} status → {} | {}", job.getJobId(), status, statusMessage);
    }

    public void updateProgress(DocJob job, int processedFiles, String statusMessage) {
        job.setProcessedFiles(processedFiles);
        job.setStatusMessage(statusMessage);
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);
    }

    public void markFailed(DocJob job, String errorMessage) {
        job.setStatus(DocJobStatus.FAILED);
        job.setErrorMessage(errorMessage);
        job.setStatusMessage("Failed: " + errorMessage);
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);
        log.error("Job {} failed: {}", job.getJobId(), errorMessage);
    }

    /** Remove jobs older than 24 hours to prevent unbounded growth. */
    @Scheduled(fixedDelay = 3_600_000) // every hour
    public void cleanupOldJobs() {
        Instant cutoff = Instant.now().minus(24, ChronoUnit.HOURS);
        jobRepository.deleteByCreatedAtBefore(cutoff);
        log.info("Cleaned up jobs created before {}", cutoff);
    }
}
