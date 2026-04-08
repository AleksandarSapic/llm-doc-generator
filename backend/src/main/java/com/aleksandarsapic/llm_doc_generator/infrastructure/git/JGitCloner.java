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

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class JGitCloner implements GitCloner {

    private final GitProperties gitProperties;

    @Override
    public CloningResult clone(String repositoryUrl, Path targetDirectory) {
        log.info("Cloning repository: {} into {}", repositoryUrl, targetDirectory);

        CloneCommand cloneCommand = Git.cloneRepository()
                .setURI(repositoryUrl)
                .setDirectory(targetDirectory.toFile())
                .setDepth(gitProperties.getCloneDepth())
                .setNoCheckout(false);

        try (Git git = cloneCommand.call()) {
            String commitSha = git.getRepository().resolve("HEAD").getName();
            log.info("Successfully cloned repository: {} at commit {}", repositoryUrl, commitSha.substring(0, 7));
            return new CloningResult(targetDirectory, commitSha);
        } catch (GitAPIException e) {
            throw new GitCloningException("Failed to clone repository: " + repositoryUrl, e);
        } catch (IOException e) {
            throw new GitCloningException("Failed to resolve HEAD commit for repository: " + repositoryUrl, e);
        }
    }
}
