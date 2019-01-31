package com.agorapulse.officeunit

import spock.lang.Specification

class SimpleDocumentDifferenceSpec extends Specification {

    void 'to string'() {
        expect:
            new SimpleDocumentDifference('/some/path').toString() == 'Difference found: /some/path'
    }

}
