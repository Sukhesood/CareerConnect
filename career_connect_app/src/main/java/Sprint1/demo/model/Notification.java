package Sprint1.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who receives this notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "read_flag", nullable = false)
    private boolean readFlag = false;

    private LocalDateTime createdAt;

    private String targetUrl;

    // Used to avoid duplicate reminders
    @Column(unique = true)
    private String deduplicationKey;

    public Notification() {
    }

    public Notification(
            User recipient,
            NotificationType type,
            String message,
            String targetUrl,
            String deduplicationKey) {

        this.recipient = recipient;
        this.type = type;
        this.message = message;
        this.targetUrl = targetUrl;
        this.deduplicationKey = deduplicationKey;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public User getRecipient() {
        return recipient;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public boolean isReadFlag() {
        return readFlag;
    }

    public void setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public String getDeduplicationKey() {
        return deduplicationKey;
    }
}