package com.aleksandarsapic.llm_doc_generator.domain.port;

import com.aleksandarsapic.llm_doc_generator.domain.model.DocJob;

import java.util.List;
import java.util.Optional;

public interface JobRepository {
    DocJob save(DocJob job);
    Optional<DocJob> findById(String jobId);
    List<DocJob> findAll();
    void deleteById(String jobId);
}
