/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2020 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.testing.officeunit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class OfficeUnit {

    private static final Set<String> DEFAULT_IGNORED = new HashSet<>();

    private static final Map<String, DifferenceCollector> COLLECTORS = new HashMap<>();

    static {
        DEFAULT_IGNORED.add("/modified[1]/text()[1]");
        DEFAULT_IGNORED.add("/created[1]/text()[1]");
        COLLECTORS.put("xml", XmlDifferenceCollector.INSTANCE);
        COLLECTORS.put("ref", XmlDifferenceCollector.INSTANCE);
        COLLECTORS.put("png", BinaryDifferenceCollector.INSTANCE);
        COLLECTORS.put("jpg", BinaryDifferenceCollector.INSTANCE);
        COLLECTORS.put("jpeg", BinaryDifferenceCollector.INSTANCE);
        COLLECTORS.put("bin", BinaryDifferenceCollector.INSTANCE);
        COLLECTORS.put("pptx", OfficeUnitDifferenceCollector.INSTANCE);
        COLLECTORS.put("xlsx", OfficeUnitDifferenceCollector.INSTANCE);
    }

    private final String prefix;
    private final Set<String> ignored;

    public OfficeUnit(String prefix, Set<String> ignored) {
        this.prefix = prefix;
        this.ignored = ignored;
    }

    public OfficeUnit() {
        this("", new HashSet<>(DEFAULT_IGNORED));
    }

    public OfficeUnit ignore(String... ignored) {
        this.ignored.addAll(Arrays.asList(ignored));
        return this;
    }

    public Set<String> getIgnored() {
        return Collections.unmodifiableSet(ignored);
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
                if (!actualEntry.isDirectory()) {
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
            }

            return differences;
        } catch (ZipException e) {
            throw new IllegalArgumentException("Error processing differences for files: " + actual.getCanonicalPath() + " and " + expected.getCanonicalPath(), e);
        }
    }

    private static void checkState(boolean condition, String errorMessage) {
        if (!condition) {
            throw new IllegalStateException(errorMessage);
        }
    }
}
