package com.aleksandarsapic.llm_doc_generator.infrastructure.llm;

import org.springframework.stereotype.Component;

@Component
public class PromptTemplates {

    public String fileExplanationPrompt(String chunkedContent) {
        return """
                You are a senior software engineer tasked with explaining source code to other developers.
                Analyze the following source code and provide a clear, concise explanation covering:
                1. What this file/module does
                2. Key classes, functions, or components it defines
                3. How it fits into the overall system (if apparent)
                4. Any notable patterns, algorithms, or design choices

                Keep your explanation technical but approachable. Be concise (3-8 sentences).

                Source code:
                ```
                %s
                ```
                """.formatted(chunkedContent);
    }

    public String projectSummaryPrompt(String fileExplanations, String repositoryUrl) {
        return """
                You are a technical writer creating project documentation.
                Based on the following per-file explanations of a software project from %s,
                create a comprehensive project overview in Markdown format.

                Formatting rules:
                - Use ## for top-level sections (e.g. ## Project Overview)
                - Use ### for subsections
                - Use --- only to separate major sections, not between individual items or files
                - Do NOT include a file-by-file breakdown — that will be appended separately

                Include these sections:
                1. ## Project Overview — What this project does and its purpose
                2. ## Architecture — High-level structure, main components, and data/control flow
                3. ## Technology Stack — Languages, frameworks, and libraries used
                4. ## Entry Points — Where execution begins and how to use the project (setup steps, main API endpoints, etc.)

                File explanations:
                %s
                """.formatted(repositoryUrl, fileExplanations);
    }
}
