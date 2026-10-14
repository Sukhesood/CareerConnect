package Sprint1.demo.service;


import Sprint1.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import Sprint1.demo.model.User;

import java.io.IOException;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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