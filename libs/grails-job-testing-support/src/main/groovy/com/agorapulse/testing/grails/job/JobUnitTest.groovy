/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2018-2021 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agorapulse.testing.grails.job

import grails.plugins.quartz.GrailsJobClass
import grails.plugins.quartz.JobDetailFactoryBean
import grails.plugins.quartz.GrailsJobFactory
import grails.plugins.quartz.DefaultGrailsJobClass
import grails.plugins.quartz.CustomTriggerFactoryBean
import grails.plugins.quartz.listeners.SessionBinderJobListener
import grails.testing.gorm.DataTest
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.support.DatastorePersistenceContextInterceptor
import org.grails.testing.ParameterizedGrailsUnitTest
import org.quartz.Scheduler
import org.quartz.JobExecutionException
import org.quartz.SchedulerFactory
import org.quartz.Trigger
import org.quartz.JobKey
import org.quartz.ListenerManager
import org.quartz.JobDetail
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.KeyMatcher
import org.slf4j.LoggerFactory
import spock.util.concurrent.BlockingVariable

import java.util.concurrent.TimeUnit
import java.util.function.Consumer

@CompileStatic
trait JobUnitTest<J> implements ParameterizedGrailsUnitTest<J>, DataTest {

    @SuppressWarnings('FieldName')
    private GrailsJobClass _jobClass

    @SuppressWarnings('FieldName')
    private List<Trigger> _triggers

    J getJob() {
        return artefactInstance
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
        return typeUnderTest.name
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

        scheduler.listenerManager.addJobListener(new JobExceptionListener({ exception.set(it) } as Consumer<JobExecutionException>))

        // adds the job to the scheduler, and associates triggers with it
        scheduler.addJob(jobDetail, true)

        scheduler.start()

        scheduler.triggerJob(jobDetail.key)

        JobExecutionException ex = exception.get()

        scheduler.shutdown(true)

        if (ex) {
            LoggerFactory.getLogger(job.getClass()).error("Exception executing job $jobDetail", ex)
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
        ListenerManager listenerManager = scheduler.listenerManager
        KeyMatcher<JobKey> matcher = KeyMatcher.keyEquals(jobDetail.key)
        if (listenerManager.getJobListener(listener.name) == null) {
            listenerManager.addJobListener(listener, matcher)
        } else {
            listenerManager.addJobListenerMatcher(listener.name, matcher)
        }
    }

}
