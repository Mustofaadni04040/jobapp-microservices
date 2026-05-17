package com.project.jobms.job.impl;
import com.project.jobms.job.Job;
import com.project.jobms.job.JobRepository;
import com.project.jobms.job.JobService;
import com.project.jobms.job.dto.JobDTO;
import com.project.jobms.job.exception.CompanyNotFoundException;
import com.project.jobms.job.exception.JobNotFoundException;
import com.project.jobms.job.external.Company;
import com.project.jobms.job.external.Review;
import com.project.jobms.job.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public List<JobDTO> findAll() {
        List<Job> jobs = jobRepository.findAll();
        List<JobDTO> jobDTOS = new ArrayList<>();


        return jobs.stream().map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void createJob(Job job) {
        try {
            restTemplate.getForObject(
                    "http://company-service/companies/" + job.getCompanyId(),
                    Company.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            throw new CompanyNotFoundException(job.getCompanyId());
        }

        jobRepository.save(job);
    }

    @Override
    public JobDTO getJobById(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException(id));

        try {
            return convertToDto(job);
        } catch (HttpClientErrorException.NotFound e) {
            throw new CompanyNotFoundException(job.getCompanyId());
        }
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

    private JobDTO convertToDto(Job job) {
//            RestTemplate restTemplate = new RestTemplate();
            Company company = restTemplate.getForObject("http://company-service/companies/" + job.getCompanyId(), Company.class);
            ResponseEntity<List<Review>> reviewResponse = restTemplate.exchange("http://review-service/reviews?companyId=" + job.getCompanyId(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {
            });
            List<Review> reviews = reviewResponse.getBody();
        //            jobDTO.setCompany(company);

            return JobMapper.mapToJobDTO(job, company, reviews);
    }
}
