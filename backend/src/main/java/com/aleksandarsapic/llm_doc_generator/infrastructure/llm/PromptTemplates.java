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

                Include:
                1. **Project Overview** - What this project does
                2. **Architecture** - High-level structure and main components
                3. **Key Components** - The most important files/modules and their roles
                4. **Technology Stack** - Languages, frameworks, and libraries used
                5. **Entry Points** - Where execution begins or how to use the project

                File explanations:
                %s
                """.formatted(repositoryUrl, fileExplanations);
    }
}
