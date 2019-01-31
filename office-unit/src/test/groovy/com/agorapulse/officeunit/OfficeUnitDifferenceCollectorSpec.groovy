package com.agorapulse.officeunit

import spock.lang.Specification

class OfficeUnitDifferenceCollectorSpec extends Specification {

    void 'return zero on error'() {
        given:
            InputStream one = Mock(InputStream) {
                read(*_) >> { throw new IOException() }
            }
            InputStream two = Mock(InputStream) {
                read(*_) >> { throw new IOException() }
            }
        expect:
            OfficeUnitDifferenceCollector.INSTANCE.computeDifferences(
                '/root',
                one,
                two,
                Collections.emptySet()
            ).size() == 0
    }

}
