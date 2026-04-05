package com.aleksandarsapic.llm_doc_generator.service;

import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;
import com.aleksandarsapic.llm_doc_generator.domain.model.DocJobStatus;
import com.aleksandarsapic.llm_doc_generator.domain.port.JobRepository;
import com.aleksandarsapic.llm_doc_generator.exception.JobNotFoundException;
import com.aleksandarsapic.llm_doc_generator.util.RepositoryUrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentationOrchestrator {

    private final JobRepository jobRepository;
    private final JobProcessor jobProcessor;
    private final RepositoryUrlValidator urlValidator;

    public DocJob submitJob(String repositoryUrl) {
        urlValidator.validate(repositoryUrl);
        String jobId = UUID.randomUUID().toString();
        DocJob job = DocJob.builder()
                .jobId(jobId)
                .repositoryUrl(repositoryUrl)
                .status(DocJobStatus.PENDING)
                .statusMessage("Job queued")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        jobRepository.save(job);
        jobProcessor.process(jobId);
        return job;
    }

    public DocJob getJob(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));
    }

    public List<DocJob> getAllJobs() {
        return jobRepository.findAll();
    }
}
