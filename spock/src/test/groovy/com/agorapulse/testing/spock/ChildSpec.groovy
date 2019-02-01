package com.agorapulse.testing.spock

class ChildSpec extends ParentSpec {

    @OverrideParentFeatures                                                             // <1>
    void 'ignore me in child'() {
        expect:
            this.getClass() == ChildSpec || this.getClass() == GrandChildSpec
    }

}
