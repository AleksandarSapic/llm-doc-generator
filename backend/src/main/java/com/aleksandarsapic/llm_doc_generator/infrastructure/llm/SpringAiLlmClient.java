package com.aleksandarsapic.llm_doc_generator.infrastructure.llm;

import com.aleksandarsapic.llm_doc_generator.config.properties.LlmProperties;
import com.aleksandarsapic.llm_doc_generator.domain.model.FileChunk;
import com.aleksandarsapic.llm_doc_generator.domain.model.FileExplanation;
import com.aleksandarsapic.llm_doc_generator.domain.port.LlmClient;
import com.aleksandarsapic.llm_doc_generator.exception.LlmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAiLlmClient implements LlmClient {

    private final ChatClient chatClient;
    private final PromptTemplates promptTemplates;
    private final LlmProperties llmProperties;

    @Override
    public FileExplanation explainChunks(List<FileChunk> chunks) {
        if (chunks.isEmpty()) {
            throw new LlmException("Cannot explain empty chunk list");
        }

        String combinedContent = chunks.stream()
                .map(FileChunk::getContent)
                .collect(Collectors.joining("\n\n"));

        String prompt = promptTemplates.fileExplanationPrompt(combinedContent);
        String filePath = chunks.getFirst().getFilePath();

        String explanation = callWithRetry(prompt, filePath);

        return FileExplanation.builder()
                .filePath(filePath)
                .explanation(explanation)
                .build();
    }

    @Override
    public String summarizeProject(List<FileExplanation> explanations, String repositoryUrl) {
        String allExplanations = explanations.stream()
                .map(e -> "### " + e.getFilePath() + "\n" + e.getExplanation())
                .collect(Collectors.joining("\n\n"));

        String prompt = promptTemplates.projectSummaryPrompt(allExplanations, repositoryUrl);
        return callWithRetry(prompt, "project-summary");
    }

    private String callWithRetry(String prompt, String context) {
        int maxRetries = llmProperties.getMaxRetries();
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content();
            } catch (Exception e) {
                lastException = e;
                log.warn("LLM call failed for {} (attempt {}/{}): {}", context, attempt, maxRetries, e.getMessage());
                if (attempt < maxRetries) {
                    long backoffMs = 1000L * (1L << (attempt - 1)); // exponential: 1s, 2s, 4s
                    try {
                        Thread.sleep(backoffMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new LlmException("Interrupted during LLM retry backoff", ie);
                    }
                }
            }
        }

        throw new LlmException("LLM call failed after " + maxRetries + " attempts for: " + context, lastException);
    }
}
