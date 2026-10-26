
package Sprint1.demo.controller;

import Sprint1.demo.model.Resume;
import Sprint1.demo.service.ResumeService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/profile")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    // Upload resume
    @PostMapping("/upload-resume")
    public String uploadResume(
            @RequestParam("resumeFile") MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute(
                "loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        try {

            resumeService.upload(email, file);

            redirectAttributes.addFlashAttribute(
                    "resumeMessage",
                    "Resume uploaded successfully!");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "resumeError",
                    e.getMessage());
        }

        return "redirect:/profile";
    }

    // View resume
    @GetMapping("/resumes/{id}")
    public ResponseEntity<byte[]> viewResume(
            @PathVariable Long id,
            HttpSession session) {

        String email = (String) session.getAttribute(
                "loggedInUserEmail");

        Resume resume = resumeService.getOwned(
                email, id);

        return buildResponse(resume, false);
    }

    // Download resume
    @GetMapping("/resumes/{id}/download")
    public ResponseEntity<byte[]> downloadResume(
            @PathVariable Long id,
            HttpSession session) {

        String email = (String) session.getAttribute(
                "loggedInUserEmail");

        Resume resume = resumeService.getOwned(
                email, id);

        return buildResponse(resume, true);
    }

    private ResponseEntity<byte[]> buildResponse(
            Resume resume,
            boolean download) {

        String encodedFilename = UriUtils.encode(
                resume.getFilename(),
                StandardCharsets.UTF_8);

        String disposition =
                (download ? "attachment" : "inline")
                + "; filename*=UTF-8''"
                + encodedFilename;

        return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    disposition)
                .header(
                    "X-Content-Type-Options",
                    "nosniff")
                .header(
                    "Cache-Control",
                    "no-store")
                .contentType(
                    MediaType.parseMediaType(
                        resume.getContentType()))
                .body(resume.getData());
    }

    // Delete selected resume
    @PostMapping("/resumes/{id}/delete")
    public String deleteResume(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String email = (String) session.getAttribute(
                "loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        resumeService.delete(email, id);

        redirectAttributes.addFlashAttribute(
                "resumeMessage",
                "Resume deleted successfully!");

        return "redirect:/profile";
    }
}

