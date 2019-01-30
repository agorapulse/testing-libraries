package com.agorapulse.fixt;

import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.*;
import java.util.Map;

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
        String testResourcesFolder = "src/test/resources";
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
        try (FileOutputStream out = new FileOutputStream(file)) {
            IOGroovyMethods.setBytes(out, IOGroovyMethods.getBytes(stream));
        }
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
        return IOGroovyMethods.getText(stream);
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
        String suffix = clazz.getSimpleName() + "/" + fileName;
        Package pkg = clazz.getPackage();
        if (pkg == null) {
            return suffix;
        }
        return pkg.getName().replaceAll("\\.", File.separator) + File.separator + suffix;
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

    private InputStream readStreamFromClasspath(String fileName) {
        return clazz.getResourceAsStream(clazz.getSimpleName() + "/" + fileName);
    }

    private InputStream readStreamFromFileSystem(String fileName) {
        try {
            return new FileInputStream(new File(getTestResourcesLocation(), getFixtureLocation(fileName)));
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
