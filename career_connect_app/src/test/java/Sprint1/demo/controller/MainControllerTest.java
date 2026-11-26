package Sprint1.demo.controller;

import Sprint1.demo.model.ApplicationSummary;
import Sprint1.demo.model.JobApplication;
import Sprint1.demo.model.JobListing;
import Sprint1.demo.model.JobListingForm;
import Sprint1.demo.model.Resume;
import Sprint1.demo.model.User;
import Sprint1.demo.repository.ApplicationSummaryRepository;
import Sprint1.demo.repository.JobApplicationRepository;
import Sprint1.demo.repository.JobRepository;
import Sprint1.demo.service.JobPostingService;
import Sprint1.demo.service.NotificationService;
import Sprint1.demo.service.ResumeService;
import Sprint1.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.Resource;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class MainControllerTest {

    @Mock private UserService userService;
    @Mock private JobPostingService jobPostingService;
    @Mock private JobApplicationRepository jobApplicationRepository;
    @Mock private JobRepository jobRepository;
    @Mock private ResumeService resumeService;
    @Mock private HttpSession session;
    @Mock private RedirectAttributes redirectAttributes;
    @Mock private ApplicationSummaryRepository applicationSummaryRepository;
    @Mock private NotificationService notificationService;

    private MainController controller;

    @BeforeEach
    void setUp() {
        controller = new MainController(userService, jobPostingService, jobApplicationRepository, jobRepository, resumeService, applicationSummaryRepository, notificationService);
        User recruiter = new User();
        recruiter.setRole("RECRUITER");
        lenient().when(session.getAttribute("loggedInUserEmail")).thenReturn("recruiter@example.com");
        lenient().when(userService.findByEmail("recruiter@example.com")).thenReturn(Optional.of(recruiter));
    }

    @Test
    void dashboardShowsEachPostingWithItsApplicationCount() {
        JobListing firstJob = job(10L, false);
        JobListing secondJob = job(20L, false);
        when(jobPostingService.getJobListingsForRecruiter("recruiter@example.com"))
                .thenReturn(List.of(firstJob, secondJob));
        when(jobApplicationRepository.countByJobListing_Id(10L)).thenReturn(3L);
        when(jobApplicationRepository.countByJobListing_Id(20L)).thenReturn(1L);
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.showMyJobPostings(session, model);

        assertEquals("my_job_postings", view);
        assertEquals(List.of(firstJob, secondJob), model.getAttribute("jobListings"));
        assertEquals(Map.of(10L, 3L, 20L, 1L), model.getAttribute("applicationCounts"));
    }

    @Test
    void applicationDashboardFiltersActiveApplicationsAndKeepsDeletedPostingsSeparate() {
        JobListing activeJob = job(10L, false);
        JobListing deletedJob = job(20L, true);
        JobApplication matching = application(activeJob, "Under Review", LocalDate.of(2026, 11, 15));
        JobApplication differentStatus = application(activeJob, "Applied", LocalDate.of(2026, 11, 15));
        JobApplication deletedApplication = application(deletedJob, "Rejected", LocalDate.of(2026, 11, 15));
        when(jobApplicationRepository.findByJobListing_Recruiter_EmailOrderBySubmittedAtDesc("recruiter@example.com"))
                .thenReturn(List.of(matching, differentStatus, deletedApplication));
        when(jobPostingService.getJobListingsForRecruiter("recruiter@example.com")).thenReturn(List.of(activeJob));
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.showRecruiterApplications("Under Review", 10L,
                LocalDate.of(2026, 11, 15), LocalDate.of(2026, 11, 15), session, model);

        assertEquals("recruiter-applications", view);
        assertEquals(2, model.getAttribute("totalApplications"));
        assertEquals(1, model.getAttribute("displayedApplications"));
        assertEquals(1, model.getAttribute("deletedApplicationsCount"));
        assertTrue(((Map<?, ?>) model.getAttribute("applicationsByJob")).containsKey(activeJob));
        assertTrue(((Map<?, ?>) model.getAttribute("deletedApplicationsByJob")).containsKey(deletedJob));
    }

    @Test
    void recruiterApplicationFilterRejectsAnInvalidSubmittedDateRange() {
        JobListing activeJob = job(10L, false);
        JobApplication application = application(activeJob, "Applied", LocalDate.of(2026, 11, 15));
        when(jobApplicationRepository.findByJobListing_Recruiter_EmailOrderBySubmittedAtDesc("recruiter@example.com"))
                .thenReturn(List.of(application));
        when(jobPostingService.getJobListingsForRecruiter("recruiter@example.com")).thenReturn(List.of(activeJob));
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.showRecruiterApplications("", null,
                LocalDate.of(2026, 11, 20), LocalDate.of(2026, 11, 15), session, model);

        assertEquals("recruiter-applications", view);
        assertEquals(0, model.getAttribute("displayedApplications"));
        assertTrue(((Map<?, ?>) model.getAttribute("applicationsByJob")).isEmpty());
        assertEquals("The start date must be on or before the end date.", model.getAttribute("filterError"));
    }

    @Test
    void recruiterCanUpdateAnOwnedApplicationStatus() {
        JobApplication application = new JobApplication();
        application.setStatus("Applied");
        when(jobApplicationRepository.findByIdAndJobListing_Recruiter_Email(99L, "recruiter@example.com"))
                .thenReturn(Optional.of(application));

        String view = controller.updateApplicationStatus(99L, "Interview", session, redirectAttributes);

        assertEquals("redirect:/recruiter/applications", view);
        assertEquals("Interview", application.getStatus());
        verify(jobApplicationRepository).save(application);
        verify(redirectAttributes).addFlashAttribute("success", "Status updated to \"Interview\".");
    }

    @Test
    void recruiterCannotUpdateAnApplicationOwnedBySomeoneElse() {
        when(jobApplicationRepository.findByIdAndJobListing_Recruiter_Email(99L, "recruiter@example.com"))
                .thenReturn(Optional.empty());

        String view = controller.updateApplicationStatus(99L, "Interview", session, redirectAttributes);

        assertEquals("redirect:/recruiter/applications", view);
        verify(jobApplicationRepository, never()).save(any(JobApplication.class));
        verifyNoInteractions(applicationSummaryRepository);
        verify(redirectAttributes).addFlashAttribute("error", "That application could not be found.");
    }

    @Test
    void recruiterRejectingAnApplicationClosesAndSavesItsHistorySummary() {
        JobApplication application = new JobApplication();
        application.setStatus("Under Review");
        ApplicationSummary summary = new ApplicationSummary();
        summary.setStatus("Under Review");
        summary.setState("Active");
        when(jobApplicationRepository.findByIdAndJobListing_Recruiter_Email(99L, "recruiter@example.com"))
                .thenReturn(Optional.of(application));
        when(applicationSummaryRepository.findByApplicationId(99L)).thenReturn(Optional.of(summary));

        String view = controller.updateApplicationStatus(99L, "Rejected", session, redirectAttributes);

        assertEquals("redirect:/recruiter/applications", view);
        assertEquals("Rejected", application.getStatus());
        assertEquals("Rejected", summary.getStatus());
        assertEquals("Closed", summary.getState());
        verify(jobApplicationRepository).save(application);
        verify(applicationSummaryRepository).save(summary);
    }

    @Test
    void recruiterCannotCreatePostingWhenMinimumSalaryExceedsMaximumSalary() {
        JobListingForm form = new JobListingForm();
        form.setSalaryMin(25);
        form.setSalaryMax(20);
        BindingResult result = new BeanPropertyBindingResult(form, "jobListing");

        String view = controller.submitJobPosting(form, result, session, redirectAttributes);

        assertEquals("job_posting_creation", view);
        assertTrue(result.hasFieldErrors("salaryMax"));
        assertEquals("Maximum salary must be greater than or equal to the minimum salary.",
                result.getFieldError("salaryMax").getDefaultMessage());
        verifyNoInteractions(jobPostingService);
    }

    @Test
    void jobSeekerCanSubmitAnApplicationAndReceivesConfirmation() {
        User seeker = new User();
        seeker.setRole("JOB_SEEKER");
        JobListing job = job(10L, false);
        job.setCompanyName("CareerConnect");
        job.setTitle("Junior Developer");
        job.setPostedDate(LocalDate.of(2026, 11, 1));
        Resume resume = mock(Resume.class);
        when(resume.getId()).thenReturn(7L);
        when(session.getAttribute("loggedInUserEmail")).thenReturn("seeker@example.com");
        when(userService.findByEmail("seeker@example.com")).thenReturn(Optional.of(seeker));
        when(jobPostingService.getJobListing(10L)).thenReturn(Optional.of(job));
        when(jobApplicationRepository.existsByApplicant_EmailAndJobListing_Id("seeker@example.com", 10L)).thenReturn(false);
        when(resumeService.list("seeker@example.com")).thenReturn(List.of(resume));
        when(jobApplicationRepository.save(any(JobApplication.class))).thenAnswer(invocation -> {
            JobApplication saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            return saved;
        });

        String view = controller.applyToJob(10L, 7L, null, session, redirectAttributes);

        assertEquals("redirect:/job_postings", view);
        verify(jobApplicationRepository).save(any(JobApplication.class));
        verify(applicationSummaryRepository).save(any(ApplicationSummary.class));
        verify(redirectAttributes).addFlashAttribute("success", "Your application was submitted.");
    }

    @Test
    void jobSeekerCanViewCurrentAndPastApplicationHistory() {
        User seeker = new User();
        seeker.setRole("JOB_SEEKER");
        JobApplication current = application(job(10L, false), "Interview", LocalDate.of(2026, 11, 15));
        JobApplication past = application(job(20L, true), "Rejected", LocalDate.of(2026, 10, 1));
        ApplicationSummary currentSummary = new ApplicationSummary();
        currentSummary.setStatus("Interview");
        currentSummary.setState("Active");
        ApplicationSummary pastSummary = new ApplicationSummary();
        pastSummary.setStatus("Rejected");
        pastSummary.setState("Closed");
        when(session.getAttribute("loggedInUserEmail")).thenReturn("seeker@example.com");
        when(userService.findByEmail("seeker@example.com")).thenReturn(Optional.of(seeker));
        when(jobApplicationRepository.findByApplicant_EmailOrderBySubmittedAtDesc("seeker@example.com"))
                .thenReturn(List.of(current, past));
        when(applicationSummaryRepository.search("seeker@example.com", null, "", null, null, null))
                .thenReturn(List.of(currentSummary, pastSummary));
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.myApplications(null, null, null, null, null, session, model);

        assertEquals("/submitted-applications", view);
        assertEquals(List.of(current, past), model.getAttribute("applications"));
        assertEquals(List.of(currentSummary, pastSummary), model.getAttribute("applicationSummaries"));
    }

    @Test
    void recruiterCanDownloadAnApplicantsResumeAndCoverLetter() throws Exception {
        Resume resume = new Resume(new User(), "candidate-resume.pdf", "application/pdf", new byte[]{1, 2, 3});
        JobApplication application = new JobApplication();
        application.setSelectedResume(resume);
        application.setCoverLetterFilename("cover-letter.pdf");
        application.setCoverLetterData(new byte[]{4, 5});
        when(jobApplicationRepository.findByIdAndJobListing_Recruiter_Email(75L, "recruiter@example.com"))
                .thenReturn(Optional.of(application));

        Resource downloadedResume = controller.downloadApplicantResume(75L, session).getBody();
        Resource downloadedLetter = controller.downloadApplicantCoverLetter(75L, session).getBody();

        assertTrue(downloadedResume != null && downloadedResume.exists());
        assertTrue(downloadedLetter != null && downloadedLetter.exists());
        assertFalse(downloadedLetter.contentLength() == 0);
    }

    private JobListing job(Long id, boolean deleted) {
        JobListing job = new JobListing();
        job.setId(id);
        job.setDeleted(deleted);
        return job;
    }

    private JobApplication application(JobListing job, String status, LocalDate submittedDate) {
        JobApplication application = new JobApplication();
        application.setJobListing(job);
        application.setStatus(status);
        application.setSubmittedAt(submittedDate);
        return application;
    }




}
