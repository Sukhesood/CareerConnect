package Sprint1.demo.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import Sprint1.demo.model.JobListing;
import Sprint1.demo.model.User;
import Sprint1.demo.service.CoverLetterPromptBuilder;
import Sprint1.demo.service.CoverLetterService;
import Sprint1.demo.service.JobPostingService;
import Sprint1.demo.service.UserService;
import jakarta.servlet.http.HttpSession;

// Gives the apply page a cover letter draft when the seeker clicks "Write with AI"
@Controller
public class CoverLetterController {

    private final JobPostingService jobPostingService;
    private final UserService userService;
    private final CoverLetterPromptBuilder promptBuilder;
    private final CoverLetterService coverLetterService;

    public CoverLetterController(JobPostingService jobPostingService,
                                 UserService userService,
                                 CoverLetterPromptBuilder promptBuilder,
                                 CoverLetterService coverLetterService) {
        this.jobPostingService = jobPostingService;
        this.userService = userService;
        this.promptBuilder = promptBuilder;
        this.coverLetterService = coverLetterService;
    }

    @GetMapping("/job_postings/{jobId}/cover-letter/draft")
    @ResponseBody
    public ResponseEntity<String> draft(@PathVariable Long jobId, HttpSession session) {
        // 1. The user must be logged in
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in first.");
        }

        // 2. The user and the job must exist
        Optional<User> applicant = userService.findByEmail(email);
        Optional<JobListing> job = jobPostingService.getJobListing(jobId);
        if (applicant.isEmpty() || job.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The job posting could not be found.");
        }

        // 3. Build the prompt, ask for the draft, and send the text back
        try {
           String prompt = promptBuilder.buildPrompt(applicant.get(), job.get(), null);
            return ResponseEntity.ok(coverLetterService.generateDraft(prompt));
        } catch (RuntimeException exception) {
              exception.printStackTrace();
                  return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Could not generate a letter. Please try again.");
        }
    }
}