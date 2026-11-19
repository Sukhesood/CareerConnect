package Sprint1.demo.service;

import Sprint1.demo.model.Resume;
import Sprint1.demo.model.User;
import Sprint1.demo.repository.ResumeRepository;
import Sprint1.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock private ResumeRepository resumeRepository;
    @Mock private UserRepository userRepository;
    @Mock private User user;

    @Test
    void deleteHidesResumeFromProfileButKeepsTheRecordForPastApplications() {
        ResumeService service = new ResumeService(resumeRepository, userRepository);
        Resume resume = new Resume(user, "resume.pdf", "application/pdf", new byte[]{1, 2});
        when(user.getId()).thenReturn(10L);
        when(userRepository.findByEmail("seeker@example.com")).thenReturn(Optional.of(user));
        when(resumeRepository.findByIdAndUser_IdAndDeletedFalse(5L, 10L))
                .thenReturn(Optional.of(resume));

        service.delete("seeker@example.com", 5L);

        assertTrue(resume.isDeleted());
        verify(resumeRepository).save(resume);
    }
}
