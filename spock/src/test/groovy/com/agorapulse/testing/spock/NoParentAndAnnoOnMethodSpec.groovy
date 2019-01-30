package com.agorapulse.testing.spock

import spock.lang.Specification

class NoParentAndAnnoOnMethodSpec extends Specification {

    @OverrideParentFeatures
    void 'can be declared even no parent exists'() {
        expect: true
    }

}
