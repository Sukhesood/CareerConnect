
package Sprint1.demo.repository;

import Sprint1.demo.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository
        extends JpaRepository<Resume, Long> {

    List<Resume> findByUser_IdOrderByUploadedAtDesc(
            Long userId
    );

    Optional<Resume> findByIdAndUser_Id(
            Long id, Long userId
    );

    // Count resumes belonging to a user
    long countByUser_Id(Long userId);
}

