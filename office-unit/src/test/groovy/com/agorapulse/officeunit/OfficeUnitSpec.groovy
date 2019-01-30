package com.agorapulse.officeunit

import spock.lang.Specification
import spock.lang.Unroll

@Unroll class OfficeUnitSpec extends Specification {

    void 'no differences: #control with #test'() {
        expect:
            new OfficeUnit().compare(
                loadFile(control),
                loadFile(test)
            ).empty
        where:
            control         | test
            'test1.pptx'    | 'test2.pptx'
            'test1.xlsx'    | 'test2.xlsx'
    }

    void '#differencesCount difference(s): #control with #test'() {
        expect:
            new OfficeUnit().compare(
                    loadFile(control),
                    loadFile(test)
            ).size() == differencesCount
        where:
            control         | test              | differencesCount
            'test1.xlsx'    | 'test3.xlsx'      | 1
            'test1.xlsx'    | 'test4.xlsx'      | 8
    }

    private static File loadFile(String name) {
        URL resource = OfficeUnitSpec.getResource(name)
        assert resource
        new File(resource.toURI())
    }

}
