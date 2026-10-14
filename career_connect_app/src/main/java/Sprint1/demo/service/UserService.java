package Sprint1.demo.service;


import Sprint1.demo.model.User;
import Sprint1.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final String UPLOAD_DIR = "uploads/";

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Error: Email is already in use!");
        }
        return userRepository.save(user);
    }

    public boolean authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.getPassword() != null && user.getPassword().equals(password);
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

    public void saveResume(String email, MultipartFile file) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent() && !file.isEmpty()) {
                User user = userOpt.get();
                
                // Create upload directory if it doesn't exist
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Generate unique filename
                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + filename);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                user.setResumeFilename(filename);
                userRepository.save(user);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store resume file.", e);
        }
    }
}