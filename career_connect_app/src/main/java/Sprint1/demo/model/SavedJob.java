package Sprint1.demo.model;

import jakarta.persistence.*;

@Entity
@Table(
    name = "saved_jobs",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "job_listing_id"}
    )
)
public class SavedJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_listing_id")
    private JobListing jobListing;

    public SavedJob() {}

    public SavedJob(User user, JobListing jobListing) {
        this.user = user;
        this.jobListing = jobListing;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public JobListing getJobListing() {
        return jobListing;
    }
}