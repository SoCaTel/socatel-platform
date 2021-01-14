package com.socatel.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyFileNotFoundException extends RuntimeException {
    private Logger logger = LoggerFactory.getLogger(MyFileNotFoundException.class);

    public MyFileNotFoundException(String s) {
        super(s);
        logger.error("File Not Found " + s);
    }
}
