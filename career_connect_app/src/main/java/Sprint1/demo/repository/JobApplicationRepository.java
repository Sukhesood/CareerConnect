package Sprint1.demo.repository;

import Sprint1.demo.model.JobApplication;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    @EntityGraph(attributePaths = {"applicant", "jobListing"})
    List<JobApplication> findByApplicant_EmailOrderBySubmittedAtDesc(String email);
    boolean existsByApplicant_EmailAndJobListing_Id(String email, Long jobListingId);
    @EntityGraph(attributePaths = {"applicant", "jobListing"})
    List<JobApplication> findByJobListing_Recruiter_EmailOrderBySubmittedAtDesc(String email);
    @EntityGraph(attributePaths = {"applicant", "jobListing"})
    Optional<JobApplication> findByIdAndJobListing_Recruiter_Email(Long id, String email);
}
