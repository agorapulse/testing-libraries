package com.agorapulse.fixt

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@RestoreSystemProperties
class FixtSpec extends Specification {

    public static final String TEXT_FILE = 'text.txt'
    public static final String TEXT_CONTENT = 'Hello Text'
    public static final String STREAM_FILE = 'stream.txt'
    public static final String STREAM_CONTENT = 'Hello Stream'

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    @Rule
    Fixt fixt = Fixt.create(FixtSpec)                                                   // <1>

    void 'reading existing files'() {
        expect:
            fixt.readStream(STREAM_FILE).text == STREAM_CONTENT                         // <2>
            fixt.readText(TEXT_FILE) == TEXT_CONTENT                                    // <3>
    }

    void 'writing files'() {
        when:
            Fixt fixt = Fixt.create(ReferenceClass)                                     // <4>
            File testResources = tmp.newFolder()

            System.setProperty('test.resources.folder', testResources.canonicalPath)

            fixt.mkdirs()                                                               // <5>
        then:
            Fixt.testResourcesLocation == testResources.canonicalPath

        when:
            ByteArrayInputStream stream = new ByteArrayInputStream(STREAM_CONTENT.bytes)
            fixt.writeStream(STREAM_FILE, stream)                                       // <6>
            fixt.writeText(TEXT_FILE, TEXT_CONTENT)                                     // <7>

            File contextTestFolder = new File(testResources, 'com/agorapulse/fixt/ReferenceClass')
        then:
            contextTestFolder.exists()
            new File(contextTestFolder, STREAM_FILE).text == STREAM_CONTENT
            new File(contextTestFolder, TEXT_FILE).text == TEXT_CONTENT
    }

}
