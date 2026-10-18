package Sprint1.demo.service;

import Sprint1.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

import Sprint1.demo.repository.*;
import Sprint1.demo.model.*;

@Service
public class JobPostingService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    public JobPostingService(UserRepository userRepository, JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.jobRepository=jobRepository;
    }



    @Transactional
    public JobListing createJobListing(JobListingForm form, String identifyingEmail) {
        JobListing job = new JobListing();
        job.setTitle(form.getTitle());
        job.setJobType(form.getJobType());
        job.setCompanyName(form.getCompanyName());
        job.setLocation(form.getLocation());
        job.setWorkMode(form.getWorkMode());
        job.setWorkMode(form.getWorkMode());
        job.setDescription(form.getDescription());
        job.setRequirement(form.getRequirement());
        job.setSkills(form.getSkills());
        job.setSalaryMin(form.getSalaryMin());
        job.setSalaryMax(form.getSalaryMax());
        job.setApplyUrl(form.getApplyUrl());
        job.setCoverLetterRequired(form.isCoverLetterRequired());
        job.setDeadline(form.getDeadline());


        if( userRepository.findByEmail(identifyingEmail).isPresent() ) {
            User user = userRepository.findByEmail(identifyingEmail).get();
            job.setRecruiter(user);
        }

        return jobRepository.save(job);
    }

    // US 3.5 (#37): job listings created by the logged-in recruiter, newest first.
    public List<JobListing> getJobListingsForRecruiter(String recruiterEmail) {
        if (recruiterEmail == null) {
            return List.of();
        }
        return jobRepository.findByRecruiter_EmailOrderByPostedDateDescIdDesc(recruiterEmail);
    }

}

