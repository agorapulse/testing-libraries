package com.agorapulse.testing.grails.job.test

@SuppressWarnings('FieldTypeRequired')
class TestJob {

    static triggers = {
        simple name: 'mySimpleTrigger', startDelay: 60000, repeatInterval: 1000
    }

    static sessionRequired = true
    static concurrent = true
    static group = 'MyGroup'
    static description = 'Example job with Simple Trigger'

    Runnable runnable

    void execute() {
        runnable.run()
    }

}
