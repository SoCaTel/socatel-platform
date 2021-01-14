package com.socatel.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageException extends RuntimeException {
    private Logger logger = LoggerFactory.getLogger(ImageException.class);

    public ImageException(String s, Exception ex) {
        super(s, ex);
        logger.error("File Storage Exception " + s);
    }

    public ImageException(String s) {
        super(s);
    }
}
