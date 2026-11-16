package Sprint1.demo.repository;

import Sprint1.demo.model.SavedJob;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SavedJobRepository
        extends JpaRepository<SavedJob, Long> {

    @EntityGraph(attributePaths = {"jobListing"})
    List<SavedJob> findByUser_EmailOrderByIdDesc(
            String email);

    Optional<SavedJob> findByUser_EmailAndJobListing_Id(
            String email, Long jobId);

    @EntityGraph(attributePaths = {"user", "jobListing"})
    List<SavedJob>
        findByJobListing_DeadlineAndJobListing_DeletedFalse(
                LocalDate deadline);
}