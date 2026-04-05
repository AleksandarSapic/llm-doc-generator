package com.aleksandarsapic.llm_doc_generator.util;

import com.aleksandarsapic.llm_doc_generator.config.properties.GitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class TempDirectoryManager {

    private final GitProperties gitProperties;

    public Path createTempDirectory(String prefix) throws IOException {
        Path baseDir = Paths.get(gitProperties.getTempBaseDir());
        return Files.createTempDirectory(baseDir, prefix);
    }

    public void deleteRecursively(Path directory) {
        if (directory == null || !Files.exists(directory)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(directory)) {
            walk.sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.warn("Failed to delete temp file: {}", path, e);
                    }
                });
        } catch (IOException e) {
            log.warn("Failed to walk temp directory for deletion: {}", directory, e);
        }
    }
}
