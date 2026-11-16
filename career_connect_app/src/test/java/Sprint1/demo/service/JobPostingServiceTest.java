package Sprint1.demo.service;

import Sprint1.demo.model.JobApplication;
import Sprint1.demo.model.JobListing;
import Sprint1.demo.model.JobListingForm;
import Sprint1.demo.repository.JobApplicationRepository;
import Sprint1.demo.repository.JobRepository;
import Sprint1.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobPostingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @InjectMocks
    private JobPostingService jobPostingService;

    @Test
    void deletingAPostingArchivesItAndRejectsItsApplications() {
        JobListing job = new JobListing();
        job.setId(42L);
        JobApplication firstApplication = new JobApplication();
        firstApplication.setStatus("Applied");
        JobApplication secondApplication = new JobApplication();
        secondApplication.setStatus("Under Review");
        List<JobApplication> applications = List.of(firstApplication, secondApplication);

        when(jobApplicationRepository.findByJobListing_Id(42L)).thenReturn(applications);

        jobPostingService.deleteJobListing(job);

        assertTrue(job.isDeleted());
        assertEquals("Rejected", firstApplication.getStatus());
        assertEquals("Rejected", secondApplication.getStatus());
        verify(jobApplicationRepository).saveAll(applications);
        verify(jobRepository).save(job);
    }

    @Test
    void toFormCopiesAJobListingForRecruiterEditing() {
        JobListing job = new JobListing();
        job.setTitle("QA Automation Intern");
        job.setCompanyName("CareerConnect Labs");
        job.setLocation("Montreal");
        job.setWorkMode("Hybrid");
        job.setJobType("Internship");
        job.setDescription("Create automated tests.");
        job.setRequirement("Basic Java knowledge");
        job.setSkills("Java, JUnit");
        job.setSalaryMin(20);
        job.setSalaryMax(25);
        job.setSalaryPeriod("hourly");
        job.setApplyUrl("https://example.com/apply");
        job.setCoverLetterRequired(true);
        job.setDeadline(LocalDate.of(2026, 12, 31));

        JobListingForm form = jobPostingService.toForm(job);

        assertEquals("QA Automation Intern", form.getTitle());
        assertEquals("CareerConnect Labs", form.getCompanyName());
        assertEquals("Montreal", form.getLocation());
        assertEquals("Hybrid", form.getWorkMode());
        assertEquals("Internship", form.getJobType());
        assertEquals("Create automated tests.", form.getDescription());
        assertEquals("Basic Java knowledge", form.getRequirement());
        assertEquals("Java, JUnit", form.getSkills());
        assertEquals(20, form.getSalaryMin());
        assertEquals(25, form.getSalaryMax());
        assertEquals("hourly", form.getSalaryPeriod());
        assertEquals("https://example.com/apply", form.getApplyUrl());
        assertTrue(form.isCoverLetterRequired());
        assertEquals(LocalDate.of(2026, 12, 31), form.getDeadline());
    }
}
