package Sprint1.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_invitations")
public class InterviewInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    @NotNull
    @Future
    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @NotBlank
    @Column(nullable = false, length = 500)
    private String locationOrLink;

    @Column(length = 1000)
    private String note;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected InterviewInvitation() { }

    public InterviewInvitation(JobApplication application, LocalDateTime scheduledAt,
                               String locationOrLink, String note) {
        this.jobApplication = application;
        this.scheduledAt = scheduledAt;
        this.locationOrLink = locationOrLink;
        this.note = note;
    }

    @PrePersist
    private void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public JobApplication getJobApplication() { return jobApplication; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public String getLocationOrLink() { return locationOrLink; }
    public String getNote() { return note; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}