package com.aleksandarsapic.llm_doc_generator.domain.port;

import com.aleksandarsapic.llm_doc_generator.domain.model.FileChunk;
import com.aleksandarsapic.llm_doc_generator.domain.model.FileExplanation;
import com.aleksandarsapic.llm_doc_generator.domain.model.LlmSelection;
import com.aleksandarsapic.llm_doc_generator.domain.model.PromptConfig;

import java.util.List;

public interface LlmClient {
    FileExplanation explainChunks(LlmSelection selection, PromptConfig promptConfig, List<FileChunk> chunks);
    String summarizeProject(LlmSelection selection, PromptConfig promptConfig, List<FileExplanation> explanations, String repositoryUrl);
}
