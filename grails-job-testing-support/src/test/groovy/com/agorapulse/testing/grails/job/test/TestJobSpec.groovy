package com.agorapulse.testing.grails.job.test

import com.agorapulse.testing.grails.job.JobUnitTest
import org.quartz.SimpleTrigger
import spock.lang.Specification

class TestJobSpec extends Specification implements JobUnitTest<TestJob> {

    Runnable mock = Mock(Runnable)

    void setup() {
        job.runnable = mock
    }

    void 'test trigger job'() {
        when:
            trigger job
        then:
            noExceptionThrown()

            1 * mock.run()
    }

    void 'check cofiguration'() {
        expect:
            triggers.any {
                it instanceof SimpleTrigger && it.repeatInterval == 1000
            }
            jobClass.concurrent
            jobClass.sessionRequired
            jobClass.description == 'Example job with Simple Trigger'
            jobClass.group == 'MyGroup'
    }

}
