package com.agorapulse.officeunit;

import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import static com.google.common.base.Preconditions.checkState;

public class OfficeUnit {

    private static final Set<String> DEFAULT_IGNORED = new HashSet<>();

    private static final ImmutableMap<String, DifferenceCollector> COLLECTORS = ImmutableMap.<String, DifferenceCollector>builder()
            .put("xml", XmlDifferenceCollector.INSTANCE)
            .put("ref", XmlDifferenceCollector.INSTANCE)
            .put("png", BinaryDifferenceCollector.INSTANCE)
            .put("jpg", BinaryDifferenceCollector.INSTANCE)
            .put("jpeg", BinaryDifferenceCollector.INSTANCE)
            .put("bin", BinaryDifferenceCollector.INSTANCE)
            .put("pptx", OfficeUnitDifferenceCollector.INSTANCE)
            .put("xlsx", OfficeUnitDifferenceCollector.INSTANCE)
            .build();

    static {
        DEFAULT_IGNORED.add("/modified[1]/text()[1]");
        DEFAULT_IGNORED.add("/created[1]/text()[1]");
    }

    private final String prefix;
    private final Set<String> ignored;

    public OfficeUnit(String prefix, Set<String> ignored) {
        this.prefix = prefix;
        this.ignored = ignored;
    }

    public OfficeUnit() {
        this("", DEFAULT_IGNORED);
    }

    public List<DocumentDifference> compare(File actual, File expected) throws IOException {
        checkState(actual.exists(), "File " + actual.getCanonicalPath() + " does not exist!");
        checkState(expected.exists(), "File " + expected.getCanonicalPath() + " does not exist!");

        try (ZipFile actualZip = new ZipFile(actual);
             ZipFile expectedZip = new ZipFile(expected)
        ) {
            Enumeration<? extends ZipEntry> actualEntries = actualZip.entries();

            List<DocumentDifference> differences = new ArrayList<>();

            while (actualEntries.hasMoreElements()) {
                ZipEntry actualEntry = actualEntries.nextElement();
                if (actualEntry.isDirectory()) {
                    continue;
                }

                ZipEntry other = expectedZip.getEntry(actualEntry.getName());

                if (other == null) {
                    differences.add(new SimpleDocumentDifference(actualEntry.getName()));
                    continue;
                }

                String path = other.getName();

                InputStream actualStream = actualZip.getInputStream(actualEntry);
                InputStream expectedStream = expectedZip.getInputStream(other);

                String ext = path.substring(path.lastIndexOf('.') + 1, path.length());

                differences.addAll(COLLECTORS.getOrDefault(ext, EmptyDifferenceCollector.INSTANCE).computeDifferences(prefix + "/" + path, actualStream, expectedStream, ignored));

            }

            return differences;
        } catch(ZipException e) {
            throw new IllegalArgumentException("Error processing differences for files: " + actual.getCanonicalPath() + " and " + expected.getCanonicalPath(), e);
        }
    }
}
