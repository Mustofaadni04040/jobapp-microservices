package com.project.jobms.job.impl;
import com.project.jobms.job.Job;
import com.project.jobms.job.JobRepository;
import com.project.jobms.job.JobService;
import com.project.jobms.job.dto.JobWithCompanyDTO;
import com.project.jobms.job.external.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {
//    private final List<Job> jobs = new ArrayList<>();
    JobRepository jobRepository;
//    private Long nextId = 1L;
    @Autowired
    RestTemplate restTemplate;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<JobWithCompanyDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        List<JobWithCompanyDTO> jobWithCompanyDTOS = new ArrayList<>();


        return jobs.stream().map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void createJob(Job job) {
//        job.setId(nextId++);
        jobRepository.save(job);
    }

    @Override
    public JobWithCompanyDTO getJobById(Long id) {
        Job job =  jobRepository.findById(id).orElse(null);

        return convertToDto(job);
    }

    @Override
    public boolean deleteJobById(Long id) {
        try {
            jobRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateJob(Long id, Job jobToUpdate) {
        Optional<Job> jobOptional = jobRepository.findById(id);

        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setTitle(jobToUpdate.getTitle());
            job.setDescription(jobToUpdate.getDescription());
            job.setMinSalary(jobToUpdate.getMinSalary());
            job.setMaxSalary(jobToUpdate.getMaxSalary());
            job.setLocation(jobToUpdate.getLocation());
            jobRepository.save(job);
            return true;
        }
        return false;
    }

    private JobWithCompanyDTO convertToDto(Job job) {
            JobWithCompanyDTO jobWithCompanyDTO = new JobWithCompanyDTO();
            jobWithCompanyDTO.setJob(job);
//            RestTemplate restTemplate = new RestTemplate();

            Company company = restTemplate.getForObject("http://company-service/companies/" + job.getCompanyId(), Company.class);;
            jobWithCompanyDTO.setCompany(company);

            return jobWithCompanyDTO;
    }
}
