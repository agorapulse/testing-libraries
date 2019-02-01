package com.agorapulse.testing.officeunit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public enum OfficeUnitDifferenceCollector implements DifferenceCollector {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(OfficeUnitDifferenceCollector.class);

    @Override
    public List<DocumentDifference> computeDifferences(String path, InputStream actualStream, InputStream expectedStream, Set<String> ignored) {
        try {
            Path actualFileDirectory = Files.createTempDirectory("actual");
            File actualFile = new File(actualFileDirectory.toFile(), path);
            Files.createDirectories(actualFile.getParentFile().toPath());
            Files.copy(actualStream, actualFile.toPath());
            Path expectedFileDirectory = Files.createTempDirectory("expected");
            File expectedFile = new File(expectedFileDirectory.toFile(), path);
            Files.createDirectories(expectedFile.getParentFile().toPath());
            Files.copy(expectedStream, expectedFile.toPath());
            return new OfficeUnit(path, ignored).compare(actualFile, expectedFile);
        } catch (IOException e) {
            LOGGER.error("Error evaluating nested office document differences", e);
        }
        return Collections.emptyList();
    }
}
