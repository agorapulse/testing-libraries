package com.agorapulse.officeunit

import spock.lang.Specification

class EmptyDifferenceCollectorSpec extends Specification {

    void 'to string'() {
        expect:
            EmptyDifferenceCollector.INSTANCE.toString() == 'No difference'
    }

}
