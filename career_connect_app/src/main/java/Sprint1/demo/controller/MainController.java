
package Sprint1.demo.controller;

import Sprint1.demo.model.User;
import Sprint1.demo.service.UserService;
import Sprint1.demo.service.ResumeService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class MainController {
//hello world
    private final UserService userService;
    private final ResumeService resumeService;

    public MainController(
            UserService userService,
            ResumeService resumeService) {

        this.userService = userService;
        this.resumeService = resumeService;
    }

    private boolean isAuth(HttpSession session) {
        return session.getAttribute(
                "loggedInUserEmail") != null;
    }

    @GetMapping("/")
    public String showHomePage(
            HttpSession session,
            Model model) {

        model.addAttribute(
                "isAuth",
                isAuth(session));

        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        boolean isAuthenticated =
                userService.authenticate(email, password);

        if (isAuthenticated) {

            session.setAttribute(
                    "loggedInUserEmail",
                    email);

            return "redirect:/dashboard";

        } else {

            model.addAttribute(
                    "loginError",
                    "Invalid email or password!");

            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute(
                "loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        Optional<User> user =
                userService.findByEmail(email);

        user.ifPresent(value ->
                model.addAttribute("user", value));

        return "dashboard";
    }

    // Updated profile page
    @GetMapping("/profile")
    public String showProfile(
            HttpSession session,
            Model model) {

        String email = (String) session.getAttribute(
                "loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        Optional<User> user =
                userService.findByEmail(email);

        if (user.isPresent()) {

            model.addAttribute(
                    "user",
                    user.get());

            // Load all resumes for this user
            model.addAttribute(
                    "resumes",
                    resumeService.list(email));

            return "profile";
        }

        return "redirect:/login";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute("user") User updatedUser,
            HttpSession session) {

        String email = (String) session.getAttribute(
                "loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        userService.updateProfile(
                email,
                updatedUser);

        return "redirect:/profile?success=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute(
                "user",
                new User());

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {

            userService.registerUser(user);

        } catch (RuntimeException e) {

            model.addAttribute(
                    "emailError",
                    e.getMessage());

            return "register";
        }

        return "redirect:/register?success";
    }
}
