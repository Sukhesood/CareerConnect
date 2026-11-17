package Sprint1.demo.service;

import Sprint1.demo.model.InterviewInvitation;
import Sprint1.demo.model.JobApplication;
import Sprint1.demo.repository.InterviewInvitationRepository;
import Sprint1.demo.repository.JobApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InterviewService {
    private final JobApplicationRepository applicationRepository;
    private final InterviewInvitationRepository invitationRepository;
    private final NotificationService notificationService;

    public InterviewService(JobApplicationRepository applicationRepository,
                            InterviewInvitationRepository invitationRepository,
                            NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.invitationRepository = invitationRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void invite(String recruiterEmail, Long applicationId, LocalDateTime scheduledAt,
                       String locationOrLink, String note) {
        JobApplication application = applicationRepository
                .findByIdAndJobListing_Recruiter_Email(applicationId, recruiterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Application not found or not yours."));

        if (application.getJobListing().isDeleted()) {
            throw new IllegalArgumentException("This job posting is archived.");
        }
        if (scheduledAt == null || !scheduledAt.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Choose a future interview date and time.");
        }
        if (locationOrLink == null || locationOrLink.isBlank() || locationOrLink.length() > 500) {
            throw new IllegalArgumentException("Provide a location or meeting link (max 500 characters).");
        }
        if (note != null && note.length() > 1000) {
            throw new IllegalArgumentException("Message must be at most 1000 characters.");
        }

        InterviewInvitation invitation = invitationRepository.save(
                new InterviewInvitation(application, scheduledAt, locationOrLink.trim(), note));
        application.setStatus("Interview");
        applicationRepository.save(application);
        notificationService.interviewScheduled(invitation);
    }
}