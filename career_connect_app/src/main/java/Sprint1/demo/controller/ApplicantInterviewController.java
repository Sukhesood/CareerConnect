package Sprint1.demo.controller;

import Sprint1.demo.repository.InterviewInvitationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicantInterviewController {
    private final InterviewInvitationRepository invitationRepository;

    public ApplicantInterviewController(InterviewInvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @GetMapping("/interviews")
    public String listMyInterviews(HttpSession session, Model model) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) return "redirect:/login";
        model.addAttribute("interviews",
                invitationRepository.findByJobApplication_Applicant_EmailOrderByScheduledAtDesc(email));
        return "my-interviews";
    }
}