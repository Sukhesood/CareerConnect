package Sprint1.demo.service;

import Sprint1.demo.model.*;
import Sprint1.demo.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationEventStream eventStream;
    private final NotificationEmailService emailService;

    public NotificationService(NotificationRepository repository,
                               NotificationEventStream eventStream,
                               NotificationEmailService emailService) {
        this.notificationRepository = repository;
        this.eventStream = eventStream;
        this.emailService = emailService;
    }

    // Existing public API retained for your MainController/NotificationController.
    @Transactional
    public void createNotification(User recipient, NotificationType type, String message,
                                   String targetUrl, String deduplicationKey) {
        if (recipient == null) return;
        Notification notification = notificationRepository.save(
                new Notification(recipient, type, message, targetUrl, deduplicationKey));
        String email = recipient.getEmail();
        Long id = notification.getId();
        // Never deliver a notification for an uncommitted or rolled-back database operation.
        Runnable deliver = () -> {
            eventStream.published(email, id);
            emailService.send(email, type.name().replace('_', ' '), message);
        };
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { deliver.run(); }
            });
        } else {
            deliver.run();
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotifications(String email) {
        return notificationRepository.findByRecipient_EmailOrderByCreatedAtDesc(email);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        return notificationRepository.countByRecipient_EmailAndReadFlagFalse(email);
    }

    @Transactional
    public void markAsRead(Long notificationId, String email) {
        notificationRepository.findByIdAndRecipient_Email(notificationId, email)
                .ifPresent(n -> n.setReadFlag(true));
    }

    @Transactional
    public void markAllAsRead(String email) {
        for (Notification n : getNotifications(email)) n.setReadFlag(true);
    }

    @Transactional
    public void newApplicationSubmitted(JobApplication application) {
        JobListing job = application.getJobListing();
        createNotification(job.getRecruiter(), NotificationType.NEW_APPLICATION,
                "New application received for '" + job.getTitle() + "' from "
                        + application.getApplicant().getFirstName() + ".",
                "/recruiter/applications", null);
    }

    @Transactional
    public void applicationStatusChanged(JobApplication application, String previousStatus) {
        if (application.getStatus().equals(previousStatus)) return;
        createNotification(application.getApplicant(), NotificationType.STATUS_CHANGE,
                "Your application for '" + application.getJobListing().getTitle()
                        + "' is now " + application.getStatus() + ".",
                "/applications", null);
    }

    @Transactional
    public void interviewScheduled(InterviewInvitation invitation) {
        JobApplication application = invitation.getJobApplication();
        String when = invitation.getScheduledAt().format(
                DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a"));
        createNotification(application.getApplicant(), NotificationType.INTERVIEW_INVITATION,
                "You are invited to interview for '" + application.getJobListing().getTitle()
                        + "' on " + when + ". Location or link: " + invitation.getLocationOrLink(),
                "/interviews", null);
    }

    @Transactional
    public void interviewReminder(InterviewInvitation invitation) {
        JobApplication application = invitation.getJobApplication();
        String when = invitation.getScheduledAt().format(
                DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a"));
        sendReminder(application.getApplicant(), NotificationType.INTERVIEW_REMINDER,
                "Reminder: your interview for '" + application.getJobListing().getTitle()
                        + "' is tomorrow at " + when + ".",
                "/interviews", "INTERVIEW:SEEKER:" + invitation.getId());
        sendReminder(application.getJobListing().getRecruiter(), NotificationType.INTERVIEW_REMINDER,
                "Reminder: your candidate interview for '" + application.getJobListing().getTitle()
                        + "' is tomorrow at " + when + ".",
                "/recruiter/applications", "INTERVIEW:RECRUITER:" + invitation.getId());
    }

    @Transactional
    public void sendReminder(User recipient, NotificationType type, String message,
                             String targetUrl, String reminderKey) {
        if (!notificationRepository.existsByDeduplicationKey(reminderKey)) {
            createNotification(recipient, type, message, targetUrl, reminderKey);
        }
    }
}