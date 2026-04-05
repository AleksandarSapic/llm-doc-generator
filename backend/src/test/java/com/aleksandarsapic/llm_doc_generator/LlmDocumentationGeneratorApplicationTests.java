package com.aleksandarsapic.llm_doc_generator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.ai.openai.api-key=test-key",
        "app.git.temp-base-dir=${java.io.tmpdir}"
})
class LlmDocumentationGeneratorApplicationTests {

    @Test
    void contextLoads() {
    }

}
