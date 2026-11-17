package Sprint1.demo.service;

import Sprint1.demo.model.*;
import Sprint1.demo.repository.*;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobPostingService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    // Constructor Injection
    public JobPostingService(
            UserRepository userRepository,
            JobRepository jobRepository,
            JobApplicationRepository jobApplicationRepository) {

        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    // ==================================================
    // CREATE JOB POSTING
    // ==================================================

    @Transactional
    public JobListing createJobListing(
            JobListingForm form,
            String identifyingEmail) {

        // Find the recruiter
        User user = userRepository.findByEmail(identifyingEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Recruiter not found"));

        // Only recruiters can create job postings
        if (!"RECRUITER".equals(user.getRole())) {
            throw new IllegalArgumentException(
                    "Only recruiters can create job postings");
        }

        // Create the job object
        JobListing job = new JobListing();

        // Set job information
        job.setTitle(form.getTitle());
        job.setJobType(form.getJobType());
        job.setCompanyName(form.getCompanyName());
        job.setLocation(form.getLocation());
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

        // Associate the recruiter with the job
        job.setRecruiter(user);

        // NEW: Associate the company if it exists
        if (user.getCompany() != null) {

            job.setCompany(user.getCompany());

            // Use the registered company name
            job.setCompanyName(
                    user.getCompany().getName());
        }

        // Save the job in MySQL
        return jobRepository.save(job);
    }

    // ==================================================
    // GET JOB POSTINGS CREATED BY RECRUITER
    // ==================================================

    public List<JobListing> getJobListingsForRecruiter(
            String recruiterEmail) {

        if (recruiterEmail == null) {
            return List.of();
        }

        return jobRepository
                .findByRecruiter_EmailAndDeletedFalseOrderByPostedDateDescIdDesc(
                        recruiterEmail);
    }

    // ==================================================
    // GET AVAILABLE JOB POSTINGS FOR JOB SEEKERS
    // ==================================================

    public List<JobListing> getAvailableJobListings(
            String applicantEmail) {

        if (applicantEmail == null) {
            return List.of();
        }

        return jobRepository
                .findAvailableForApplicant(applicantEmail);
    }

    // ==================================================
    // GET A SINGLE JOB POSTING
    // ==================================================

    public Optional<JobListing> getJobListing(Long jobId) {

        return jobRepository.findByIdAndDeletedFalse(jobId);
    }

    // ==================================================
    // GET JOB POSTING OWNED BY RECRUITER
    // ==================================================

    public Optional<JobListing> getOwnedJobListing(
            Long jobId,
            String recruiterEmail) {

        if (jobId == null || recruiterEmail == null) {
            return Optional.empty();
        }
        return jobRepository.findByIdAndRecruiter_EmailAndDeletedFalse(jobId, recruiterEmail);
    }

    // ==================================================
    // CONVERT JOB LISTING TO EDIT FORM
    // ==================================================

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

    // ==================================================
    // UPDATE AN EXISTING JOB POSTING
    // ==================================================

    @Transactional
    public JobListing updateJobListing(
            JobListing job,
            JobListingForm form) {

        // Update job information
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

        // NEW: Keep the company relationship updated
        User recruiter = job.getRecruiter();

        if (recruiter != null &&
                recruiter.getCompany() != null) {

            job.setCompany(recruiter.getCompany());

            job.setCompanyName(
                    recruiter.getCompany().getName());
        }

        // Save updated job
        return jobRepository.save(job);
    }

    // ==================================================
    // DELETE / ARCHIVE JOB POSTING
    // ==================================================

    @Transactional
    public void deleteJobListing(JobListing job) {

        // Find applications submitted for this job
        List<JobApplication> applications = jobApplicationRepository.findByJobListing_Id(job.getId());
        // Reject all applications
        for (JobApplication application : applications) {
            application.setStatus("Rejected");
        }

        // Save updated application statuses
        jobApplicationRepository.saveAll(applications);

        // Soft delete the job
        job.setDeleted(true);
        jobRepository.save(job);
    }
}
