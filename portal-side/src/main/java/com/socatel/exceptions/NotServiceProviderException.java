package com.socatel.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotServiceProviderException extends RuntimeException {
    private Logger logger = LoggerFactory.getLogger(MyFileNotFoundException.class);

    public NotServiceProviderException(String s) {
        super(s);
        logger.error("Not Service Provider " + s);
    }
}
