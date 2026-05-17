package com.project.jobms.job.exception;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(Long jobId) {
        super("Job not found with id: " + jobId);
    }
}
