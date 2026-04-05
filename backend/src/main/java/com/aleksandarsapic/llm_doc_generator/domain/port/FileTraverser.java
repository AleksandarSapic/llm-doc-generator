package com.aleksandarsapic.llm_doc_generator.domain.port;

import java.nio.file.Path;
import java.util.List;

public interface FileTraverser {
    List<Path> traverse(Path rootDirectory);
}
