package Sprint1.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Sprint1.demo.model.JobListing;

@Repository
public interface JobRepository extends JpaRepository<JobListing, Long> {
    // Spring Data JPA gives you findAll(), save(), findById(), etc. out of the box!

    // US 3.5 (#37): all job listings posted by one recruiter, newest first.
    // Spring builds the query from the method name: recruiter.email = ?, ordered by postedDate then id (descending).
    List<JobListing> findByRecruiter_EmailOrderByPostedDateDescIdDesc(String email);
}
