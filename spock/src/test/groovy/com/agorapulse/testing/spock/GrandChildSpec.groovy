package com.agorapulse.testing.spock

@OverrideParentFeatures
class GrandChildSpec extends ParentSpec {

    void 'ignore me in grandchild'() {
        expect:
            this.getClass() == GrandChildSpec
    }

}
