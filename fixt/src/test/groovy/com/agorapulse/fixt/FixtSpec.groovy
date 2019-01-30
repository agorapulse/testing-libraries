package com.agorapulse.fixt

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@RestoreSystemProperties
class FixtSpec extends Specification {

    public static final String TEST_TEXT_FILE = 'text.txt'
    public static final String TEST_TEXT_CONTENT = 'Hello Text'
    public static final String TEST_STREAM_FILE = 'stream.txt'
    public static final String TEST_STREAM_CONTENT = 'Hello Stream'

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    @Rule
    Fixt fixt = Fixt.create(FixtSpec)

    void 'reading existing files'() {
        expect:
            fixt.readStream(TEST_STREAM_FILE).text == TEST_STREAM_CONTENT
            fixt.readText(TEST_TEXT_FILE) == TEST_TEXT_CONTENT
    }

    void 'writing files'() {
        when:
            Fixt fixt = Fixt.create(ReferenceClass)
            File testResources = tmp.newFolder()

            System.setProperty('test.resources.folder', testResources.canonicalPath)

            fixt.mkdirs()
        then:
            Fixt.testResourcesLocation == testResources.canonicalPath

        when:
            fixt.writeStream(TEST_STREAM_FILE, new ByteArrayInputStream(TEST_STREAM_CONTENT.bytes))
            fixt.writeText(TEST_TEXT_FILE, TEST_TEXT_CONTENT)

            File contextTestFolder = new File(testResources, 'com/agorapulse/fixt/ReferenceClass')
        then:
            contextTestFolder.exists()
            new File(contextTestFolder, TEST_STREAM_FILE).text == TEST_STREAM_CONTENT
            new File(contextTestFolder, TEST_TEXT_FILE).text == TEST_TEXT_CONTENT
    }

}
