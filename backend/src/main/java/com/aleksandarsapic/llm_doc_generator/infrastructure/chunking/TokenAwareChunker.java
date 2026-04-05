package com.aleksandarsapic.llm_doc_generator.infrastructure.chunking;

import com.aleksandarsapic.llm_doc_generator.config.properties.ChunkingProperties;
import com.aleksandarsapic.llm_doc_generator.domain.model.FileChunk;
import com.aleksandarsapic.llm_doc_generator.domain.port.CodeChunker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAwareChunker implements CodeChunker {

    private final ChunkingProperties chunkingProperties;

    // Approximate: chars / 4 ≈ tokens
    private static final int CHARS_PER_TOKEN = 4;

    @Override
    public List<FileChunk> chunk(Path filePath, String content) {
        int targetChars = chunkingProperties.getChunkTokenTarget() * CHARS_PER_TOKEN;
        int overlapChars = chunkingProperties.getChunkTokenOverlap() * CHARS_PER_TOKEN;

        String[] lines = content.split("\n", -1);
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();
        List<String> overlapLines = new ArrayList<>();

        for (String line : lines) {
            currentChunk.append(line).append("\n");

            if (currentChunk.length() >= targetChars) {
                chunks.add(currentChunk.toString());
                // Compute overlap from the end of this chunk
                overlapLines.clear();
                int overlapSize = 0;
                String[] chunkLines = currentChunk.toString().split("\n", -1);
                for (int i = chunkLines.length - 1; i >= 0 && overlapSize < overlapChars; i--) {
                    overlapLines.addFirst(chunkLines[i]);
                    overlapSize += chunkLines[i].length() + 1;
                }
                currentChunk = new StringBuilder();
                for (String overlapLine : overlapLines) {
                    currentChunk.append(overlapLine).append("\n");
                }
            }
        }

        if (!currentChunk.isEmpty()) {
            chunks.add(currentChunk.toString());
        }

        if (chunks.isEmpty()) {
            return List.of();
        }

        String filePathStr = filePath.toString();
        int totalChunks = chunks.size();
        List<FileChunk> result = new ArrayList<>();

        for (int i = 0; i < totalChunks; i++) {
            String header = String.format("// [File: %s | Chunk %d of %d]\n",
                    filePath.getFileName(), i + 1, totalChunks);
            result.add(FileChunk.builder()
                    .filePath(filePathStr)
                    .content(header + chunks.get(i))
                    .chunkIndex(i)
                    .totalChunks(totalChunks)
                    .build());
        }

        return result;
    }
}
