/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
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
package com.agorapulse.testing.fixt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Simple tool for managing test fixtures in folders which corresponds the package and the name of the specification class.
 */
public class Fixt {

    private static final String TEST_RESOURCES_FOLDER_PROPERTY_NAME = "testresourcesfolder";
    private static final String IGNORED_CHARACTERS = "[\\W_]";

    /**
     * Creates new Fixt for the given object which might be a class.
     * @param object the given object which might be class
     * @return new Fixt for the given object
     */
    public static Fixt create(Object object) {
        Fixt fixt = new Fixt(object instanceof Class ? (Class<?>) object : object.getClass());
        fixt.mkdirs();
        return fixt;
    }

    /**
     * Creates new Fixt for the given class.
     * @param clazz the given class
     * @return new Fixt for the given class
     */
    public static Fixt create(Class<?> clazz) {
        Fixt fixt = new Fixt(clazz);
        fixt.mkdirs();
        return fixt;
    }

    private final Class<?> clazz;

    private Fixt(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * Find the location of the test resources folder.
     * @return the location of the test resources folder
     */
    public static String getTestResourcesLocation() {
        String testResourcesFolder = FileSystems.getDefault().getPath("src", "test", "resources").toString();
        for (Map.Entry<Object, Object> property : System.getProperties().entrySet()) {
            String canonicalKey = property.getKey().toString().replaceAll(IGNORED_CHARACTERS, "");
            if (canonicalKey.equalsIgnoreCase(TEST_RESOURCES_FOLDER_PROPERTY_NAME) && property.getValue() != null) {
                testResourcesFolder = property.getValue().toString();
            }
        }
        for (Map.Entry<String, String> property : System.getenv().entrySet()) {
            String canonicalKey = property.getKey().replaceAll(IGNORED_CHARACTERS, "");
            if (canonicalKey.equalsIgnoreCase(TEST_RESOURCES_FOLDER_PROPERTY_NAME) && property.getValue() != null) {
                testResourcesFolder = property.getValue();
            }
        }
        return testResourcesFolder;
    }

    /**
     * Creates the directory for the fixtures.
     *
     * The root for the fixtures is either specified with `TEST_RESOURCES_FOLDER` system property or defaults to
     * <code>src/test/resources</code>.
     */
    public boolean mkdirs() {
        String path = getFixtureLocation("");
        File file = new File(getTestResourcesLocation(), path);
        return file.mkdirs();
    }

    /**
     * Saves the fixture to the appropriate location.
     *
     * The root for the fixtures is either specified with `TEST_RESOURCES_FOLDER` system property or defaults to
     * <code>src/test/resources</code>.
     *
     * @param fileName relative name of the file
     * @param stream the stream with the content for the new file
     *
     * @return the newly created file
     */
    public File writeStream(String fileName, InputStream stream) throws IOException {
        String path = getFixtureLocation(fileName);
        File file = new File(getTestResourcesLocation(), path);
        file.getParentFile().mkdirs();
        Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return file;
    }

    /**
     * Returns the stream for given relative path.
     *
     * The file must be located in the directory package/as/path/NameOfTheSpec.
     *
     * @param fileName the relative file name
     * @return the stream representing the content of the given file
     */
    public InputStream readStream(String fileName) {
        InputStream fromClasspath = readStreamFromClasspath(fileName);

        if (fromClasspath != null) {
            return fromClasspath;
        }

        return readStreamFromFileSystem(fileName);
    }

    /**
     * Returns the stream for given relative path.
     *
     * The file must be located in the directory package/as/path/NameOfTheSpec.
     *
     * @param fileName the relative file name
     * @return the text stored in the given file
     */
    public String readText(String fileName) {
        InputStream stream = readStream(fileName);
        if (stream == null) {
            return null;
        }

        return streamToText(stream);
    }

    /**
     * Copies all files from the fixtureDirectoryPath into destinationDirectory.
     *
     * <p>
     *     The files are copied from the test resources folder such as <code>src/main/test/resources</code>.
     *     The current implementation does not support reading the directory content from the classpath (e.g. a different project)
     * </p>
     * @param fixtureDirectoryPath
     * @param destinationDirectory
     * @return
     */
    public File copyTo(String fixtureDirectoryPath, File destinationDirectory) {
        File testingDirectory = new File(getTestResourcesLocation(), getFixtureLocation(fixtureDirectoryPath));

        if (!testingDirectory.exists()) {
            if (testingDirectory.mkdirs()) {
                throw new IllegalArgumentException("The directory " + testingDirectory.getAbsolutePath() + " does not exist but it has been crated!"
                    + "Please, pay attention that the folder must exists in the very same project as the test");
            }
        }

        try {
            copyFolder(testingDirectory.toPath(), destinationDirectory.toPath());
            return testingDirectory;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to copy directory", e);
        }
    }

    /**
     * Saves the fixture to the appropriate location.
     *
     * The root for the fixtures is either specified with `TEST_RESOURCES_FOLDER` system property or defaults to
     * <code>src/test/resources</code>.
     *
     * @param fileName relative name of the file
     * @param text the text of the new file
     *
     * @return the newly created file
     */
    public File  writeText(String fileName, String text) throws IOException {
        return writeStream(fileName, new ByteArrayInputStream(text.getBytes()));
    }

    private String getFixtureLocation(String fileName) {
        Package pkg = clazz.getPackage();
        List<String> packagePath = pkg == null ? Collections.emptyList() : Arrays.asList(pkg.getName().split("\\."));
        List<String> pathElements = new ArrayList<>(packagePath);
        pathElements.add(clazz.getSimpleName());
        pathElements.add(fileName);
        String[] pathParams = pathElements.toArray(new String[0]);

        return FileSystems.getDefault().getPath("", pathParams).toString();
    }

    private static String streamToText(InputStream stream) {
        try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
            if (scanner.hasNext()) {
                return scanner.next();

            }
        }
        return "";
    }

    public static void copyFolder(Path source, Path target, CopyOption... options) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private InputStream readStreamFromClasspath(String fileName) {
        return clazz.getResourceAsStream(clazz.getSimpleName() + '/' + fileName);
    }

    private InputStream readStreamFromFileSystem(String fileName) {
        try {
            return new FileInputStream(new File(getTestResourcesLocation(), getFixtureLocation(fileName)));
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
