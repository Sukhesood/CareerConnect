package Sprint1.demo.repository;

import Sprint1.demo.model.Notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // All notifications belonging to one user
    List<Notification>
        findByRecipient_EmailOrderByCreatedAtDesc(
                String email
        );

    // Number of unread notifications
    long countByRecipient_EmailAndReadFlagFalse(
            String email
    );

    // Find a notification only if it belongs to this user
    Optional<Notification>
        findByIdAndRecipient_Email(
                Long id,
                String email
        );

    // Prevent duplicate scheduled reminders
    boolean existsByDeduplicationKey(
            String deduplicationKey
    );
}