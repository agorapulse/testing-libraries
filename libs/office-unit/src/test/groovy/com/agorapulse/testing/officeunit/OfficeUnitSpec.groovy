/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2024 Agorapulse.
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
import spock.lang.Unroll

@Unroll class OfficeUnitSpec extends Specification {

    // tag::differences[]
    void 'no differences: #control with #test'() {
        expect:
            new OfficeUnit().compare(                                                   // <1>
                loadFile(control),
                loadFile(test)
            ).empty
        where:
            control         | test
            'test1.pptx'    | 'test2.pptx'
            'test1.xlsx'    | 'test2.xlsx'
    }
    // end::differences[]

    // tag::differences-count[]
    void '#differencesCount difference(s): #control with #test'() {
        given:
            OfficeUnit officeUnit = new OfficeUnit()
        expect:
            officeUnit.compare(                                                         // <2>
                    loadFile(control),
                    loadFile(test)
            ).size() == differencesCount
        where:
            control         | test              | differencesCount
            'test1.xlsx'    | 'test3.xlsx'      | 1
            'test1.xlsx'    | 'test4.xlsx'      | 8
            'test1.xlsx'    | 'test5.xlsx'      | 159
            'test1.pptx'    | 'test3.pptx'      | 2469
    }
    // end::differences-count[]

    void '#differencesCount difference(s): #control with #test (using difference collector)'() {
        given:
            OfficeUnit officeUnit = new OfficeUnit()
        expect:
            OfficeUnitDifferenceCollector.INSTANCE.computeDifferences(
                "/nested/$control",
                loadFile(control).newInputStream(),
                loadFile(test).newInputStream(),
                officeUnit.ignored
            ).size() == differencesCount

        where:
            control         | test              | differencesCount
            'test1.xlsx'    | 'test3.xlsx'      | 1
            'test1.xlsx'    | 'test4.xlsx'      | 8
            'test1.xlsx'    | 'test5.xlsx'      | 159
            'test1.pptx'    | 'test3.pptx'      | 2469
    }

    // tag::ignore[]
    void 'ignore some other part'() {
        given:
            OfficeUnit officeUnit = new OfficeUnit()
                .ignore('/sst[1]/si[3]/t[1]/text()[1]')                                 // <3>
        expect:
            officeUnit.compare(
                loadFile('test1.xlsx'),
                loadFile('test3.xlsx')
            ).empty
    }
    // end::ignore[]

    void 'xml difference is readable'() {
        expect:
            OfficeUnit officeUnit = new OfficeUnit()

            officeUnit.compare(
                loadFile('test1.xlsx'),
                loadFile('test3.xlsx')
            ).first().toString().startsWith('Path: /xl/sharedStrings.xml')
    }

    void 'file must exist'() {
        when:
            new OfficeUnit().compare(new File('no-such.file'), new File('noting.here'))
        then:
            thrown(IllegalStateException)
    }

    void 'file must be archive'() {
        when:
            new OfficeUnit().compare(
                loadFile('test1.xlsx'),
                loadFile('not-a.zip')
            )
        then:
            thrown(IllegalArgumentException)
    }

    private static File loadFile(String name) {
        URL resource = OfficeUnitSpec.getResource(name)
        assert resource
        return new File(resource.toURI())
    }

}
