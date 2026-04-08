CREATE TABLE doc_jobs (
    job_id            VARCHAR(36)   NOT NULL PRIMARY KEY,
    repository_url    VARCHAR(2048) NOT NULL,
    git_commit_sha    VARCHAR(40),
    status            VARCHAR(32)   NOT NULL,
    status_message    TEXT,
    total_files       INTEGER       NOT NULL DEFAULT 0,
    processed_files   INTEGER       NOT NULL DEFAULT 0,
    error_message     TEXT,
    llm_provider      VARCHAR(64),
    llm_model         VARCHAR(128),
    result_json       TEXT,
    created_at        TIMESTAMPTZ   NOT NULL,
    updated_at        TIMESTAMPTZ   NOT NULL
);

CREATE INDEX idx_doc_jobs_status ON doc_jobs (status);
CREATE INDEX idx_doc_jobs_repo_sha_status
    ON doc_jobs (repository_url, git_commit_sha, status)
    WHERE git_commit_sha IS NOT NULL;
