package com.aleksandarsapic.llm_doc_generator.infrastructure.aggregation;

import com.aleksandarsapic.llm_doc_generator.domain.model.FileExplanation;
import com.aleksandarsapic.llm_doc_generator.domain.model.ProjectDocumentation;
import com.aleksandarsapic.llm_doc_generator.domain.port.DocumentationAggregator;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class MarkdownAggregator implements DocumentationAggregator {

    @Override
    public ProjectDocumentation aggregate(String jobId, String repositoryUrl,
                                          List<FileExplanation> explanations,
                                          String projectSummary) {
        StringBuilder markdown = new StringBuilder();

        markdown.append("# Project Documentation\n\n");
        markdown.append("**Repository:** ").append(repositoryUrl).append("\n\n");
        markdown.append("**Generated:** ").append(Instant.now()).append("\n\n");
        markdown.append("---\n\n");

        markdown.append(projectSummary).append("\n\n");
        markdown.append("---\n\n");

        markdown.append("## File-by-File Reference\n\n");
        for (FileExplanation explanation : explanations) {
            markdown.append("### `").append(explanation.getFilePath()).append("`\n\n");
            markdown.append(explanation.getExplanation()).append("\n\n");
        }

        return ProjectDocumentation.builder()
                .jobId(jobId)
                .repositoryUrl(repositoryUrl)
                .markdownContent(markdown.toString())
                .fileExplanations(explanations)
                .generatedAt(Instant.now())
                .build();
    }
}
