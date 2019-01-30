package com.agorapulse.officeunit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public enum EmptyDifferenceCollector implements DifferenceCollector {

    INSTANCE;

    @Override
    public List<DocumentDifference> computeDifferences(String path, InputStream actualStream, InputStream expectedStream, Set<String> ignored) {
        Logger log = LoggerFactory.getLogger(DifferenceCollector.class);
        if (log.isInfoEnabled()) {
            log.info("Skipping {}", path);
        }
        return Collections.emptyList();
    }
}
