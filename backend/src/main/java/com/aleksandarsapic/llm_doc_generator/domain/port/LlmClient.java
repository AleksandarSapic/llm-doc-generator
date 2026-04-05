package com.aleksandarsapic.llm_doc_generator.domain.port;

import com.aleksandarsapic.llm_doc_generator.domain.model.FileChunk;
import com.aleksandarsapic.llm_doc_generator.domain.model.FileExplanation;

import java.util.List;

public interface LlmClient {
    FileExplanation explainChunks(List<FileChunk> chunks);
    String summarizeProject(List<FileExplanation> explanations, String repositoryUrl);
}
