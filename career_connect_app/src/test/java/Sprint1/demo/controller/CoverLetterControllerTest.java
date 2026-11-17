package Sprint1.demo.controller;

import Sprint1.demo.model.JobListing;
import Sprint1.demo.model.User;
import Sprint1.demo.service.CoverLetterPromptBuilder;
import Sprint1.demo.service.CoverLetterService;
import Sprint1.demo.service.JobPostingService;
import Sprint1.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoverLetterControllerTest {

    // Fake helpers, so the tests never call the real AI
    @Mock private JobPostingService jobPostingService;
    @Mock private UserService userService;
    @Mock private CoverLetterPromptBuilder promptBuilder;
    @Mock private CoverLetterService coverLetterService;
    @Mock private HttpSession session;

    private CoverLetterController controller;

    @BeforeEach
    void setUp() {
        controller = new CoverLetterController(jobPostingService, userService, promptBuilder, coverLetterService);
    }

    @Test
    void draftIsRefusedWhenNobodyIsLoggedIn() {
        when(session.getAttribute("loggedInUserEmail")).thenReturn(null);

        ResponseEntity<String> response = controller.draft(1L, session);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void draftReturnsNotFoundWhenTheJobDoesNotExist() {
        when(session.getAttribute("loggedInUserEmail")).thenReturn("seeker@example.com");
        when(userService.findByEmail("seeker@example.com")).thenReturn(Optional.of(new User()));
        when(jobPostingService.getJobListing(5L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = controller.draft(5L, session);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void draftReturnsTheTextWrittenByTheAi() {
        User seeker = new User();
        JobListing job = new JobListing();
        when(session.getAttribute("loggedInUserEmail")).thenReturn("seeker@example.com");
        when(userService.findByEmail("seeker@example.com")).thenReturn(Optional.of(seeker));
        when(jobPostingService.getJobListing(7L)).thenReturn(Optional.of(job));
        when(promptBuilder.buildPrompt(seeker, job, null)).thenReturn("the prompt");
        when(coverLetterService.generateDraft("the prompt")).thenReturn("Dear Hiring Manager, ...");

        ResponseEntity<String> response = controller.draft(7L, session);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Dear Hiring Manager, ...", response.getBody());
    }

    @Test
    void draftShowsAFriendlyMessageWhenTheAiFails() {
        User seeker = new User();
        JobListing job = new JobListing();
        when(session.getAttribute("loggedInUserEmail")).thenReturn("seeker@example.com");
        when(userService.findByEmail("seeker@example.com")).thenReturn(Optional.of(seeker));
        when(jobPostingService.getJobListing(7L)).thenReturn(Optional.of(job));
        when(promptBuilder.buildPrompt(seeker, job, null)).thenReturn("the prompt");
        when(coverLetterService.generateDraft("the prompt")).thenThrow(new RuntimeException("AI is down"));

        ResponseEntity<String> response = controller.draft(7L, session);

        assertEquals(503, response.getStatusCode().value());
        assertEquals("Could not generate a letter. Please try again.", response.getBody());
    }
}