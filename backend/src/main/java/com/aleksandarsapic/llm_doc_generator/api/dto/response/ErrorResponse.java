package com.aleksandarsapic.llm_doc_generator.api.dto.response;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class ErrorResponse {
    int status;
    String error;
    String message;
    Instant timestamp;
}
