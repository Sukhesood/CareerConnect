package Sprint1.demo.service;

import Sprint1.demo.model.*;
import Sprint1.demo.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public SavedJobService(
            SavedJobRepository savedJobRepository,
            UserRepository userRepository,
            JobRepository jobRepository) {

        this.savedJobRepository = savedJobRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    @Transactional
    public void saveJob(String email, Long jobId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new IllegalArgumentException("User not found."));

        if (!"JOB_SEEKER".equals(user.getRole())) {
            throw new IllegalArgumentException(
                    "Only job seekers can save jobs.");
        }

        JobListing job = jobRepository
                .findByIdAndDeletedFalse(jobId)
                .orElseThrow(() ->
                    new IllegalArgumentException("Job not found."));

        boolean alreadySaved = savedJobRepository
                .findByUser_EmailAndJobListing_Id(email, jobId)
                .isPresent();

        if (!alreadySaved) {
            savedJobRepository.save(new SavedJob(user, job));
        }
    }

    @Transactional(readOnly = true)
    public List<SavedJob> getSavedJobs(String email) {
        return savedJobRepository
                .findByUser_EmailOrderByIdDesc(email);
    }

    @Transactional
    public void removeJob(String email, Long jobId) {

        savedJobRepository
                .findByUser_EmailAndJobListing_Id(email, jobId)
                .ifPresent(savedJobRepository::delete);
    }
}