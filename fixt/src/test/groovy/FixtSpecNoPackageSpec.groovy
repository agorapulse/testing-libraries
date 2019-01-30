import com.agorapulse.fixt.Fixt
import org.junit.ClassRule
import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

/**
 * Testing Fixt where the reference class has no
 */
class FixtSpecNoPackageSpec extends Specification {

    public static final String TEST_TEXT_FILE = 'text.txt'
    public static final String TEST_TEXT_CONTENT = 'Hello Text'
    public static final String TEST_STREAM_FILE = 'stream.txt'
    public static final String TEST_STREAM_CONTENT = 'Hello Stream'

    @ClassRule @Shared
    TemporaryFolder tmp = new TemporaryFolder()

    @ClassRule @Shared
    EnvironmentVariables environmentVariables = new EnvironmentVariables()

    @Rule
    Fixt fixt = Fixt.create(FixtSpecNoPackageSpec)

    void setupSpec() {
        File testResourcesFolder = tmp.newFolder()
        environmentVariables.set('TEST_RESOURCES_FOLDER', testResourcesFolder.canonicalPath)
    }

    void cleanupSpec() {
        new File(Fixt.testResourcesLocation, FixtSpecNoPackageSpec.simpleName).deleteDir()
    }

    void 'reading existing files'() {
        when:
            fixt.mkdirs()
            fixt.writeStream(TEST_STREAM_FILE, new ByteArrayInputStream(TEST_STREAM_CONTENT.bytes))
            fixt.writeText(TEST_TEXT_FILE, TEST_TEXT_CONTENT)
        then:
            fixt.readStream(TEST_STREAM_FILE).text == TEST_STREAM_CONTENT
            fixt.readText(TEST_TEXT_FILE) == TEST_TEXT_CONTENT

            !fixt.readStream(TEST_STREAM_FILE.reverse())
            !fixt.readText(TEST_TEXT_FILE.reverse())
    }

}
