package Sprint1.demo.controller;

import Sprint1.demo.service.ResumeService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeControllerTest {

    @Mock private ResumeService resumeService;
    @Mock private HttpSession session;
    @Mock private RedirectAttributes redirectAttributes;

    @Test
    void unauthenticatedUserCannotDeleteAResume() {
        ResumeController controller = new ResumeController(resumeService);
        when(session.getAttribute("loggedInUserEmail")).thenReturn(null);

        String view = controller.deleteResume(5L, session, redirectAttributes);

        assertEquals("redirect:/login", view);
        verifyNoInteractions(resumeService, redirectAttributes);
    }
}
