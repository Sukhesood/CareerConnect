package Sprint1.demo.service;

import Sprint1.demo.model.JobApplication;
import Sprint1.demo.model.JobListing;
import Sprint1.demo.model.JobListingForm;
import Sprint1.demo.model.User;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobPostingServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @InjectMocks
    private JobPostingService jobPostingService;

    @Test
    void deletingAnExpiredPostingArchivesItAndKeepsItsApplicationsUntouched() {
        JobListing job = new JobListing();
        job.setId(42L);
        job.setDeadline(LocalDate.now().minusDays(1));
        JobApplication firstApplication = new JobApplication();
        firstApplication.setStatus("Applied");
        JobApplication secondApplication = new JobApplication();
        secondApplication.setStatus("Under Review");

        jobPostingService.deleteJobListing(job);

        assertTrue("Applied".equals(firstApplication.getStatus()));
        assertTrue("Under Review".equals(secondApplication.getStatus()));
        verifyNoInteractions(jobApplicationRepository);
        verify(jobRepository).archiveById(42L);
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

    @Test
    void expiredPostingsAreHiddenFromJobSeekersButRemainForRecruiterManagement() {
        JobListing openJob = new JobListing();
        openJob.setDeadline(LocalDate.now());
        JobListing noDeadlineJob = new JobListing();
        JobListing expiredJob = new JobListing();
        expiredJob.setDeadline(LocalDate.now().minusDays(1));
        when(jobRepository.findAvailableForApplicant("seeker@example.com"))
                .thenReturn(List.of(openJob, noDeadlineJob, expiredJob));

        List<JobListing> available = jobPostingService.getAvailableJobListings("seeker@example.com");

        assertEquals(List.of(openJob, noDeadlineJob), available);
    }


    @Test
    void formToJobPostingTest() {
        JobListingForm form = new JobListingForm();

        form.setTitle("QA Automation Intern");
        form.setCompanyName("CareerConnect Labs");
        form.setLocation("Montreal");
        form.setWorkMode("Hybrid");
        form.setJobType("Internship");
        form.setDescription("Create automated tests.");
        form.setRequirement("Basic Java knowledge");
        form.setSkills("Java, JUnit");
        form.setSalaryMin(20);
        form.setSalaryMax(25);
        form.setSalaryPeriod("hourly");
        form.setApplyUrl("https://example.com/apply");
        form.setCoverLetterRequired(true);
        form.setDeadline(LocalDate.of(2026, 12, 31));

        User user = new User();
        user.setEmail("user@gmail.com");
        user.setRole("RECRUITER");

        when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.of(user));
        when(jobRepository.save(any(JobListing.class))).thenAnswer(inv -> inv.getArgument(0));

        JobListing job = jobPostingService.createJobListing(form, "user@gmail.com");

        assertEquals("QA Automation Intern", job.getTitle());
        assertEquals("CareerConnect Labs", job.getCompanyName());
        assertEquals("Montreal", job.getLocation());
        assertEquals("Hybrid", job.getWorkMode());
        assertEquals("Internship", job.getJobType());
        assertEquals("Create automated tests.", job.getDescription());
        assertEquals("Basic Java knowledge", job.getRequirement());
        assertEquals("Java, JUnit", job.getSkills());
        assertEquals(20, job.getSalaryMin());
        assertEquals(25, job.getSalaryMax());
        assertEquals("hourly", job.getSalaryPeriod());
        assertEquals("https://example.com/apply", job.getApplyUrl());
        assertTrue(job.isCoverLetterRequired());
        assertEquals(LocalDate.of(2026, 12, 31), job.getDeadline());
        assertSame(user, job.getRecruiter());
    }
}
