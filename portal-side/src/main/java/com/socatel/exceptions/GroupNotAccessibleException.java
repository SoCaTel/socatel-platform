package com.socatel.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupNotAccessibleException extends RuntimeException {
    private Logger logger = LoggerFactory.getLogger(GroupNotAccessibleException.class);
    public GroupNotAccessibleException(String s) {
        super(s);
        logger.error("Group Not Accessible Exception " + s);
    }
}
