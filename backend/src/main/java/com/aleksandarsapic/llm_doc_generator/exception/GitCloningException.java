package com.aleksandarsapic.llm_doc_generator.exception;

public class GitCloningException extends RuntimeException {
    public GitCloningException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitCloningException(String message) {
        super(message);
    }
}
