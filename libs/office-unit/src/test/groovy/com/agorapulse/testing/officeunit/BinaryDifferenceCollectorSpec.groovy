/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2025 Agorapulse.
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
package com.agorapulse.testing.officeunit

import spock.lang.Specification

class BinaryDifferenceCollectorSpec extends Specification {

    void 'return zero on error'() {
        given:
            InputStream one = Mock(InputStream) {
                read(*_) >> { throw new IOException() }
            }
            InputStream two = Mock(InputStream) {
                read(*_) >> { throw new IOException() }
            }
        expect:
            BinaryDifferenceCollector.INSTANCE.computeDifferences(
                '/root',
                one,
                two,
                Collections.emptySet()
            ).size() == 0
    }

    void 'no difference for same input stream'() {
        given:
            InputStream one = new ByteArrayInputStream(new byte[0])
        expect:
            BinaryDifferenceCollector.INSTANCE.computeDifferences(
                '/root',
                one,
                one,
                Collections.emptySet()
            ).size() == 0
    }

    void 'to string'() {
        expect:
            BinaryDifferenceCollector.INSTANCE.toString() == 'Binary difference'
    }

}
