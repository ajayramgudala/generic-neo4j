package com.example.genericneo4j.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {
    private static final Logger logger = LoggerFactory.getLogger("AUDIT");

    public void log(String action, String entity, Long id, String details) {
        logger.info("AUDIT | action={} | entity={} | id={} | details={}", action, entity, id, details);
    }
} 