package com.aleksandarsapic.llm_doc_generator.domain.port;

import com.aleksandarsapic.llm_doc_generator.domain.model.FileChunk;

import java.nio.file.Path;
import java.util.List;

public interface CodeChunker {
    List<FileChunk> chunk(Path filePath, String content);
}
