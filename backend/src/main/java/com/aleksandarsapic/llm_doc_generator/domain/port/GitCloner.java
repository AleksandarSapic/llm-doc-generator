package com.aleksandarsapic.llm_doc_generator.domain.port;

import java.nio.file.Path;

public interface GitCloner {
    CloningResult clone(String repositoryUrl, Path targetDirectory);

    record CloningResult(Path directory, String commitSha) {}
}
