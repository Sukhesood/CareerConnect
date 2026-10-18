package Sprint1.demo.controller;

import Sprint1.demo.model.JobListing;
import Sprint1.demo.model.JobListingForm;
import Sprint1.demo.model.User;
import Sprint1.demo.repository.JobApplicationRepository;
import Sprint1.demo.service.JobPostingService;
import Sprint1.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Optional;

@Controller
//new main controller
public class MainController {
	 private final UserService userService ;
     private final JobPostingService jobPostingService;
	 private final JobApplicationRepository jobApplicationRepository;
	   public MainController(UserService userService, JobPostingService jobPostingService, JobApplicationRepository jobApplicationRepository) {
	       this.userService = userService;
           this.jobPostingService=jobPostingService;
	       this.jobApplicationRepository = jobApplicationRepository;
	   }
    private boolean isAuth(HttpSession session) {
        return (null != session.getAttribute("loggedInUserEmail"));
    }

    private boolean isRecruiter(HttpSession session) {
        String email =(String) session.getAttribute("loggedInUserEmail");
        Optional<User> result = userService.findByEmail(email);
        if (result.isPresent()){
            User temp = result.get();
            return temp.getRole().equals("RECRUITER");
        }
        return false;
    }



    @GetMapping("/")
    public String showHomePage(HttpSession session, Model model) {
        model.addAttribute("isAuth", isAuth(session));
        return "index";
    }



    @GetMapping({"/job_posting_creation"})
    public String showJobPostingCreation(HttpSession session, Model model){
        if (!isAuth(session)) return "redirect:/login";
        if (!isRecruiter(session)) return "redirect:/dashboard";
        model.addAttribute("jobListing", new JobListing());
        return "job_posting_creation";
    }

    @PostMapping({"/job_posting_creation"})
    public String submitJobPosting(@Valid @ModelAttribute("jobListing") JobListingForm form, BindingResult result, HttpSession session,
                                   RedirectAttributes redirect) {
        if (!isAuth(session)) return "redirect:/login";
        if (!isRecruiter(session)) return "redirect:/dashboard";


        if (result.hasErrors()) {
            return "job_posting_creation";
        }
        String email = (String) session.getAttribute(("loggedInUserEmail"));
        if (email == null) {
            return "redirect:/login";
        }


        jobPostingService.createJobListing(form, email);


        redirect.addFlashAttribute("success", "Listing published.");
        return "redirect:/dashboard";
    }

    // US 3.5 (#37): dedicated page holding every job posting created by the logged-in recruiter
    @GetMapping({"/my_job_postings"})
    public String showMyJobPostings(HttpSession session, Model model) {
        if (!isAuth(session)) return "redirect:/login";
        if (!isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        model.addAttribute("jobListings", jobPostingService.getJobListingsForRecruiter(email));
        model.addAttribute("today", LocalDate.now());
        return "my_job_postings";
    }



    @GetMapping({"/login"})
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email, 
                            @RequestParam("password") String password, 
                            HttpSession session, 
                            Model model) {
        boolean isAuthenticated = userService.authenticate(email, password);
        if (isAuthenticated) {
            session.setAttribute("loggedInUserEmail", email);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("loginError", "Invalid email or password!");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        model.addAttribute("isRecruiter",isRecruiter(session));
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) return "redirect:/login";
        
        Optional<User> user = userService.findByEmail(email);
        user.ifPresent(value -> model.addAttribute("user", value));
        return "dashboard";
    }

    @GetMapping("/applications")
    public String showSubmittedApplications(HttpSession session, Model model) {
        if (!isAuth(session)) return "redirect:/login";
        if (isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        model.addAttribute("applications", jobApplicationRepository.findByApplicant_EmailOrderBySubmittedAtDesc(email));
        return "submitted-applications";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        model.addAttribute("isRecruiter",isRecruiter(session));


        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) return "redirect:/login";

        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "profile";
        }
        return "redirect:/login";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser, HttpSession session) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) return "redirect:/login";

        userService.updateProfile(email, updatedUser);
        return "redirect:/profile?success=true";
    }

    @PostMapping("/profile/upload-resume")
    public String uploadResume(@RequestParam("resumeFile") MultipartFile file, HttpSession session) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) return "redirect:/login";

        userService.saveResume(email, file);
        return "redirect:/profile?resumeSuccess=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }




    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                               BindingResult bindingResult, 
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            userService.registerUser(user);
        } catch (RuntimeException e) {
            model.addAttribute("emailError", e.getMessage());
            return "register";
        }
        return "redirect:/register?success";
    }
}
