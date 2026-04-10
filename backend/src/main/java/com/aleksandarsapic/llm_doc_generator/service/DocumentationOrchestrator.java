package com.aleksandarsapic.llm_doc_generator.service;

import com.aleksandarsapic.llm_doc_generator.config.properties.LlmProperties;
import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;
import com.aleksandarsapic.llm_doc_generator.domain.model.DocJobStatus;
import com.aleksandarsapic.llm_doc_generator.domain.model.LlmProvider;
import com.aleksandarsapic.llm_doc_generator.domain.model.LlmSelection;
import com.aleksandarsapic.llm_doc_generator.domain.model.PromptConfig;
import com.aleksandarsapic.llm_doc_generator.domain.port.JobRepository;
import com.aleksandarsapic.llm_doc_generator.exception.JobNotFoundException;
import com.aleksandarsapic.llm_doc_generator.util.RepositoryUrlValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentationOrchestrator {

    private final JobRepository jobRepository;
    private final JobProcessor jobProcessor;
    private final RepositoryUrlValidator urlValidator;
    private final LlmProperties llmProperties;

    public DocJob submitJob(String repositoryUrl, String providerStr, String modelStr,
                            String fileExplanationTemplate, String projectSummaryTemplate) {
        urlValidator.validate(repositoryUrl);

        return jobRepository.findActiveByRepositoryUrl(repositoryUrl)
                .orElseGet(() -> createAndDispatch(repositoryUrl, providerStr, modelStr,
                        fileExplanationTemplate, projectSummaryTemplate));
    }

    private DocJob createAndDispatch(String repositoryUrl, String providerStr, String modelStr,
                                     String fileExplanationTemplate, String projectSummaryTemplate) {
        String resolvedProviderStr = StringUtils.hasText(providerStr)
                ? providerStr
                : llmProperties.getProvider();
        LlmProvider provider = LlmProvider.fromString(resolvedProviderStr);
        String model = llmProperties.resolveModel(provider, modelStr);

        String jobId = UUID.randomUUID().toString();
        DocJob job = DocJob.builder()
                .jobId(jobId)
                .repositoryUrl(repositoryUrl)
                .status(DocJobStatus.PENDING)
                .statusMessage("Job queued")
                .llmProvider(provider.name().toLowerCase())
                .llmModel(model)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        jobRepository.save(job);
        PromptConfig promptConfig = new PromptConfig(fileExplanationTemplate, projectSummaryTemplate);
        jobProcessor.process(jobId, new LlmSelection(provider, model), promptConfig);
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
