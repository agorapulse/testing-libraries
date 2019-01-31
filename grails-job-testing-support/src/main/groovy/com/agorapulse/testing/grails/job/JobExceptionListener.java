package com.agorapulse.testing.grails.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

import java.util.function.Consumer;

public class JobExceptionListener extends JobListenerSupport {

    private final Consumer<JobExecutionException> finished;

    public JobExceptionListener(Consumer<JobExecutionException> finished) {
        this.finished = finished;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        finished.accept(jobException);
    }
}
