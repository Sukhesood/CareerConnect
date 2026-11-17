
package Sprint1.demo.repository;

import Sprint1.demo.model.InterviewInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.time.LocalDateTime;

public interface InterviewInvitationRepository
        extends JpaRepository<InterviewInvitation, Long> {

    List<InterviewInvitation>
        findByJobApplication_IdOrderByScheduledAtDesc(
            Long applicationId
        );

    @EntityGraph(attributePaths = {
        "jobApplication",
        "jobApplication.applicant",
        "jobApplication.jobListing",
        "jobApplication.jobListing.recruiter"
    })
    List<InterviewInvitation>
        findByScheduledAtGreaterThanEqualAndScheduledAtLessThan(
            LocalDateTime start,
            LocalDateTime end
        );

    @EntityGraph(attributePaths = {
        "jobApplication",
        "jobApplication.jobListing"
    })
    List<InterviewInvitation>
        findByJobApplication_Applicant_EmailOrderByScheduledAtDesc(
            String email
        );
}
