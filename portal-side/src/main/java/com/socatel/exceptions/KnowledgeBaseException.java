package com.socatel.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgeBaseException extends RuntimeException {
    private Logger logger = LoggerFactory.getLogger(KnowledgeBaseException.class);

    public KnowledgeBaseException(Exception e) {
        super(e);
        logger.error("Error occurred while connecting to the Knowledge Base");
    }
}