package com.socatel.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupNotFoundException extends RuntimeException {
    private Logger logger = LoggerFactory.getLogger(GroupNotFoundException.class);

    public GroupNotFoundException(String s) {
        super(s);
        logger.warn("Group Not Found Exception " + s);
    }
}
