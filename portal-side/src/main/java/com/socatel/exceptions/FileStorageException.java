package com.socatel.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStorageException extends RuntimeException {
    private Logger logger = LoggerFactory.getLogger(FileStorageException.class);

    public FileStorageException(String s, Exception ex) {
        super(s, ex);
        logger.error("File Storage Exception " + s);
    }

    public FileStorageException(String s) {
        super(s);
    }
}
