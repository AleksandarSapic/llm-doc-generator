package com.aleksandarsapic.llm_doc_generator.domain.model;

public enum DocJobStatus {
    PENDING,
    CLONING,
    TRAVERSING,
    PROCESSING,
    AGGREGATING,
    COMPLETED,
    FAILED;

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }
}
