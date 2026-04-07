package com.aleksandarsapic.llm_doc_generator.service;

import com.aleksandarsapic.llm_doc_generator.domain.model.*;
import com.aleksandarsapic.llm_doc_generator.domain.port.*;
import com.aleksandarsapic.llm_doc_generator.exception.JobNotFoundException;
import com.aleksandarsapic.llm_doc_generator.util.TempDirectoryManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobProcessor {

    private final JobRepository jobRepository;
    private final JobProgressTracker progressTracker;
    private final GitCloner gitCloner;
    private final FileTraverser fileTraverser;
    private final CodeChunker codeChunker;
    private final LlmClient llmClient;
    private final DocumentationAggregator documentationAggregator;
    private final TempDirectoryManager tempDirectoryManager;

    @Async("jobExecutor")
    public void process(String jobId, LlmSelection selection) {
        MDC.put("jobId", jobId);
        DocJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));
        Path tempDir = null;

        try {
            // Phase 1: Clone
            progressTracker.updateStatus(job, DocJobStatus.CLONING, "Cloning repository...");
            tempDir = tempDirectoryManager.createTempDirectory("llm-doc-" + jobId + "-");
            gitCloner.clone(job.getRepositoryUrl(), tempDir);

            // Phase 2: Traverse
            progressTracker.updateStatus(job, DocJobStatus.TRAVERSING, "Discovering source files...");
            List<Path> sourceFiles = fileTraverser.traverse(tempDir);
            job.setTotalFiles(sourceFiles.size());
            progressTracker.updateStatus(job, DocJobStatus.TRAVERSING,
                    "Found " + sourceFiles.size() + " source files");

            // Phase 3: Process each file
            progressTracker.updateStatus(job, DocJobStatus.PROCESSING, "Starting LLM analysis...");
            List<FileExplanation> explanations = new ArrayList<>();
            int processed = 0;

            for (Path filePath : sourceFiles) {
                String fileName = filePath.getFileName().toString();
                progressTracker.updateProgress(job, processed,
                        "Explaining file " + (processed + 1) + " of " + sourceFiles.size() + ": " + fileName);

                try {
                    String content = Files.readString(filePath);
                    Path relativePath = tempDir.relativize(filePath);
                    List<FileChunk> chunks = codeChunker.chunk(relativePath, content);
                    if (!chunks.isEmpty()) {
                        FileExplanation explanation = llmClient.explainChunks(selection, chunks);
                        explanations.add(FileExplanation.builder()
                                .filePath(relativePath.toString())
                                .explanation(explanation.getExplanation())
                                .build());
                    }
                } catch (Exception e) {
                    log.warn("Failed to explain file {}, skipping: {}", filePath, e.getMessage());
                }

                processed++;
                progressTracker.updateProgress(job, processed,
                        "Explained " + processed + " of " + sourceFiles.size() + " files");
            }

            // Phase 4: Aggregate
            progressTracker.updateStatus(job, DocJobStatus.AGGREGATING, "Generating project summary...");
            String projectSummary = llmClient.summarizeProject(selection, explanations, job.getRepositoryUrl());
            ProjectDocumentation documentation = documentationAggregator.aggregate(
                    jobId, job.getRepositoryUrl(), explanations, projectSummary);

            // Phase 5: Complete
            job.setResult(documentation);
            progressTracker.updateStatus(job, DocJobStatus.COMPLETED,
                    "Documentation generated for " + explanations.size() + " files");

        } catch (Exception e) {
            log.error("Job {} failed with exception", jobId, e);
            progressTracker.markFailed(job, e.getMessage());
        } finally {
            if (tempDir != null) {
                tempDirectoryManager.deleteRecursively(tempDir);
            }
            MDC.remove("jobId");
        }
    }
}
