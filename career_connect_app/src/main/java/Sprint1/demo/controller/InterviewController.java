package Sprint1.demo.controller;

import Sprint1.demo.repository.JobApplicationRepository;
import Sprint1.demo.service.InterviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/recruiter/applications/{applicationId}/interview")
public class InterviewController {
    private final JobApplicationRepository applicationRepository;
    private final InterviewService interviewService;

    public InterviewController(JobApplicationRepository applicationRepository,
                               InterviewService interviewService) {
        this.applicationRepository = applicationRepository;
        this.interviewService = interviewService;
    }

    @GetMapping
    public String form(@PathVariable Long applicationId, HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) return "redirect:/login";
        var application = applicationRepository.findByIdAndJobListing_Recruiter_Email(applicationId, email);
        if (application.isEmpty()) return "redirect:/recruiter/applications";
        model.addAttribute("jobApplication", application.get());
        return "interview-invite";
    }

    @PostMapping
    public String invite(@PathVariable Long applicationId,
                         @RequestParam("scheduledAt")
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime scheduledAt,
                         @RequestParam("locationOrLink") String locationOrLink,
                         @RequestParam(value = "note", required = false) String note,
                         HttpSession session, RedirectAttributes redirect) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) return "redirect:/login";
        try {
            interviewService.invite(email, applicationId, scheduledAt, locationOrLink, note);
            redirect.addFlashAttribute("success", "Interview invitation sent to the applicant.");
            return "redirect:/recruiter/applications";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/recruiter/applications/" + applicationId + "/interview";
        }
    }
}