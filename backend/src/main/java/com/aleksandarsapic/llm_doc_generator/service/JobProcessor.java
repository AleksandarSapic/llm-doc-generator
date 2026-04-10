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
    public void process(String jobId, LlmSelection selection, PromptConfig promptConfig) {
        try (var ignored = MDC.putCloseable("jobId", jobId)) {
            DocJob job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new JobNotFoundException(jobId));
            Path tempDir = null;

            try {
                progressTracker.updateStatus(job, DocJobStatus.CLONING, "Cloning repository...");
                tempDir = tempDirectoryManager.createTempDirectory("llm-doc-" + jobId + "-");
                GitCloner.CloningResult cloneResult = gitCloner.clone(job.getRepositoryUrl(), tempDir);

                job.setGitCommitSha(cloneResult.commitSha());

                if (returnCachedIfAvailable(job, cloneResult.commitSha())) {
                    return;
                }

                jobRepository.save(job);
                List<FileExplanation> explanations = explainFiles(job, selection, promptConfig, cloneResult.directory());
                aggregateAndComplete(job, jobId, selection, promptConfig, explanations);
            } catch (Exception e) {
                log.error("Job {} failed with exception", jobId, e);
                progressTracker.markFailed(job, e.getMessage());
            } finally {
                if (tempDir != null) {
                    tempDirectoryManager.deleteRecursively(tempDir);
                }
            }
        }
    }

    /**
     * Copies cached result onto the job if a completed job for this repo+SHA exists.
     * Returns true if cache hit (job is marked complete and caller should return early).
     */
    private boolean returnCachedIfAvailable(DocJob job, String commitSha) {
        return jobRepository.findCompletedByRepoAndSha(job.getRepositoryUrl(), commitSha)
                .map(cached -> {
                    job.setResult(cached.getResult());
                    job.setTotalFiles(cached.getTotalFiles());
                    job.setProcessedFiles(cached.getProcessedFiles());
                    progressTracker.updateStatus(job, DocJobStatus.COMPLETED,
                            "Returned cached result from commit " + commitSha.substring(0, 7));
                    return true;
                })
                .orElse(false);
    }

    private List<FileExplanation> explainFiles(DocJob job, LlmSelection selection,
                                               PromptConfig promptConfig, Path repoDir) {
        progressTracker.updateStatus(job, DocJobStatus.TRAVERSING, "Discovering source files...");
        List<Path> sourceFiles = fileTraverser.traverse(repoDir);
        job.setTotalFiles(sourceFiles.size());
        progressTracker.updateStatus(job, DocJobStatus.TRAVERSING,
                "Found " + sourceFiles.size() + " source files");

        progressTracker.updateStatus(job, DocJobStatus.PROCESSING, "Starting LLM analysis...");
        List<FileExplanation> explanations = new ArrayList<>();

        for (int i = 0; i < sourceFiles.size(); i++) {
            Path relativePath = repoDir.relativize(sourceFiles.get(i));
            try {
                String content = Files.readString(sourceFiles.get(i));
                List<FileChunk> chunks = codeChunker.chunk(relativePath, content);
                if (!chunks.isEmpty()) {
                    explanations.add(llmClient.explainChunks(selection, promptConfig, chunks));
                }
            } catch (Exception e) {
                log.warn("Failed to explain file {}, skipping: {}", relativePath, e.getMessage());
            }
            progressTracker.updateProgress(job, i + 1,
                    "Explained " + (i + 1) + " of " + sourceFiles.size() + " files");
        }

        return explanations;
    }

    private void aggregateAndComplete(DocJob job, String jobId, LlmSelection selection,
                                      PromptConfig promptConfig, List<FileExplanation> explanations) {
        progressTracker.updateStatus(job, DocJobStatus.AGGREGATING, "Generating project summary...");
        String projectSummary = llmClient.summarizeProject(selection, promptConfig, explanations, job.getRepositoryUrl());
        ProjectDocumentation documentation = documentationAggregator.aggregate(
                jobId, job.getRepositoryUrl(), explanations, projectSummary);

        job.setResult(documentation);
        progressTracker.updateStatus(job, DocJobStatus.COMPLETED,
                "Documentation generated for " + explanations.size() + " files");
    }
}
