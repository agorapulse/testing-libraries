package com.agorapulse.testing.grails.job.test

import com.agorapulse.testing.grails.job.JobUnitTest
import org.quartz.SimpleTrigger
import spock.lang.Specification

@SuppressWarnings('ClassStartsWithBlankLine')
class TestJobSpec extends Specification implements JobUnitTest<TestJob> {               // <1>

    Runnable mock = Mock(Runnable)

    void setup() {
        job.runnable = mock                                                             // <2>
    }

    void 'test trigger job'() {
        when:
            trigger job                                                                 // <3>
        then:
            noExceptionThrown()

            1 * mock.run()
    }

    void 'check cofiguration'() {
        expect:
            triggers.any {                                                              // <4>
                it instanceof SimpleTrigger && it.repeatInterval == 1000
            }
            jobClass.concurrent                                                         // <5>
            jobClass.sessionRequired
            jobClass.description == 'Example job with Simple Trigger'
            jobClass.group == 'MyGroup'
    }

}
