
package Sprint1.demo.controller;

import Sprint1.demo.service.PasswordResetService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class PasswordResetController {

	private final PasswordResetService passwordResetService;

    // Constructor Injection (Spring automatically injects this)
    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email) {
        passwordResetService.createAndSendToken(email);
        // Redirect with ?sent parameter to match th:if="${param.sent}" in your HTML
        return "redirect:/forgot-password?sent";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        boolean isValid = passwordResetService.validatePasswordResetToken(token);
        model.addAttribute("token", token);
        model.addAttribute("valid", isValid); // Passes true/false so your HTML checks th:if / th:unless correctly
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        
        // 1. Check if passwords match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("token", token);
            model.addAttribute("valid", true); // Token was valid, but form submission had a mismatch error
            model.addAttribute("error", "New passwords do not match.");
            return "reset-password";
        }

        try {
            // 2. Update password via service
            passwordResetService.updatePassword(token, password);
            redirectAttributes.addFlashAttribute("message", "Password successfully updated. You can now log in.");
            return "redirect:/login";
        } catch (Exception e) {
            // 3. Handle token expiry or other errors on submission
            model.addAttribute("token", token);
            model.addAttribute("valid", false);
            model.addAttribute("error", e.getMessage());
            return "reset-password";
        }
    }
}