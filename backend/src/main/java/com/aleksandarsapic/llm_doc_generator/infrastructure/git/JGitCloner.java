package com.aleksandarsapic.llm_doc_generator.infrastructure.git;

import com.aleksandarsapic.llm_doc_generator.config.properties.GitProperties;
import com.aleksandarsapic.llm_doc_generator.domain.port.GitCloner;
import com.aleksandarsapic.llm_doc_generator.exception.GitCloningException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class JGitCloner implements GitCloner {

    private final GitProperties gitProperties;

    @Override
    public Path clone(String repositoryUrl, Path targetDirectory) {
        log.info("Cloning repository: {} into {}", repositoryUrl, targetDirectory);

        CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(repositoryUrl)
                .setDirectory(targetDirectory.toFile())
                .setDepth(gitProperties.getCloneDepth())
                .setNoCheckout(false);

        try (Git git = cloneCommand.call()) {
            log.info("Successfully cloned repository: {}", repositoryUrl);
            return targetDirectory;
        } catch (GitAPIException e) {
            throw new GitCloningException("Failed to clone repository: " + repositoryUrl, e);
        }
    }
}
