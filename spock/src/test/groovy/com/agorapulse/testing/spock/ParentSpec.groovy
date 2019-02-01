package com.agorapulse.testing.spock

import spock.lang.Specification

class ParentSpec extends Specification {

    void 'ignore me in child'() {                                                       // <1>
        expect:
            this.getClass() == ParentSpec || this.getClass() == GrandChildSpec
    }

    void 'ignore me in grandchild'() {
        expect:
            this.getClass() == ParentSpec || this.getClass() == ChildSpec
    }

}
