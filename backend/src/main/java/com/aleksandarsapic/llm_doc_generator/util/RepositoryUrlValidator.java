package com.aleksandarsapic.llm_doc_generator.util;

import com.aleksandarsapic.llm_doc_generator.exception.GitCloningException;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validates repository URLs to prevent SSRF (Server-Side Request Forgery) attacks.
 * Rejects file://, non-http(s) schemes, localhost, and private IP ranges.
 */
@Component
public class RepositoryUrlValidator {

    private static final List<Pattern> PRIVATE_IP_PATTERNS = List.of(
            Pattern.compile("^10\\..*"),
            Pattern.compile("^172\\.(1[6-9]|2[0-9]|3[01])\\..*"),
            Pattern.compile("^192\\.168\\..*"),
            Pattern.compile("^127\\..*"),
            Pattern.compile("^169\\.254\\..*"),
            Pattern.compile("^::1$"),
            Pattern.compile("^fc.*"),
            Pattern.compile("^fd.*")
    );

    public void validate(String repositoryUrl) {
        URI uri;
        try {
            uri = URI.create(repositoryUrl);
        } catch (IllegalArgumentException e) {
            throw new GitCloningException("Invalid repository URL: " + repositoryUrl);
        }

        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
            throw new GitCloningException(
                    "Repository URL must use http or https scheme, got: " + scheme);
        }

        String host = uri.getHost();
        if (host == null || host.isBlank()) {
            throw new GitCloningException("Repository URL has no host");
        }

        if (host.equalsIgnoreCase("localhost")) {
            throw new GitCloningException("Repository URL must not point to localhost");
        }

        try {
            InetAddress address = InetAddress.getByName(host);
            String ip = address.getHostAddress();
            for (Pattern pattern : PRIVATE_IP_PATTERNS) {
                if (pattern.matcher(ip).matches()) {
                    throw new GitCloningException(
                            "Repository URL must not point to a private or reserved IP address");
                }
            }
        } catch (UnknownHostException e) {
            // Can't resolve — let JGit fail with a clear error
        }
    }
}
