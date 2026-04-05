package com.aleksandarsapic.llm_doc_generator.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FileChunk {
    String filePath;
    String content;
    int chunkIndex;
    int totalChunks;
}
