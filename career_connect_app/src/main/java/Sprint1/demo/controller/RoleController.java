
package Sprint1.demo.controller;

import Sprint1.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RoleController {

    private final UserService userService;

    public RoleController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/profile/switch-role")
    public String switchRole(
            @RequestParam("role") String role,
            HttpSession session) {

        String email = (String) session.getAttribute(
                "loggedInUserEmail"
        );

        if (email == null) {
            return "redirect:/login";
        }

        try {
            userService.switchRole(email, role);

            return "redirect:/profile?roleSuccess=true";

        } catch (IllegalArgumentException e) {

            return "redirect:/profile?roleError=true";
        }
    }
}
