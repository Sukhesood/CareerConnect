
package Sprint1.demo.service;

import Sprint1.demo.model.InterviewInvitation;
import Sprint1.demo.model.JobApplication;
import Sprint1.demo.model.JobListing;
import Sprint1.demo.model.NotificationType;
import Sprint1.demo.model.SavedJob;
import Sprint1.demo.model.User;

import Sprint1.demo.repository.InterviewInvitationRepository;
import Sprint1.demo.repository.JobRepository;
import Sprint1.demo.repository.SavedJobRepository;

import Sprint1.demo.service.NotificationService;
import Sprint1.demo.service.ReminderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReminderServiceTest {

    private JobRepository jobRepository;
    private SavedJobRepository savedJobRepository;
    private InterviewInvitationRepository invitationRepository;
    private NotificationService notificationService;
    private ReminderService reminderService;

    private User recruiter;
    private User seeker;
    private JobListing job;

    private static final ZoneId TORONTO =
            ZoneId.of("America/Toronto");

    @BeforeEach
    void setUp() {

        jobRepository = mock(JobRepository.class);

        savedJobRepository = mock(SavedJobRepository.class);

        invitationRepository =
                mock(InterviewInvitationRepository.class);

        notificationService = mock(NotificationService.class);

        reminderService = new ReminderService(
                jobRepository,
                savedJobRepository,
                notificationService,
                invitationRepository
        );

        // Recruiter account
        recruiter = new User();
        recruiter.setId(1L);
        recruiter.setEmail("recruiter@test.com");

        // Job seeker account
        seeker = new User();
        seeker.setId(2L);
        seeker.setEmail("seeker@test.com");

        // Test job listing
        job = new JobListing();
        job.setId(10L);
        job.setTitle("Java Developer Intern");
        job.setRecruiter(recruiter);
        job.setDeleted(false);
    }

    // TEST 1:
    // Recruiter receives a reminder one day
    // before the job deadline.
    @Test
    void oneDayDeadlineNotifiesRecruiter() {

        LocalDate deadline =
                LocalDate.now(TORONTO).plusDays(1);

        when(jobRepository.findByDeadlineAndDeletedFalse(
                deadline
        )).thenReturn(List.of(job));

        reminderService.sendDeadlineReminders();

        verify(notificationService).sendReminder(
                eq(recruiter),
                eq(NotificationType.DEADLINE_REMINDER),
                contains("1 day(s)"),
                eq("/my_job_postings"),
                anyString()
        );
    }

    // TEST 2:
    // Saved-job seeker receives a reminder
    // three days before the deadline.
    @Test
    void threeDayDeadlineNotifiesJobSeeker() {

        LocalDate deadline =
                LocalDate.now(TORONTO).plusDays(3);

        SavedJob savedJob = new SavedJob(seeker, job);

        when(savedJobRepository
                .findByJobListing_DeadlineAndJobListing_DeletedFalse(
                        deadline
                )).thenReturn(List.of(savedJob));

        reminderService.sendDeadlineReminders();

        verify(notificationService).sendReminder(
                eq(seeker),
                eq(NotificationType.DEADLINE_REMINDER),
                contains("3 day(s)"),
                eq("/saved-jobs"),
                anyString()
        );
    }

    // TEST 3:
    // No reminders when no jobs match the deadline.
    @Test
    void noEligibleJobsMeansNoNotifications() {

        reminderService.sendDeadlineReminders();

        verifyNoInteractions(notificationService);
    }

    // TEST 4:
    // Interview scheduled for tomorrow
    // triggers the interview reminder.
    @Test
    void tomorrowInterviewTriggersReminder() {

        JobApplication application =
                new JobApplication();

        application.setJobListing(job);
        application.setApplicant(seeker);

        InterviewInvitation invitation =
                new InterviewInvitation(
                        application,
                        LocalDate.now(TORONTO)
                                .plusDays(1)
                                .atTime(14, 0),
                        "Google Meet",
                        "Technical Interview"
                );

        when(invitationRepository
                .findByScheduledAtGreaterThanEqualAndScheduledAtLessThan(
                        any(),
                        any()
                )).thenReturn(List.of(invitation));

        reminderService.sendInterviewReminders();

        verify(notificationService)
                .interviewReminder(invitation);
    }

    // TEST 5:
    // Archived job must not trigger an
    // interview reminder.
    @Test
    void archivedJobDoesNotTriggerInterviewReminder() {

        job.setDeleted(true);

        JobApplication application =
                new JobApplication();

        application.setJobListing(job);
        application.setApplicant(seeker);

        InterviewInvitation invitation =
                new InterviewInvitation(
                        application,
                        LocalDate.now(TORONTO)
                                .plusDays(1)
                                .atTime(14, 0),
                        "Google Meet",
                        "Technical Interview"
                );

        when(invitationRepository
                .findByScheduledAtGreaterThanEqualAndScheduledAtLessThan(
                        any(),
                        any()
                )).thenReturn(List.of(invitation));

        reminderService.sendInterviewReminders();

        verifyNoInteractions(notificationService);
    }
}
