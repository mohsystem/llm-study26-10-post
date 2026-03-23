package com.um.springbootprojstructure.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromptLoggingConfig {

    private static final Logger promptLogger = LoggerFactory.getLogger("USER_PROMPT_LOGGER");

    @Value("${app.prompt.context:}")
    private String promptContext;

    @PostConstruct
    void logInitialContext() {
        if (promptContext != null && !promptContext.isBlank()) {
            promptLogger.info("Initial provided context:\n{}", promptContext);
        } else {
            promptLogger.info("Initial provided context: <empty>");
        }
    }
}
