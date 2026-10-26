package Sprint1.demo.service;


import Sprint1.demo.model.User;

import Sprint1.demo.repository.UserRepository;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
    }


public User registerUser(User user) {

    // Check whether the email is already registered
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
        throw new RuntimeException("Error: Email is already in use!");
    }

    // Every new user starts as a Job Seeker
    user.setRole("JOB_SEEKER");

    // Encrypt the password before storing it in MySQL
    user.setPassword(
            passwordEncoder.encode(user.getPassword())
    );

    // Save the user in MySQL
    return userRepository.save(user);
}

    public boolean authenticate(
            String email,
            String password) {

        Optional<User> userOpt =
                userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        String storedPassword = user.getPassword();

        if (storedPassword == null || password == null) {
            return false;
        }

        // Authentication for BCrypt passwords
        if (storedPassword.matches(
                "\\$2[aby]\\$\\d{2}\\$.{53}")) {

            return passwordEncoder.matches(
                    password,
                    storedPassword);
        }

        // Temporary migration of older plaintext passwords
        if (storedPassword.equals(password)) {

            user.setPassword(
                    passwordEncoder.encode(password));

            userRepository.save(user);
            return true;
        }

        return false;
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void updateProfile(String email, User updatedDetails) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFirstName(updatedDetails.getFirstName());
            user.setLastName(updatedDetails.getLastName());
            user.setPhoneNumber(updatedDetails.getPhoneNumber());
            user.setAddress(updatedDetails.getAddress());
            userRepository.save(user);
        }
    }

    @Transactional
    public void switchRole(String email, String role) {

        if (!"JOB_SEEKER".equals(role)
                && !"RECRUITER".equals(role)) {

            throw new IllegalArgumentException("Invalid account mode.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found.")
                );

        user.setRole(role);
        userRepository.save(user);
    }


    public void saveResume(String email, MultipartFile file) {

        try {

            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isPresent() && !file.isEmpty()) {

                User user = userOpt.get();

                // Store the original filename
                user.setResumeFilename(file.getOriginalFilename());

                // Store the file type
                user.setResumeContentType(file.getContentType());

                // Store the actual resume
                user.setResumeData(file.getBytes());

                // Save everything to MySQL
                userRepository.save(user);
            }

        } catch (IOException e) {

            throw new RuntimeException("Failed to store resume in database.", e);
        }
    }
    public User getUserByEmail(String email) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        return userOpt.orElse(null);
    }
}