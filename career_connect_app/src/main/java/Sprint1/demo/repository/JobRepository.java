package Sprint1.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Sprint1.demo.model.JobListing;

@Repository
public interface JobRepository extends JpaRepository<JobListing, Long> {
    // Spring Data JPA gives you findAll(), save(), findById(), etc. out of the box!
}