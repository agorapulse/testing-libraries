package com.agorapulse.quartz.test;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import java.util.function.Consumer;

public class JobExceptionListener implements JobListener {

    private final Consumer<JobExecutionException> finished;

    public JobExceptionListener(Consumer<JobExecutionException> finished) {
        this.finished = finished;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        // do nothing
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        // do nothing
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        finished.accept(jobException);
    }
}
