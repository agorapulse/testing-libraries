package com.agorapulse.testing.spock

import spock.mock.DetachedMockFactory

class MyMockFactory {

    static Runnable mockRunnable() {
        new DetachedMockFactory().Mock(Runnable)
    }

}
