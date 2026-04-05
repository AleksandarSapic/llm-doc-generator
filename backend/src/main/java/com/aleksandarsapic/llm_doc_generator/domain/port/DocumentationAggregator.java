package com.aleksandarsapic.llm_doc_generator.domain.port;

import com.aleksandarsapic.llm_doc_generator.domain.model.FileExplanation;
import com.aleksandarsapic.llm_doc_generator.domain.model.ProjectDocumentation;

import java.util.List;

public interface DocumentationAggregator {
    ProjectDocumentation aggregate(String jobId, String repositoryUrl, List<FileExplanation> explanations, String projectSummary);
}
