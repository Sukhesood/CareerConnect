package Sprint1.demo.repository;

import Sprint1.demo.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByApplicant_EmailOrderBySubmittedAtDesc(String email);
}
