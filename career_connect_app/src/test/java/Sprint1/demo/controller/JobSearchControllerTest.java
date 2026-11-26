package Sprint1.demo.controller;

import Sprint1.demo.model.JobListing;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobSearchControllerTest {

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
        controller = new MainController(userService, jobPostingService, jobApplicationRepository,
                jobRepository, resumeService, applicationSummaryRepository, notificationService);
    }
    @Test
    void searchRemovesExtraSpacesAroundTheTypedWords() {
        when(session.getAttribute("loggedInUserEmail")).thenReturn("seeker@example.com");
        JobListing match = new JobListing();
        when(jobRepository.search("java", "Montreal", "Full-time", "seeker@example.com"))
                .thenReturn(List.of(match));
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.showJobs("  java ", " Montreal ", "Full-time ", model, session, redirectAttributes);

        assertEquals("jobs", view);
        assertEquals(List.of(match), model.getAttribute("jobs"));
        assertEquals("java", model.getAttribute("keyword"));
        assertEquals("Montreal", model.getAttribute("selectedLocation"));
        assertEquals("Full-time", model.getAttribute("selectedJobType"));
        assertTrue((Boolean) model.getAttribute("filtersActive"));
    }

    @Test
    void searchSendsVisitorsWhoAreNotLoggedInBackToTheHomePage() {
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.showJobs("java", "", "", model, session, redirectAttributes);

        assertEquals("redirect:/", view);
        verifyNoInteractions(jobRepository);
    }
    @Test
    void searchWithNoFiltersShowsEveryJobAndFillsTheDropdowns() {
        when(session.getAttribute("loggedInUserEmail")).thenReturn("seeker@example.com");
        JobListing first = new JobListing();
        JobListing second = new JobListing();
        when(jobRepository.search("", "", "", "seeker@example.com"))
                .thenReturn(List.of(first, second));
        when(jobRepository.findDistinctLocations()).thenReturn(List.of("Montreal", "Toronto"));
        when(jobRepository.findDistinctJobTypes()).thenReturn(List.of("Full-time", "Internship"));
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.showJobs("", "", "", model, session, redirectAttributes);

        assertEquals("jobs", view);
        assertEquals(List.of(first, second), model.getAttribute("jobs"));
        assertEquals(List.of("Montreal", "Toronto"), model.getAttribute("locations"));
        assertEquals(List.of("Full-time", "Internship"), model.getAttribute("jobTypes"));
        assertFalse((Boolean) model.getAttribute("filtersActive"));
    }
    @Test
    void searchWithNoMatchingJobsShowsAnEmptyListAndKeepsTheTypedWords() {
        when(session.getAttribute("loggedInUserEmail")).thenReturn("seeker@example.com");
        when(jobRepository.search("zzzz", "", "", "seeker@example.com"))
                .thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.showJobs("zzzz", "", "", model, session, redirectAttributes);

        assertEquals("jobs", view);
        assertTrue(((List<?>) model.getAttribute("jobs")).isEmpty());
        assertEquals("zzzz", model.getAttribute("keyword"));
        assertTrue((Boolean) model.getAttribute("filtersActive"));
    }
    @Test
    void searchWithOnlyALocationFilterTurnsTheFiltersOn() {
        when(session.getAttribute("loggedInUserEmail")).thenReturn("seeker@example.com");
        JobListing match = new JobListing();
        when(jobRepository.search("", "Toronto", "", "seeker@example.com"))
                .thenReturn(List.of(match));
        ExtendedModelMap model = new ExtendedModelMap();

        controller.showJobs("", "Toronto", "", model, session, redirectAttributes);

        assertEquals(List.of(match), model.getAttribute("jobs"));
        assertEquals("Toronto", model.getAttribute("selectedLocation"));
        assertEquals("", model.getAttribute("keyword"));
        assertTrue((Boolean) model.getAttribute("filtersActive"));
    }

}