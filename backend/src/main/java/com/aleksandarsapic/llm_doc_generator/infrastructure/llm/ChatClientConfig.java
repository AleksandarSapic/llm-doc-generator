package com.aleksandarsapic.llm_doc_generator.infrastructure.llm;

import com.aleksandarsapic.llm_doc_generator.domain.model.LlmProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ChatClientConfig {

    @Bean
    public Map<LlmProvider, ChatClient> chatClientsByProvider(
            @Qualifier("openAiChatModel") ChatModel openAiChatModel,
            @Qualifier("ollamaChatModel") ChatModel ollamaChatModel,
            @Qualifier("anthropicChatModel") ChatModel anthropicChatModel) {

        return Map.of(
                LlmProvider.OPENAI,    ChatClient.builder(openAiChatModel).build(),
                LlmProvider.OLLAMA,    ChatClient.builder(ollamaChatModel).build(),
                LlmProvider.ANTHROPIC, ChatClient.builder(anthropicChatModel).build()
        );
    }
}
