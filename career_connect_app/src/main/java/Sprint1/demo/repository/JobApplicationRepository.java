package Sprint1.demo.repository;

import Sprint1.demo.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByApplicant_EmailOrderBySubmittedAtDesc(String email);
    List<JobApplication> findByJobListing_Recruiter_EmailOrderBySubmittedAtDesc(String email);
    Optional<JobApplication> findByIdAndJobListing_Recruiter_Email(Long id, String email);
}
