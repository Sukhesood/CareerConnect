
package Sprint1.demo.service;

import Sprint1.demo.model.JobApplication;
import Sprint1.demo.model.JobListing;
import Sprint1.demo.model.Notification;
import Sprint1.demo.model.NotificationType;
import Sprint1.demo.model.User;

import Sprint1.demo.repository.NotificationRepository;

import Sprint1.demo.service.NotificationService;
import Sprint1.demo.service.NotificationEventStream;
import Sprint1.demo.service.NotificationEmailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    private NotificationRepository repository;
    private NotificationEventStream eventStream;
    private NotificationEmailService emailService;
    private NotificationService service;

    private User recruiter;
    private User seeker;
    private JobApplication application;

    @BeforeEach
    void setUp() {

        repository = mock(NotificationRepository.class);
        eventStream = mock(NotificationEventStream.class);
        emailService = mock(NotificationEmailService.class);

        service = new NotificationService(
                repository,
                eventStream,
                emailService
        );

        // Simulate saving a notification in MySQL.
        when(repository.save(any(Notification.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        // Create a test recruiter.
        recruiter = new User();
        recruiter.setId(1L);
        recruiter.setEmail("recruiter@test.com");
        recruiter.setFirstName("Rita");

        // Create a test job seeker.
        seeker = new User();
        seeker.setId(2L);
        seeker.setEmail("seeker@test.com");
        seeker.setFirstName("Alex");

        // Create a test job.
        JobListing job = new JobListing();
        job.setId(10L);
        job.setTitle("Java Developer Intern");
        job.setRecruiter(recruiter);

        // Create a test application.
        application = new JobApplication();
        application.setJobListing(job);
        application.setApplicant(seeker);
        application.setStatus("Applied");
    }

    // TEST 1:
    // Recruiter receives a notification for a new application.
    @Test
    void newApplicationCreatesNotificationForRecruiter() {

        service.newApplicationSubmitted(application);

        verify(repository).save(argThat(n ->
                n.getRecipient() == recruiter
                && n.getType() == NotificationType.NEW_APPLICATION
                && n.getMessage().contains("Java Developer Intern")
        ));
    }

    // TEST 2:
    // Job seeker receives a status change notification.
    @Test
    void changedStatusNotifiesJobSeeker() {

        application.setStatus("Under Review");

        service.applicationStatusChanged(
                application,
                "Applied"
        );

        verify(repository).save(argThat(n ->
                n.getRecipient() == seeker
                && n.getType() == NotificationType.STATUS_CHANGE
                && n.getMessage().contains("Under Review")
        ));
    }

    // TEST 3:
    // No notification when application status is unchanged.
    @Test
    void unchangedStatusDoesNotCreateNotification() {

        service.applicationStatusChanged(
                application,
                "Applied"
        );

        verify(repository, never())
                .save(any(Notification.class));
    }

    // TEST 4:
    // Duplicate deadline reminder must not be created.
    @Test
    void duplicateReminderIsPrevented() {

        when(repository.existsByDeduplicationKey("KEY-1"))
                .thenReturn(true);

        service.sendReminder(
                seeker,
                NotificationType.DEADLINE_REMINDER,
                "Application deadline tomorrow",
                "/saved-jobs",
                "KEY-1"
        );

        verify(repository, never())
                .save(any(Notification.class));
    }

    // TEST 5:
    // Notification owner can mark notification as read.
    @Test
    void notificationOwnerCanMarkAsRead() {

        Notification notification = new Notification(
                seeker,
                NotificationType.STATUS_CHANGE,
                "Application updated",
                "/applications",
                null
        );

        when(repository.findByIdAndRecipient_Email(
                7L,
                "seeker@test.com"
        )).thenReturn(Optional.of(notification));

        service.markAsRead(
                7L,
                "seeker@test.com"
        );

        assertTrue(notification.isReadFlag());
    }

    // TEST 6:
    // Another user cannot mark somebody else's
    // notification as read.
    @Test
    void anotherUserCannotMarkNotificationAsRead() {

        Notification notification = new Notification(
                seeker,
                NotificationType.STATUS_CHANGE,
                "Application updated",
                "/applications",
                null
        );

        when(repository.findByIdAndRecipient_Email(
                7L,
                "recruiter@test.com"
        )).thenReturn(Optional.empty());

        service.markAsRead(
                7L,
                "recruiter@test.com"
        );

        assertFalse(notification.isReadFlag());
    }
}
