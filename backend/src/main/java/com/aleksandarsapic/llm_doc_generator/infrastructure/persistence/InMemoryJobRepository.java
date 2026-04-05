package com.aleksandarsapic.llm_doc_generator.infrastructure.persistence;

import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;
import com.aleksandarsapic.llm_doc_generator.domain.port.JobRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Primary
@Repository
public class InMemoryJobRepository implements JobRepository {

    private final ConcurrentHashMap<String, DocJob> store = new ConcurrentHashMap<>();

    @Override
    public DocJob save(DocJob job) {
        store.put(job.getJobId(), job);
        return job;
    }

    @Override
    public Optional<DocJob> findById(String jobId) {
        return Optional.ofNullable(store.get(jobId));
    }

    @Override
    public List<DocJob> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(String jobId) {
        store.remove(jobId);
    }
}
