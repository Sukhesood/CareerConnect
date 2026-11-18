package Sprint1.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/** Optional email delivery; failure does not undo the saved in-app notification. */
@Service
public class NotificationEmailService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationEmailService.class);
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean enabled;
    private final String from;

    public NotificationEmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                                    @Value("${app.notifications.mail-enabled:false}") boolean enabled,
                                    @Value("${app.notifications.from:}") String from) {
        this.mailSenderProvider = mailSenderProvider;
        this.enabled = enabled;
        this.from = from;
    }

    @Async
    public void send(String recipientEmail, String subject, String body) {
        if (!enabled) return;
        JavaMailSender sender = mailSenderProvider.getIfAvailable();
        if (sender == null || from == null || from.isBlank()) {
            LOG.warn("Notification email is enabled but SMTP sender or app.notifications.from is missing");
            return;
        }
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(from);
            mail.setTo(recipientEmail);
            mail.setSubject("CareerConnect: " + subject);
            mail.setText(body + "\n\nSign in to CareerConnect to view this notification.");
            sender.send(mail);
        } catch (MailException ex) {
            LOG.warn("Could not send notification email to {}: {}", recipientEmail, ex.getMessage());
        }
    }
}