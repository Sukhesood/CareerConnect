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
        job.setSalaryPeriod(form.getSalaryPeriod());
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

    public List<JobListing> getAvailableJobListings(String applicantEmail) {
        if (applicantEmail == null) {
            return List.of();
        }
        return jobRepository.findAvailableForApplicant(applicantEmail);
    }

    public java.util.Optional<JobListing> getJobListing(Long jobId) {
        return jobRepository.findById(jobId);
    }

    // US 3.3 (#35): a job listing, but only if the given recruiter created it
    public java.util.Optional<JobListing> getOwnedJobListing(Long jobId, String recruiterEmail) {
        if (jobId == null || recruiterEmail == null) {
            return java.util.Optional.empty();
        }
        return jobRepository.findByIdAndRecruiter_Email(jobId, recruiterEmail);
    }

    // US 3.3 (#35): copy a saved job listing into a form so the edit page opens pre-filled
    public JobListingForm toForm(JobListing job) {
        JobListingForm form = new JobListingForm();
        form.setTitle(job.getTitle());
        form.setCompanyName(job.getCompanyName());
        form.setLocation(job.getLocation());
        form.setWorkMode(job.getWorkMode());
        form.setJobType(job.getJobType());
        form.setDescription(job.getDescription());
        form.setRequirement(job.getRequirement());
        form.setSkills(job.getSkills());
        form.setSalaryMin(job.getSalaryMin());
        form.setSalaryMax(job.getSalaryMax());
        form.setSalaryPeriod(job.getSalaryPeriod());
        form.setApplyUrl(job.getApplyUrl());
        form.setCoverLetterRequired(job.isCoverLetterRequired());
        form.setDeadline(job.getDeadline());
        return form;
    }

    // US 3.3 (#35): save the recruiter's changes to an existing job listing
    @Transactional
    public JobListing updateJobListing(JobListing job, JobListingForm form) {
        job.setTitle(form.getTitle());
        job.setCompanyName(form.getCompanyName());
        job.setLocation(form.getLocation());
        job.setWorkMode(form.getWorkMode());
        job.setJobType(form.getJobType());
        job.setDescription(form.getDescription());
        job.setRequirement(form.getRequirement());
        job.setSkills(form.getSkills());
        job.setSalaryMin(form.getSalaryMin());
        job.setSalaryMax(form.getSalaryMax());
        job.setSalaryPeriod(form.getSalaryPeriod());
        job.setApplyUrl(form.getApplyUrl());
        job.setCoverLetterRequired(form.isCoverLetterRequired());
        job.setDeadline(form.getDeadline());
        return jobRepository.save(job);
    }

}

