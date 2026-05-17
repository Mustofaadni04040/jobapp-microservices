package com.project.jobms.job;

import com.project.jobms.job.dto.JobWithCompanyDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<JobWithCompanyDTO>> findAll() {
        List<JobWithCompanyDTO> jobs = jobService.findAll();

        if (jobs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(jobs);
    }

    @PostMapping
    public ResponseEntity<String> createJob(@RequestBody Job job) {
//        List<Company> companies = companyService.getAllCompanies();
//
//        if (companies.isEmpty()) {
//            return new ResponseEntity<>("No companies found. Please create a company before creating a job.", HttpStatus.BAD_REQUEST);
//        }
        jobService.createJob(job);
        return new ResponseEntity<>("Job created successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobWithCompanyDTO> getJobById(@PathVariable Long id) {
        JobWithCompanyDTO jobWithCompanyDTO = jobService.getJobById(id);

        if (jobWithCompanyDTO != null) {
            return ResponseEntity.ok(jobWithCompanyDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJobById(@PathVariable Long id) {
        boolean deleted = jobService.deleteJobById(id);

        if (deleted) {
            return new ResponseEntity<>("Job deleted successfully", HttpStatus.OK);
        }

        return new  ResponseEntity<>("No jobs found", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateJob(@PathVariable Long id, @RequestBody Job jobToUpdate) {
        boolean updated = jobService.updateJob(id, jobToUpdate);

        if (updated) {
            return new ResponseEntity<>("Job updated successfully", HttpStatus.OK);
        }

        return new ResponseEntity<>("No jobs found", HttpStatus.NOT_FOUND);
    }
}
