package com.agorapulse.testing.spock

import spock.lang.Specification

class DetachedSpec extends Specification {

    @Detached Runnable runnable = MyMockFactory.mockRunnable()

    void 'mock is attached'() {
        when:
            runnable.run()
        then:
            1 * runnable.run()
    }

}
