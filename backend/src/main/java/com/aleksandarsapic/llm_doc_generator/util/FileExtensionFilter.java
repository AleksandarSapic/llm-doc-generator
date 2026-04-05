package com.aleksandarsapic.llm_doc_generator.util;

import com.aleksandarsapic.llm_doc_generator.config.properties.ChunkingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileExtensionFilter {

    private final ChunkingProperties chunkingProperties;

    public boolean isAllowed(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) return false;
        String extension = fileName.substring(dotIndex);
        return getAllowedExtensions().contains(extension.toLowerCase());
    }

    public boolean isInIgnoredDirectory(Path filePath, Path rootDirectory) {
        Set<String> ignoredDirs = getIgnoredDirectories();
        Path relative = rootDirectory.relativize(filePath);
        for (Path part : relative) {
            if (ignoredDirs.contains(part.toString())) {
                return true;
            }
        }
        return false;
    }

    private Set<String> getAllowedExtensions() {
        return chunkingProperties.getAllowedExtensions().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private Set<String> getIgnoredDirectories() {
        return chunkingProperties.getIgnoredDirectories().stream()
                .collect(Collectors.toSet());
    }
}
