package com.agorapulse.testing.spock

import spock.lang.Specification

@OverrideParentFeatures
class NoParentAndAnnoOnTypeSpec extends Specification {

    void 'can be declared even no parent exists'() {
        expect: true
    }

}
