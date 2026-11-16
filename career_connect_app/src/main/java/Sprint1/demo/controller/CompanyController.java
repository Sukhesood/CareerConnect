package Sprint1.demo.controller;

import Sprint1.demo.model.*;
import Sprint1.demo.repository.UserRepository;
import Sprint1.demo.service.CompanyService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recruiter/company")
public class CompanyController {

    private final UserRepository userRepository;
    private final CompanyService companyService;

    public CompanyController(
            UserRepository userRepository,
            CompanyService companyService) {

        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    @GetMapping
    public String showCompany(HttpSession session, Model model) {

        String email = (String)
                session.getAttribute("loggedInUserEmail");

        if (email == null) return "redirect:/login";

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow();

        if (!"RECRUITER".equals(recruiter.getRole())) {
            return "redirect:/dashboard";
        }

        Company company = recruiter.getCompany();

        model.addAttribute(
                "company",
                company == null ? new Company() : company
        );

        return "company-settings";
    }

    @PostMapping
    public String saveCompany(
            @Valid @ModelAttribute("company") Company company,
            BindingResult result,
            HttpSession session) {

        String email = (String)
                session.getAttribute("loggedInUserEmail");

        if (email == null) return "redirect:/login";

        if (result.hasErrors()) {
            return "company-settings";
        }

        companyService.saveCompany(email, company);

        return "redirect:/recruiter/company";
    }
}