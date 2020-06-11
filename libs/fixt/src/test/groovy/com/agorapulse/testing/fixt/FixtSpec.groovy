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
package com.agorapulse.testing.fixt

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.util.environment.RestoreSystemProperties

@RestoreSystemProperties
class FixtSpec extends Specification {

    public static final String TEXT_FILE = 'text.txt'
    public static final String TEXT_CONTENT = 'Hello Text\n'
    public static final String STREAM_FILE = 'stream.txt'
    public static final String STREAM_CONTENT = 'Hello Stream\n'

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    @Rule
    Fixt fixt = Fixt.create(this)                                                       // <1>

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

            File contextTestFolder = new File(testResources, 'com/agorapulse/testing/fixt/ReferenceClass')
        then:
            contextTestFolder.exists()
            new File(contextTestFolder, STREAM_FILE).text == STREAM_CONTENT
            new File(contextTestFolder, TEXT_FILE).text == TEXT_CONTENT
    }

}
