package com.agorapulse.quartz.test

import grails.plugins.quartz.*
import grails.plugins.quartz.listeners.SessionBinderJobListener
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.support.DatastorePersistenceContextInterceptor
import org.grails.testing.ParameterizedGrailsUnitTest
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.KeyMatcher
import spock.util.concurrent.BlockingVariable

import java.util.concurrent.TimeUnit

@CompileStatic
trait JobUnitTest<J> extends ParameterizedGrailsUnitTest<J> {

    private GrailsJobClass _jobClass
    private List<Trigger> _triggers

    J getJob() {
        artefactInstance
    }

    GrailsJobClass getJobClass() {
        if (_jobClass == null) {
            _jobClass = new DefaultGrailsJobClass(job.getClass())
        }
        return _jobClass
    }

    @CompileDynamic
    List<Trigger> getTriggers() {
        if (_triggers == null) {
            JobDetail jobDetail = createJobDetail()
            _triggers = jobClass.triggers.collect { name, Expando descriptor ->
                CustomTriggerFactoryBean factory = new CustomTriggerFactoryBean()
                factory.triggerClass = descriptor.triggerClass
                factory.triggerAttributes = descriptor.triggerAttributes
                factory.jobDetail = jobDetail
                factory.afterPropertiesSet()
                return factory.object
            }
        }
        return _triggers
    }

    @CompileDynamic @Override
    void mockArtefact(Class<?> artefactClass) {
        defineBeans {
            "${getBeanName(artefactClass)}"(artefactClass)
        }
    }

    @Override
    String getBeanName(Class<?> artefactClass) {
        typeUnderTest.name
    }

    @SuppressWarnings('UnusedMethodParameter')
    boolean trigger(J job) {
        JobDetail jobDetail = createJobDetail()

        SchedulerFactory schedulerFactory = new StdSchedulerFactory()
        // Retrieve a scheduler from schedule factory
        Scheduler scheduler = schedulerFactory.scheduler
        scheduler.jobFactory = new GrailsJobFactory(applicationContext: applicationContext)

        definePersistentInterceptor(scheduler, jobDetail)

        BlockingVariable<JobExecutionException> exception = new BlockingVariable<>(1, TimeUnit.MINUTES)

        scheduler.listenerManager.addJobListener(new JobExceptionListener({ exception.set(it) }))

        // adds the job to the scheduler, and associates triggers with it
        scheduler.addJob(jobDetail, true)

        scheduler.start()

        scheduler.triggerJob(jobDetail.key)

        JobExecutionException ex = exception.get()

        scheduler.shutdown(true)

        if (ex) {
            if (ex.cause) {
                throw ex.cause
            }
            throw ex
        }

        return true
    }

    private JobDetail createJobDetail() {
        // Creates job details
        JobDetailFactoryBean jdfb = new JobDetailFactoryBean()
        jdfb.jobClass = jobClass
        jdfb.afterPropertiesSet()
        return jdfb.object
    }

    @CompileDynamic
    private void definePersistentInterceptor(Scheduler scheduler, JobDetail jobDetail) {
        defineBeans {
            persistenceInterceptor(DatastorePersistenceContextInterceptor, ref('grailsDatastore'))
            "${SessionBinderJobListener.NAME}"(SessionBinderJobListener) { bean ->
                bean.autowire = 'byName'
            }
        }
        SessionBinderJobListener listener = applicationContext.getBean(SessionBinderJobListener)
        ListenerManager listenerManager = scheduler.getListenerManager()
        KeyMatcher<JobKey> matcher = KeyMatcher.keyEquals(jobDetail.key)
        if (listenerManager.getJobListener(listener.getName()) == null) {
            listenerManager.addJobListener(listener, matcher)
        } else {
            listenerManager.addJobListenerMatcher(listener.getName(), matcher)
        }
    }
}
