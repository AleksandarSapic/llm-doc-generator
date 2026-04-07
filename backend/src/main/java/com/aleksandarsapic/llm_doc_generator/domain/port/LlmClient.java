package com.aleksandarsapic.llm_doc_generator.domain.port;

import com.aleksandarsapic.llm_doc_generator.domain.model.FileChunk;
import com.aleksandarsapic.llm_doc_generator.domain.model.FileExplanation;
import com.aleksandarsapic.llm_doc_generator.domain.model.LlmSelection;

import java.util.List;

public interface LlmClient {
    FileExplanation explainChunks(LlmSelection selection, List<FileChunk> chunks);
    String summarizeProject(LlmSelection selection, List<FileExplanation> explanations, String repositoryUrl);
}
