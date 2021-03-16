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
import com.agorapulse.testing.fixt.Fixt
import org.junit.ClassRule
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
