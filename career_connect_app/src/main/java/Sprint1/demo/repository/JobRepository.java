package Sprint1.demo.repository;

import java.util.List;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Sprint1.demo.model.JobListing;

@Repository
public interface JobRepository extends JpaRepository<JobListing, Long> {
    // Spring Data JPA gives you findAll(), save(), findById(), etc. out of the box!

//Asking the data base
    @Query("""
             SELECT j FROM JobListing j 
            WHERE (:keyword = '' 
                   OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(j.skills) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:location = '' OR LOWER(j.location) = LOWER(:location))
              AND (:jobType = '' OR LOWER(j.jobType) = LOWER(:jobType))
            ORDER BY j.postedDate DESC, j.id DESC
            """)

   List<JobListing> search(@Param("keyword") String keyword,
                            @Param("location") String location,
                            @Param("jobType") String jobType);

    @Query("SELECT DISTINCT j.location FROM JobListing j WHERE j.location IS NOT NULL AND j.location <> '' ORDER BY j.location")
    List<String> findDistinctLocations();

    @Query("SELECT DISTINCT j.jobType FROM JobListing j WHERE j.jobType IS NOT NULL AND j.jobType <> '' ORDER BY j.jobType")
    List<String> findDistinctJobTypes();

    // US 3.5 (#37): all job listings posted by one recruiter, newest first.
    // Spring builds the query from the method name: recruiter.email = ?, ordered by
    // postedDate then id (descending).
    List<JobListing> findByRecruiter_EmailOrderByPostedDateDescIdDesc(String email);

    // US 3.3 (#35): find a job listing only if it belongs to this recruiter (used
    // for editing)
    Optional<JobListing> findByIdAndRecruiter_Email(Long id, String email);

    @Query("SELECT job FROM JobListing job WHERE NOT EXISTS (SELECT application FROM JobApplication application WHERE application.jobListing = job AND application.applicant.email = :email) ORDER BY job.postedDate DESC, job.id DESC")
    List<JobListing> findAvailableForApplicant(@Param("email") String email);
}
