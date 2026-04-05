package com.aleksandarsapic.llm_doc_generator.domain.port;

import java.nio.file.Path;

public interface GitCloner {
    Path clone(String repositoryUrl, Path targetDirectory);
}
