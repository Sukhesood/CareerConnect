package Sprint1.demo.controller;

import Sprint1.demo.model.Notification;
import Sprint1.demo.service.NotificationService;
import Sprint1.demo.service.UserService;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import Sprint1.demo.service.NotificationEventStream;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final NotificationEventStream eventStream;
    public NotificationController(
            NotificationService notificationService,
            UserService userService, NotificationEventStream eventStream) {

        this.notificationService = notificationService;
        this.userService = userService;
        this.eventStream = eventStream;
    }

    // Browser uses the existing logged-in session; no user identifier is accepted from client.
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter stream(HttpSession session) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return eventStream.subscribe(email);
    }

    @GetMapping(value = "/api/unread-count", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Long> unreadCount(HttpSession session) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return Map.of("count", notificationService.getUnreadCount(email));
    }

    @GetMapping
    public String showNotifications(
            @RequestParam(defaultValue = "all") String filter,
            HttpSession session,
            Model model) {

        String email = (String)
                session.getAttribute("loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }
        boolean isRecruiter = userService.findByEmail(email)
                .map(user -> "RECRUITER".equals(user.getRole()))
                .orElse(false);

        model.addAttribute("isRecruiter", isRecruiter);
        List<Notification> all =
                notificationService.getNotifications(email);

        long unreadCount =
                notificationService.getUnreadCount(email);

        if (!List.of("all", "unread", "read").contains(filter)) {
            filter = "all";
        }

        List<Notification> filtered;

        if (filter.equals("unread")) {
            filtered = all.stream()
                    .filter(n -> !n.isReadFlag())
                    .toList();

        } else if (filter.equals("read")) {
            filtered = all.stream()
                    .filter(Notification::isReadFlag)
                    .toList();

        } else {
            filtered = all;
        }

        model.addAttribute("notifications", filtered);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("totalCount", all.size());
        model.addAttribute("filter", filter);

        return "notifications";
    }

    @PostMapping("/{id}/read")
    public String markAsRead(
            @PathVariable Long id,
            HttpSession session) {

        String email = (String)
                session.getAttribute("loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        notificationService.markAsRead(id, email);

        return "redirect:/notifications";
    }

    @PostMapping("/read-all")
    public String markAllAsRead(HttpSession session) {

        String email = (String)
                session.getAttribute("loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        notificationService.markAllAsRead(email);

        return "redirect:/notifications";
    }
}