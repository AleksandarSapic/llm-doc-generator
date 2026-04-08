package com.aleksandarsapic.llm_doc_generator.service;

import com.aleksandarsapic.llm_doc_generator.api.dto.response.JobStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterRegistry {

    private final ConcurrentHashMap<String, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(String jobId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.computeIfAbsent(jobId, k -> ConcurrentHashMap.newKeySet()).add(emitter);

        Runnable cleanup = () -> removeEmitter(jobId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        log.debug("SSE client registered for job {}", jobId);
        return emitter;
    }

    public void broadcast(String jobId, JobStatusResponse payload) {
        Set<SseEmitter> set = emitters.get(jobId);
        if (set == null || set.isEmpty()) return;

        boolean isTerminal = payload.getStatus().isTerminal();
        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : set) {
            try {
                emitter.send(SseEmitter.event()
                        .name("status")
                        .data(payload, MediaType.APPLICATION_JSON));
                if (isTerminal) {
                    emitter.complete();
                }
            } catch (IOException e) {
                log.debug("SSE client disconnected for job {}, removing", jobId);
                deadEmitters.add(emitter);
            }
        }

        deadEmitters.forEach(dead -> removeEmitter(jobId, dead));

        if (isTerminal) {
            emitters.remove(jobId);
        }
    }

    public void evict(String jobId) {
        Set<SseEmitter> set = emitters.remove(jobId);
        if (set != null) {
            set.forEach(SseEmitter::complete);
        }
    }

    private void removeEmitter(String jobId, SseEmitter emitter) {
        Set<SseEmitter> set = emitters.get(jobId);
        if (set != null) {
            set.remove(emitter);
            if (set.isEmpty()) {
                emitters.remove(jobId, set);
            }
        }
    }
}
