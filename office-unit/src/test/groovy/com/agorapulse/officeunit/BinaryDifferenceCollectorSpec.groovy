package com.agorapulse.officeunit

import spock.lang.Specification

class BinaryDifferenceCollectorSpec extends Specification {

    void 'return zero on error'() {
        given:
            InputStream one = Mock(InputStream) {
                read(*_) >> { throw new IOException() }
            }
            InputStream two = Mock(InputStream) {
                read(*_) >> { throw new IOException() }
            }
        expect:
            BinaryDifferenceCollector.INSTANCE.computeDifferences(
                '/root',
                one,
                two,
                Collections.emptySet()
            ).size() == 0
    }

    void 'no difference for same input stream'() {
        given:
            InputStream one = new ByteArrayInputStream(new byte[0])
        expect:
            BinaryDifferenceCollector.INSTANCE.computeDifferences(
                '/root',
                one,
                one,
                Collections.emptySet()
            ).size() == 0
    }

    void 'to string'() {
        expect:
            BinaryDifferenceCollector.INSTANCE.toString() == 'Binary difference'
    }

}
