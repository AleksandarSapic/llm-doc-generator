package com.aleksandarsapic.llm_doc_generator.infrastructure.llm;

import com.aleksandarsapic.llm_doc_generator.config.properties.LlmProperties;
import com.aleksandarsapic.llm_doc_generator.domain.model.FileChunk;
import com.aleksandarsapic.llm_doc_generator.domain.model.FileExplanation;
import com.aleksandarsapic.llm_doc_generator.domain.model.LlmProvider;
import com.aleksandarsapic.llm_doc_generator.domain.model.LlmSelection;
import com.aleksandarsapic.llm_doc_generator.domain.port.LlmClient;
import com.aleksandarsapic.llm_doc_generator.exception.LlmException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringAiLlmClient implements LlmClient {

    private final Map<LlmProvider, ChatClient> chatClientsByProvider;
    private final PromptTemplates promptTemplates;
    private final LlmProperties llmProperties;

    @Override
    public FileExplanation explainChunks(LlmSelection selection, List<FileChunk> chunks) {
        if (chunks.isEmpty()) {
            throw new LlmException("Cannot explain empty chunk list");
        }

        String combinedContent = chunks.stream()
                .map(FileChunk::getContent)
                .collect(Collectors.joining("\n\n"));

        String prompt = promptTemplates.fileExplanationPrompt(combinedContent);
        String filePath = chunks.getFirst().getFilePath();

        String explanation = callWithRetry(prompt, filePath, selection);

        return FileExplanation.builder()
                .filePath(filePath)
                .explanation(explanation)
                .build();
    }

    @Override
    public String summarizeProject(LlmSelection selection, List<FileExplanation> explanations, String repositoryUrl) {
        String allExplanations = explanations.stream()
                .map(e -> "### " + e.getFilePath() + "\n" + e.getExplanation())
                .collect(Collectors.joining("\n\n"));

        String prompt = promptTemplates.projectSummaryPrompt(allExplanations, repositoryUrl);
        return callWithRetry(prompt, "project-summary", selection);
    }

    private String callWithRetry(String prompt, String context, LlmSelection selection) {
        ChatClient client = chatClientsByProvider.get(selection.provider());
        if (client == null) {
            throw new LlmException("No ChatClient configured for provider: " + selection.provider());
        }

        ChatOptions options = buildOptions(selection);
        int maxRetries = llmProperties.getMaxRetries();
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return client.prompt()
                        .options(options)
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

    private ChatOptions buildOptions(LlmSelection selection) {
        return switch (selection.provider()) {
            case OPENAI -> OpenAiChatOptions.builder().model(selection.model()).build();
            case OLLAMA -> OllamaChatOptions.builder().model(selection.model()).build();
            case ANTHROPIC -> AnthropicChatOptions.builder().model(selection.model()).build();
        };
    }
}
