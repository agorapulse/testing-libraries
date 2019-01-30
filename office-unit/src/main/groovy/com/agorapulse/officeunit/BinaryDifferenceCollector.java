package com.agorapulse.officeunit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public enum BinaryDifferenceCollector implements DifferenceCollector {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(BinaryDifferenceCollector.class);

    public List<DocumentDifference> computeDifferences(String path, InputStream actualStream, InputStream expectedStream, Set<String> ignoredPaths) {
        try {
            boolean contentEquals = IOUtils.contentEquals(actualStream, expectedStream);
            if (!contentEquals) {
                return Collections.singletonList(new SimpleDocumentDifference(path));
            }
        } catch (IOException e){
            LOGGER.error("Exception comparing two binary files", e);
        }
        return Collections.emptyList();
    }

}
