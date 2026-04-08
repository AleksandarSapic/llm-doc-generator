package com.aleksandarsapic.llm_doc_generator.api.controller;

import com.aleksandarsapic.llm_doc_generator.api.dto.response.JobStatusResponse;
import com.aleksandarsapic.llm_doc_generator.service.DocumentationOrchestrator;
import com.aleksandarsapic.llm_doc_generator.service.SseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobSseController {

    private final DocumentationOrchestrator orchestrator;
    private final SseEmitterRegistry sseEmitterRegistry;

    @GetMapping(value = "/{jobId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamJobStatus(@PathVariable String jobId) {
        JobStatusResponse snapshot = JobStatusResponse.from(orchestrator.getJob(jobId));

        // Register before emitting the snapshot — prevents the race where the job
        // completes between the DB read and the emitter registration
        SseEmitter emitter = sseEmitterRegistry.register(jobId);

        try {
            emitter.send(SseEmitter.event()
                    .name("status")
                    .data(snapshot, MediaType.APPLICATION_JSON));
        } catch (IOException e) {
            emitter.completeWithError(e);
            return emitter;
        }

        if (snapshot.getStatus().isTerminal()) {
            emitter.complete();
        }

        log.debug("SSE stream opened for job {}, current status: {}", jobId, snapshot.getStatus());
        return emitter;
    }
}
