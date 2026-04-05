package com.aleksandarsapic.llm_doc_generator.infrastructure.llm;

import com.aleksandarsapic.llm_doc_generator.config.properties.LlmProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    /**
     * Selects the ChatModel bean by name based on the configured provider.
     * Spring AI registers beans named "openAiChatModel" and "ollamaChatModel".
     * We inject both and pick the right one at runtime.
     */
    @Bean
    public ChatClient chatClient(
            LlmProperties llmProperties,
            @Qualifier("openAiChatModel") ChatModel openAiChatModel,
            @Qualifier("ollamaChatModel") ChatModel ollamaChatModel) {

        ChatModel selected = switch (llmProperties.getProvider().toLowerCase()) {
            case "ollama" -> ollamaChatModel;
            default -> openAiChatModel;
        };
        return ChatClient.builder(selected).build();
    }
}
