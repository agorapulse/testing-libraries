package com.agorapulse.testing.fixt;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Simple tool for managing test fixtures in folders which corresponds the package and the name of the specification class.
 */
public class Fixt implements TestRule {

    private static final String TEST_RESOURCES_FOLDER_PROPERTY_NAME = "testresourcesfolder";
    private static final String IGNORED_CHARACTERS = "[\\W_]";

    /**
     * Creates new Fixt for the given class.
     * @param clazz the given class
     * @return new Fixt for the given class
     */
    public static Fixt create(Class clazz) {
        return new Fixt(clazz);
    }

    private final Class clazz;

    private Fixt(Class clazz) {
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
     */
    public void writeStream(String fileName, InputStream stream) throws IOException {
        String path = getFixtureLocation(fileName);
        File file = new File(getTestResourcesLocation(), path);
        file.getParentFile().mkdirs();
        Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
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
    public String readText(String fileName) throws IOException {
        InputStream stream = readStream(fileName);
        if (stream == null) {
            return null;
        }

        return streamToText(stream);
    }

    /**
     * Saves the fixture to the appropriate location.
     *
     * The root for the fixtures is either specified with `TEST_RESOURCES_FOLDER` system property or defaults to
     * <code>src/test/resources</code>.
     *
     * @param fileName relative name of the file
     * @param text the text of the new file
     */
    public void writeText(String fileName, String text) throws IOException {
        writeStream(fileName, new ByteArrayInputStream(text.getBytes()));
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

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                mkdirs();
                base.evaluate();
            }
        };
    }

    private static String streamToText(InputStream stream) {
        String text = null;
        try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }
        return text;
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
