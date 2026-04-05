package com.aleksandarsapic.llm_doc_generator.infrastructure.filesystem;

import com.aleksandarsapic.llm_doc_generator.domain.port.FileTraverser;
import com.aleksandarsapic.llm_doc_generator.util.FileExtensionFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalFileTraverser implements FileTraverser {

    private final FileExtensionFilter extensionFilter;

    @Override
    public List<Path> traverse(Path rootDirectory) {
        log.info("Traversing directory: {}", rootDirectory);

        try (Stream<Path> walk = Files.walk(rootDirectory)) {
            List<Path> files = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> !extensionFilter.isInIgnoredDirectory(path, rootDirectory))
                    .filter(extensionFilter::isAllowed)
                    .sorted()
                    .toList();

            log.info("Found {} source files in {}", files.size(), rootDirectory);
            return files;
        } catch (IOException e) {
            log.error("Failed to traverse directory: {}", rootDirectory, e);
            return Collections.emptyList();
        }
    }
}
