package com.aleksandarsapic.llm_doc_generator.api.validation;

public class PromptTemplateValidator {

    public static void validateFileExplanationTemplate(String template) {
        if (template == null || template.isBlank()) return;
        int count = countPlaceholders(template);
        if (count != 1) {
            throw new IllegalArgumentException(
                    "fileExplanationTemplate must contain exactly 1 '%s' placeholder " +
                    "(for file content), but found " + count);
        }
    }

    public static void validateProjectSummaryTemplate(String template) {
        if (template == null || template.isBlank()) return;
        int count = countPlaceholders(template);
        if (count != 2) {
            throw new IllegalArgumentException(
                    "projectSummaryTemplate must contain exactly 2 '%s' placeholders " +
                    "(first: repositoryUrl, second: fileExplanations), but found " + count);
        }
    }

    private static int countPlaceholders(String template) {
        int count = 0;
        int idx = 0;
        while ((idx = template.indexOf("%s", idx)) != -1) {
            count++;
            idx += 2;
        }
        return count;
    }
}
