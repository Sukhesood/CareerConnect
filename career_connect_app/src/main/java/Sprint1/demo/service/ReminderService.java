
package Sprint1.demo.service;

import Sprint1.demo.model.JobListing;
import Sprint1.demo.model.InterviewInvitation;
import Sprint1.demo.model.SavedJob;
import Sprint1.demo.model.User;
import Sprint1.demo.model.NotificationType;

import Sprint1.demo.repository.JobRepository;
import Sprint1.demo.repository.InterviewInvitationRepository;
import Sprint1.demo.repository.SavedJobRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ReminderService {

    private static final ZoneId ZONE =
            ZoneId.of("America/Toronto");

    private final JobRepository jobRepository;
    private final SavedJobRepository savedJobRepository;
    private final NotificationService notificationService;
    private final InterviewInvitationRepository invitationRepository;

    // Constructor
    public ReminderService(
            JobRepository jobRepository,
            SavedJobRepository savedJobRepository,
            NotificationService notificationService,
            InterviewInvitationRepository invitationRepository) {

        this.jobRepository = jobRepository;
        this.savedJobRepository = savedJobRepository;
        this.notificationService = notificationService;
        this.invitationRepository = invitationRepository;
    }

    // Run automatically every day at 8 AM
    @Scheduled(
        cron = "0 0 8 * * *",
        zone = "America/Toronto"
    )
    @Transactional
    public void sendDeadlineReminders() {

        LocalDate today = LocalDate.now(ZONE);

        // Check deadlines 3 days and 1 day away
        for (int days : new int[] {3, 1}) {

            LocalDate deadline = today.plusDays(days);

            // ===================================
            // NOTIFY RECRUITERS
            // ===================================

            for (JobListing job :
                    jobRepository.findByDeadlineAndDeletedFalse(
                            deadline)) {

                User recruiter = job.getRecruiter();

                if (recruiter == null) {
                    continue;
                }

                String key =
                        "RECRUITER_DEADLINE:"
                        + recruiter.getId() + ":"
                        + job.getId() + ":"
                        + deadline + ":"
                        + days;

                String message =
                        "Reminder: Your job posting '"
                        + job.getTitle()
                        + "' will close in "
                        + days + " day(s).";

                notificationService.sendReminder(
                        recruiter,
                        NotificationType.DEADLINE_REMINDER,
                        message,
                        "/my_job_postings",
                        key
                );
            }

            // ===================================
            // NOTIFY JOB SEEKERS
            // ===================================

            for (SavedJob saved :
                    savedJobRepository
                        .findByJobListing_DeadlineAndJobListing_DeletedFalse(
                                deadline)) {

                JobListing job = saved.getJobListing();
                User jobSeeker = saved.getUser();

                if (jobSeeker == null) {
                    continue;
                }

                String key =
                        "SEEKER_DEADLINE:"
                        + jobSeeker.getId() + ":"
                        + job.getId() + ":"
                        + deadline + ":"
                        + days;

                String message =
                        "Reminder: Your saved job '"
                        + job.getTitle()
                        + "' will close in "
                        + days + " day(s).";

                notificationService.sendReminder(
                        jobSeeker,
                        NotificationType.DEADLINE_REMINDER,
                        message,
                        "/saved-jobs",
                        key
                );
            }
        }
    }

    // Day-before reminders for job seekers AND recruiters.
    @Scheduled(cron = "0 0 8 * * *", zone = "America/Toronto")
    @Transactional
    public void sendInterviewReminders() {
        LocalDate tomorrow = LocalDate.now(ZONE).plusDays(1);
        LocalDateTime start = tomorrow.atStartOfDay();
        LocalDateTime end = tomorrow.plusDays(1).atStartOfDay();
        for (InterviewInvitation invitation : invitationRepository
                .findByScheduledAtGreaterThanEqualAndScheduledAtLessThan(start, end)) {
            if (!invitation.getJobApplication().getJobListing().isDeleted()) {
                notificationService.interviewReminder(invitation);
            }
        }
    }
}