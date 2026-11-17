package Sprint1.demo.controller;

import Sprint1.demo.service.SavedJobService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/saved-jobs")
public class SavedJobController {

    private final SavedJobService savedJobService;

    public SavedJobController(SavedJobService savedJobService) {
        this.savedJobService = savedJobService;
    }

    @GetMapping
    public String showSavedJobs(HttpSession session, Model model) {

        String email = (String)
                session.getAttribute("loggedInUserEmail");

        if (email == null) return "redirect:/login";

        model.addAttribute(
            "savedJobs",
            savedJobService.getSavedJobs(email)
        );

        return "saved-jobs";
    }

    @PostMapping("/{jobId}/save")
    public String saveJob(
            @PathVariable Long jobId,
            HttpSession session,
            RedirectAttributes redirect) {

        String email = (String)
                session.getAttribute("loggedInUserEmail");

        if (email == null) return "redirect:/login";

        try {
            savedJobService.saveJob(email, jobId);

            redirect.addFlashAttribute(
                "success", "Job saved successfully.");

        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute(
                "error", exception.getMessage());
        }

        return "redirect:/job_postings/" + jobId;
    }

    @PostMapping("/{jobId}/remove")
    public String removeJob(
            @PathVariable Long jobId,
            HttpSession session) {

        String email = (String)
                session.getAttribute("loggedInUserEmail");

        if (email == null) return "redirect:/login";

        savedJobService.removeJob(email, jobId);

        return "redirect:/saved-jobs";
    }
}