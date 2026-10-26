
package Sprint1.demo.service;

import Sprint1.demo.model.PasswordResetToken;
import Sprint1.demo.model.User;
import Sprint1.demo.repository.PasswordResetTokenRepository;
import Sprint1.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            JavaMailSender mailSender,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createAndSendToken(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        // Reuse an existing token record, if one exists.
        PasswordResetToken resetToken = tokenRepository
                .findByUser(user)
                .orElseGet(PasswordResetToken::new);

        String token = UUID.randomUUID().toString();

        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(15)
        );

        tokenRepository.save(resetToken);

        String resetUrl = baseUrl
                + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(user.getEmail());
        message.setSubject(
                "CareerConnect - Password Reset Request"
        );

        message.setText(
                "Hello,\n\n"
                + "We received a request to reset your "
                + "CareerConnect password.\n\n"
                + "Click the link below to reset your password:\n"
                + resetUrl
                + "\n\nThis link expires in 15 minutes."
                + "\n\nIf you did not request this reset, "
                + "you can ignore this email."
        );

        mailSender.send(message);
    }

    public boolean validatePasswordResetToken(String token) {

        Optional<PasswordResetToken> resetTokenOpt =
                tokenRepository.findByToken(token);

        return resetTokenOpt.isPresent()
                && !resetTokenOpt.get().isExpired();
    }

    @Transactional
    public void updatePassword(String token, String newPassword) {

        PasswordResetToken resetToken = tokenRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid password reset token."
                        )
                );

        if (resetToken.isExpired()) {
            throw new IllegalStateException(
                    "Token has expired."
            );
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must contain at least 6 characters."
            );
        }

        User user = resetToken.getUser();

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);

        tokenRepository.delete(resetToken);
    }
}
